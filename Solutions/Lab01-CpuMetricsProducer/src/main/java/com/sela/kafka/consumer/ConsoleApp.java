package com.sela.kafka.consumer;

import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.sela.kafka.common.CpuMetricMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by eyalbenivri on 07/12/2016.
 */
public class ConsoleApp {
    public static void main(String[] args) throws IOException {
        final Gson gson = new Gson();
        Properties properties = new Properties();
        InputStream props = Resources.getResource("consumer.properties").openStream();
        properties.load(props);
        final KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Arrays.asList("cpuMetrics"));
        Timer t = new Timer();
        final CpuCounter counter = new CpuCounter(20);
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                ConsumerRecords<String, String> records = consumer.poll(500);
                for (ConsumerRecord<String, String> record : records) {
                    CpuMetricMessage msg = gson.fromJson(record.value(), CpuMetricMessage.class);
                    System.out.format("Got 1 message from mac address %s, sent %.3f seconds ago%n", msg.getMacAddress(), (System.nanoTime() - msg.getNanoTime()) / 1000000000.0);
                    if(counter.addAndCheckPeriod(msg)) {
                        printStats();
                    }
                }
            }

            private void printStats() {
                ArrayList<Double> cpuAvgs = counter.getCpuAvrages();
                ArrayList<Double> memAvgs = counter.getMemAvrages();
                System.out.format("Per.\tCpu\tMem%n");
                for (int i = 0; i < cpuAvgs.size(); i++) {
                    System.out.format("%d\t%.3f\t%.3fMB%n", i, cpuAvgs.get(i), memAvgs.get(i) / (1024^2));
                }
                System.out.println();
                System.out.format("Global CPU usage: %.3f, Global Mem usage: %.3fMB", counter.getGlobalCpuUsageAvg(), counter.getGlobalFreeMemAvg());

            }
        }, 0, 1000);

    }


}

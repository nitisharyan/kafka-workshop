package com.sela.kafka.producer;

import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.sela.kafka.common.CpuMetricMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by eyalbenivri on 21/11/2016.
 */
public class ConsoleApp {
    public static void main(String[] args) throws IOException {
        final UsageSampler sampler = new UsageSampler();
        final Gson gson = new Gson();
        InputStream props = Resources.getResource("producer.properties").openStream();
        Properties properties = new Properties();
        properties.load(props);
        final KafkaSender sender = new KafkaSender(properties);

        Timer t = new Timer();
        t.schedule(new TimerTask() {

            @Override
            public void run() {
                CpuMetricMessage msg = new CpuMetricMessage(sampler);
                try {
                    sender.sendMessageToKafka(msg);
                    String msg_json = gson.toJson(msg);
                    System.out.println("Send message to Kafka. msg content: " + msg_json);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }, 0, 500);

    }
}

package com.sela.kafka;

import com.google.common.io.Resources;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by eyalbenivri on 21/11/2016.
 *
 * To start this process you must first start ZooKeeper and Kafka
 * To start them locally, enter from the command prompt/terminal the following commands:
 *
 * $KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties
 * $KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties
 *
 */
public class ConsoleApp {
    public static void main(String[] args) throws IOException {
        final UsageSampler sampler = new UsageSampler();
        final Gson gson = new Gson();
        final String macAddress = sampler.getMacAdress();
        InputStream props = Resources.getResource("producer.properties").openStream();
        Properties properties = new Properties();
        properties.load(props);
        final KafkaSender sender = new KafkaSender(properties);

        Timer t = new Timer();
        t.schedule(new TimerTask() {

            @Override
            public void run() {
                CpuMetricMessage msg = new CpuMetricMessage(sampler, macAddress);
                try {
                    sender.sendMessageToKafka(msg);
                    String msg_json = gson.toJson(msg);
                    System.out.println("Send message to Kafka. msg content: " + msg_json);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }, 0, 5000);

    }
}

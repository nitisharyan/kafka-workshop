package com.sela.kafka;

import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * Created by eyalbenivri on 24/11/2016.
 */
public class KafkaSender {

    KafkaProducer<String, String> producer;
    private final String TOPIC = "cpuMetrics";

    Gson gson;
    public KafkaSender(Properties properties) {
        producer = new KafkaProducer<String, String>(properties);
        gson = new Gson();
    }

    public void sendMessageToKafka(CpuMetricMessage msg) throws InterruptedException {
        String msg_json = gson.toJson(msg);
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(TOPIC, calcPartition(msg.getMacAddress()), String.format("%s-%d%n", msg.getMacAddress(), msg.getNanoTime()), msg_json);
        producer.send(record);
    }

    private Integer calcPartition(String macAddress) {
        int numberOfParitions = producer.partitionsFor(TOPIC).size();
        int macAddressHash = macAddress.hashCode();
        return macAddressHash % numberOfParitions;
    }
}

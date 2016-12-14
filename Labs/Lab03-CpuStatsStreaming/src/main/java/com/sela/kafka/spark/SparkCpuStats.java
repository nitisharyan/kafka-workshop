package com.sela.kafka.spark;

import com.sela.kafka.common.CpuMetricMessage;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Seconds;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sela on 12/9/16.
 */
public class SparkCpuStats {
    public static void main(String[] args) throws InterruptedException {
        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", "localhost:9092");
        // you probably need to set more properties here...

        SparkConf conf = new SparkConf(true).setMaster("local");
        conf.setAppName("spark_cpustats_java");
        JavaSparkContext jsc = new JavaSparkContext(conf);
        jsc.setLogLevel("WARN");
        JavaStreamingContext streamingContext = new JavaStreamingContext(jsc, Seconds.apply(10));

        // define topics to pass into the KafkaUtils method
        final JavaInputDStream<ConsumerRecord<String, String>> stream = KafkaUtils
                .createDirectStream(streamingContext, LocationStrategies.PreferConsistent(), ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams));


        // implelment code to parse data from kafka,
        // map from each message the string represnetation, parse to object using CpuMetricMessage
        // calculate every 30 seconds window a avrage of cpuUsage and freeMemory, per mac address


        streamingContext.start();
        streamingContext.awaitTermination();
    }


}

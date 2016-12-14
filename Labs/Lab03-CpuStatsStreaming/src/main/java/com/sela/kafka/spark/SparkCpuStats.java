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
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", "spark_cpustats_java");
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);

        Collection<String> topics = Arrays.asList("cpuMetrics");

        SparkConf conf = new SparkConf(true).setMaster("local");
        conf.setAppName("spark_cpustats_java");
        JavaSparkContext jsc = new JavaSparkContext(conf);
        jsc.setLogLevel("WARN");
        JavaStreamingContext streamingContext = new JavaStreamingContext(jsc, Seconds.apply(10));

        final JavaInputDStream<ConsumerRecord<String, String>> stream = KafkaUtils
                .createDirectStream(streamingContext, LocationStrategies.PreferConsistent(), ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams));
        JavaPairDStream<String, CpuMetricMessage> logs = stream
                .map(cr -> CpuMetricMessage.createInstance(cr.value()))
                .mapToPair(cpuMsg -> new Tuple2<>(cpuMsg.getMacAddress(), cpuMsg));
        JavaPairDStream<String, Tuple2<Double, Integer>> cpuLogStream = logs.mapValues(cpu -> new Tuple2<>(cpu.getCpuUsage(), 1))
                .reduceByKeyAndWindow((cpu1, cpu2) -> new Tuple2(cpu1._1 + cpu2._1, cpu1._2 + cpu2._2), Seconds.apply(30));
        cpuLogStream.mapValues(cpu -> cpu._1 / cpu._2).print();

        JavaPairDStream<String, Tuple2<Long, Integer>> memLogStream = logs.mapValues(cpu -> new Tuple2<>(cpu.getFreeMemory()/(1024^2), 1))
                .reduceByKeyAndWindow((cpu1, cpu2) -> new Tuple2(cpu1._1 + cpu2._1, cpu1._2 + cpu2._2), Seconds.apply(30));
        memLogStream.mapValues(stats -> (double)stats._1 / stats._2).print();

        streamingContext.start();
        streamingContext.awaitTermination();
    }


}

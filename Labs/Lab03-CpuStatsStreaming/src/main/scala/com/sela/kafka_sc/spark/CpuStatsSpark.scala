package com.sela.kafka_sc.spark

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable

/**
  * Created by sela on 12/14/16.
  */
object CpuStatsSpark {
  def main(args: Array[String]): Unit = {
    val kafkaParams2 = new mutable.HashMap[String, String]
    kafkaParams2.put("bootstrap.servers", "localhost:9092")
    // you probably need to set some more properties

    val conf = new SparkConf().setAppName("spark_cpustats_scala").setMaster("local")
    val streaming = new StreamingContext(conf, Seconds(10))
    streaming.sparkContext.setLogLevel("WARN")

    // define topics to pass to the KafkaUtils method
    val inStream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream(streaming, LocationStrategies.PreferConsistent, ConsumerStrategies.Subscribe[String, String](topics, kafkaParams2))

    // implelment code to parse data from kafka,
    // map from each message the string represnetation, parse to object using CpuMetricMessage
    // calculate every 30 seconds window a avrage of cpuUsage and freeMemory, per mac address

    streaming.start()
    streaming.awaitTermination()
  }
}

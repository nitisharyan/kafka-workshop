package com.sela.kafka_sc.spark

import java.sql.Timestamp

import com.sela.kafka_sc.common.CpuMetricMessage
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.sql.catalyst.util.DateTimeUtils
import org.apache.spark.sql.types.{DateType, IntegerType}
import org.apache.spark.sql.{DataFrame, SparkSession, functions}
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.util.TimeStampedValue

import scala.collection.mutable

/**
  * Created by sela on 12/14/16.
  */
object CpuStatsSparkScala {
  def main(args: Array[String]): Unit = {
    val kafkaParams2 = new mutable.HashMap[String, String]
    kafkaParams2.put("bootstrap.servers", "localhost:9092")
    kafkaParams2.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaParams2.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaParams2.put("group.id", "spark_cpustats_scala")
    kafkaParams2.put("auto.offset.reset", "latest")
    kafkaParams2.put("enable.auto.commit", "false")
    kafkaParams2.put("subscribe", "cpuMetrics")

    val conf = new SparkConf().setAppName("spark_cpustats_scala").setMaster("local")
    val streaming = new StreamingContext(conf, Seconds(10))
    streaming.sparkContext.setLogLevel("WARN")

    val topics = Array[String]("cpuMetrics")

    val inStream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream(streaming, LocationStrategies.PreferConsistent, ConsumerStrategies.Subscribe[String, String](topics, kafkaParams2))
    val pairLogs = inStream.map(x => CpuMetricMessage.apply(x.value())).map(x => (x.macAddress, x))
    pairLogs.mapValues(x => (x.cpuUsage, x.freeMemory, 1))
      .reduceByKeyAndWindow((x, y) => (x._1 + y._1, x._2 + y._2, x._3 + y._3), Seconds(30))
      .mapValues(x => (x._1 / x._3, (x._2.toDouble / (1024 ^ 2)) / x._3))
      .print()

    streaming.start()
    streaming.awaitTermination()
  }
}

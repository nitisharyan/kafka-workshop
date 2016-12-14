package com.sela.kafka_sc.flink

import java.util.Properties

import com.sela.kafka_sc.common.CpuMetricMessage
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import org.apache.flink.streaming.util.serialization.SimpleStringSchema

import scala.collection.mutable

/**
  * Created by sela on 12/14/16.
  */
object CpuStatsFlink {
  def main(args: Array[String]): Unit = {
    val kafkaParams2 = new Properties()
    kafkaParams2.setProperty("kafka.bootstrap.servers", "localhost:9092")
    kafkaParams2.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaParams2.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaParams2.setProperty("group.id", "spark_cpustats_java_strctured")
    kafkaParams2.setProperty("auto.offset.reset", "latest")
    kafkaParams2.setProperty("enable.auto.commit", "false")
    kafkaParams2.setProperty("subscribe", "cpuMetrics")

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val text = env.addSource(new FlinkKafkaConsumer010[String]("cpuMetrics",new SimpleStringSchema(), kafkaParams2))
    text.print()

  }
}

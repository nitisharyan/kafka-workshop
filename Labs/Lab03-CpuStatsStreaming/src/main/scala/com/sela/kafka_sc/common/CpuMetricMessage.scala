package com.sela.kafka_sc.common

import com.google.gson.Gson

/**
  * Created by sela on 12/14/16.
  */
case class CpuMetricMessage(cpuUsage:Double, freeMemory:Long, macAddress:String, nanoTime:Long)


object CpuMetricMessage {
  def apply(jsonStr:String): CpuMetricMessage = {
    val gson = new Gson()
    gson.fromJson(jsonStr, classOf[CpuMetricMessage])
  }
}

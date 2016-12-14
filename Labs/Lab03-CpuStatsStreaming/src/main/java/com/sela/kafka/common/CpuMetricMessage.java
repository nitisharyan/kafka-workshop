package com.sela.kafka.common;

import com.google.gson.Gson;

/**
 * Created by sela on 12/12/16.
 */
public class CpuMetricMessage {
//
//    public com.sela.kafka.common.CpuMetricMessage(UsageSampler sampler, String macAddress) {
//        this.cpuUsage = sampler.getCpuUsage();
//        this.freeMemory = sampler.getFreeMemory();
//        this.nanoTime = System.nanoTime();
//        this.macAddress = macAddress;
//    }

    public static CpuMetricMessage createInstance(String jsonStr) {
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, CpuMetricMessage.class);
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public long getNanoTime() {
        return nanoTime;
    }

    public String getMacAddress() {
        return macAddress;
    }

    private double cpuUsage;
    private long freeMemory;
    private long nanoTime;
    private String macAddress;
}


package com.sela.kafka.common;

import com.sela.kafka.producer.UsageSampler;

public class CpuMetricMessage {

    public CpuMetricMessage(UsageSampler sampler) {
        this.cpuUsage = sampler.getCpuUsage();
        this.freeMemory = sampler.getFreeMemory();
        this.nanoTime = System.nanoTime();
        this.macAddress = sampler.getMacAddress();
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

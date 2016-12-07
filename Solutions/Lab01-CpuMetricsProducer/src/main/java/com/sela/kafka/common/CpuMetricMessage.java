package com.sela.kafka.common;

import com.sela.kafka.producer.UsageSampler;

/**
 * Created by eyalbenivri on 21/11/2016.
 */
public class CpuMetricMessage {

    public CpuMetricMessage(UsageSampler sampler, String macAddress) {
        this.cpuUsage = sampler.getCpuUsage();
        this.freeMemory = sampler.getFreeMemory();
        this.nanoTime = System.nanoTime();
        this.macAddress = macAddress;
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

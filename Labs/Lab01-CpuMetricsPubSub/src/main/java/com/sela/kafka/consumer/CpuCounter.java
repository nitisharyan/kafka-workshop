package com.sela.kafka.consumer;

import com.sela.kafka.common.CpuMetricMessage;

import java.util.ArrayList;

/**
 * Created by eyalbenivri on 07/12/2016.
 */
public class CpuCounter {
    private long globalMsgCounter = 0;
    private long periodMsgCounter = 0;
    private double globalCpuUsageCounter = 0;
    private double periodCpuUsageCounter = 0;
    private double globalFreeMemCounter = 0;
    private double periodFreeMemCounter = 0;
    final private int periodSize;

    public ArrayList<Double> getCpuAvrages() {
        return cpuAvrages;
    }

    public ArrayList<Double> getMemAvrages() {
        return memAvrages;
    }

    public double getGlobalCpuUsageAvg() {
        return globalCpuUsageCounter / globalMsgCounter;
    }

    public double getGlobalFreeMemAvg() {
        return globalFreeMemCounter / globalMsgCounter;
    }

    private ArrayList<Double> cpuAvrages = new ArrayList<Double>();
    private ArrayList<Double> memAvrages = new ArrayList<Double>();

    public CpuCounter(int periodSize) {
        this.periodSize = periodSize;
    }

    public boolean addAndCheckPeriod(CpuMetricMessage msg) {
        ++globalCpuUsageCounter;
        globalCpuUsageCounter += msg.getCpuUsage();
        globalFreeMemCounter += msg.getFreeMemory();

        ++periodMsgCounter;
        periodCpuUsageCounter += msg.getCpuUsage();
        periodFreeMemCounter += msg.getFreeMemory();
        if(periodMsgCounter >= periodSize) {
            cpuAvrages.add(periodCpuUsageCounter / periodMsgCounter);
            memAvrages.add(periodFreeMemCounter / periodMsgCounter);

            periodMsgCounter = 0;
            periodCpuUsageCounter = 0;
            periodFreeMemCounter = 0;
            return true;
        }
        return false;
    }
}

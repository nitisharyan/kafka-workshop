package com.sela.kafka.producer;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * Created by eyalbenivri on 21/11/2016.
 */
public class UsageSampler {

    final static String[] macs = {
            "00-00-00-00-00-00-00-00-00-00-00-00-00-00-00-01",
            "00-00-00-00-00-00-00-00-00-00-00-00-00-00-00-02",
            "00-00-00-00-00-00-00-00-00-00-00-00-00-00-00-03",
            "00-00-00-00-00-00-00-00-00-00-00-00-00-00-00-04",
            "00-00-00-00-00-00-00-00-00-00-00-00-00-00-00-05"
    };

    static OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();

    final static Random random = new Random();

    public double getCpuUsage() {
        double cpu = operatingSystemMXBean.getProcessCpuLoad();
        if(random.nextDouble() > 0.8) {
            cpu *= 3;
        }
        return cpu;
    }

    public long getFreeMemory() {
        long mem = operatingSystemMXBean.getFreePhysicalMemorySize();
        if(random.nextDouble() > 0.8) {
            mem /= 3;
        }
        return mem;
    }

    public String getMacAddress() {
        return macs[random.nextInt(macs.length)];
    }

    private static String getRealMacAddress() {
        String macAddress = null;
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getInetAddresses().nextElement().getAddress();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            macAddress = sb.toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(2);
        }
        return macAddress;
    }
}

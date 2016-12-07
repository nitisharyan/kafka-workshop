package com.sela.kafka.producer;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by eyalbenivri on 21/11/2016.
 */
public class UsageSampler {

    static OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
    static String macAddress = null;

    public double getCpuUsage() {
        return operatingSystemMXBean.getProcessCpuLoad();
    }

    public long getFreeMemory() {
        return operatingSystemMXBean.getFreePhysicalMemorySize();
    }

    public String getMacAdress() {
        if(macAddress == null) {
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

        }
        return macAddress;
    }
}

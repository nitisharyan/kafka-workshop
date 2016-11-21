package com.sela.kafka;

import com.google.gson.Gson;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by eyalbenivri on 21/11/2016.
 */
public class ConsoleApp {
    public static void main(String[] args) {
        final UsageSampler sampler = new UsageSampler();
        final Gson gson = new Gson();
        final String macAddress = sampler.getMacAdress();


        Timer t = new Timer();
        t.schedule(new TimerTask() {

            @Override
            public void run() {
                CpuMetricMessage msg = new CpuMetricMessage(sampler, macAddress);
                String msg_json = gson.toJson(msg);
                // encode and send msg to Kafka
                System.out.println("Send message to Kafka. msg content: " + msg_json);
            }
        }, 0, 5000);

    }
}

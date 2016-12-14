package com.sela.kafka.producer;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ConsoleApp {
    public static void main(String[] args) throws IOException {


        final UsageSampler sampler = new UsageSampler();
        final Gson gson = new Gson();


        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                // implelment this method and use sampler to get data, and send data to a 'cpuMetrics' topic in Kafka
            }
        }, 0, 500);

    }
}

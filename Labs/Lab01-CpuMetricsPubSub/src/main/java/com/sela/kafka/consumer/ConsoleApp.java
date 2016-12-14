package com.sela.kafka.consumer;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by eyalbenivri on 07/12/2016.
 */
public class ConsoleApp {

    public static void main(String[] args) throws IOException {
        final Gson gson = new Gson();

        Timer t = new Timer();
        final CpuCounter counter = new CpuCounter(20);
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                // each run you should poll for new messages from kafka topic and gather statitics from cpu samples
                // use the counter instance to help you gather statitics about the data coming in
            }
        }, 0, 1000);

    }


}

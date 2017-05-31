package com.billy.controller.lib.processors;

import com.billy.controller.lib.core.AbstractMessageProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author billy.qi
 * @since 17/5/31 15:01
 */
public class LogMessageProcessor extends AbstractMessageProcessor {

    @Override
    public void onMessage(String message) {

    }

    @Override
    public String getKey() {
        return "log";
    }

    @Override
    public void onConnectionStart() {
        new LogThread().start();
    }

    @Override
    public void onConnectionStop() {
    }

    private class LogThread extends Thread {
        @Override
        public void run() {
            Process pro = null;
            try {
                Runtime.getRuntime().exec("logcat -c").waitFor();
                pro = Runtime.getRuntime().exec("logcat");
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }

            if (pro == null) {
                if (isRunning()) {
                    sendMessage("get_logcat_failed");
                }
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line;
            while (isRunning()) {
                try {
                    while (isRunning() && (line = reader.readLine()) != null) {
                        sendMessage(line);
                        Thread.yield();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
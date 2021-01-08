package com.cordova.mynfccontrollersample.nfc.services;

import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
import android.util.Log;

public class TimeOutNfcController {
    private Timer timer;
    private TimerTask timerTask;
    private long timeToWait;
    private final Handler handler;

    public TimeOutNfcController(long timeToWait) {
        this.timer = new Timer();
        this.handler = new Handler();
        this.timeToWait = timeToWait;
    }

    public void start() {
        initTime();
        timer.schedule(timerTask, timeToWait);
    }

    private void initTime() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    Log.d(TimeOutNfcController.class.getSimpleName(), "run: Finish");
                });
            }
        };
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}

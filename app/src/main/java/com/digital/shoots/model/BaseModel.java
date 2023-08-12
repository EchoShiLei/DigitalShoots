package com.digital.shoots.model;

import com.digital.shoots.ble.BleDeviceControl;

import java.util.Timer;
import java.util.TimerTask;

public abstract class BaseModel {
    public static long TIME_PERIOD = 10;
    BleDeviceControl bleDeviceControl;
    ModelCallback callback;
    Timer timer;

    long time = 0;

    public BaseModel(BleDeviceControl bleDeviceControl, ModelCallback callback) {
        this.bleDeviceControl = bleDeviceControl;
        this.callback = callback;
        timer = new Timer();
    }

    public void start() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                doTime();
                callback.countdownTime(time);
            }

        },0, TIME_PERIOD);
    }

    public void end(){
        timer.cancel();
    }

    abstract void doTime();

    public interface ModelCallback {
        void countdownTime(long time);
    }
}

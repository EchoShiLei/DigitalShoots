package com.digital.shoots.model;

import com.digital.shoots.ble.BleDataUtils;
import com.digital.shoots.ble.BleDeviceControl;
import com.digital.shoots.ble.BleDeviceControl.DataCallback;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public abstract class BaseModel {
    private ModelState state = ModelState.IDLE;
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

    public abstract void start();

    public abstract void ready();

    public abstract void run();

    public void end() {
        timer.cancel();
        bleDeviceControl.writeBle(BleDataUtils.closeAllLight());
    }


    public void startTime() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                doTime();
                callback.countdownTime(time);
            }

        }, 0, TIME_PERIOD);
        callback.countdownTime(time);
    }

    abstract void doTime();

    public abstract void onData(byte[] data);

    public interface ModelCallback {
        void countdownTime(long time);
    }

    public enum ModelType {
        NOVICE, JUNIOR, BATTLE;
    }

    public void sendMsg(byte[] data) {
        bleDeviceControl.writeBle(data);
    }

    enum ModelState {
        IDLE,
        READY,
        RUN,
        END
    }
}

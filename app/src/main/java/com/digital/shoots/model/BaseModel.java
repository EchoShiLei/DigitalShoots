package com.digital.shoots.model;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.digital.shoots.ble.BleDataUtils;
import com.digital.shoots.ble.BleDeviceControl;

import java.util.Timer;
import java.util.TimerTask;

import static com.digital.shoots.utils.BaseConstant.MCU_CMD_LED_HEART;
import static com.digital.shoots.utils.BaseConstant.MCU_CMD_LED_HIT;

public abstract class BaseModel {
    private static final String TAG = "BaseModel";
    private ModelState state = ModelState.IDLE;
    public static long TIME_PERIOD = 10;

    BleDeviceControl bleDeviceControl;
    HandlerThread handlerThread = new HandlerThread("BaseModel");
    Handler handler;
    ModelCallback callback;
    Timer timer;

    long time = 0;

    public BaseModel(BleDeviceControl bleDeviceControl, ModelCallback callback) {
        this.bleDeviceControl = bleDeviceControl;
        this.callback = callback;
        init();
    }

    public void init() {
        Log.d("BaseModel", "init");
        timer = new Timer();
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        if (!BleDataUtils.isOnline) {
            return;
        }
        bleDeviceControl.writeBle(BleDataUtils.openAllBlueLight());
        state = ModelState.READY;
    }

    //展示3s倒计时
    public void show3sCountdown() {
        Log.d("BaseModel", "show3sCountdown");
        handler.postDelayed(() -> {
            bleDeviceControl.writeBle(BleDataUtils.showNumber(3));

            handler.postDelayed(() -> {
                bleDeviceControl.writeBle(BleDataUtils.showNumber(2));

                handler.postDelayed(() -> {
                    bleDeviceControl.writeBle(BleDataUtils.showNumber(1));
                    handler.postDelayed(() -> {
                        Log.d("BaseModel", "start");
                        start();
                    }, 1000);
                }, 1000);
            }, 1000);
        }, 1000);

    }

    public abstract void start();

    public abstract void ready();

    public abstract void run();

    public void end() {
        timer.cancel();
        handler.removeCallbacksAndMessages(null);
        handlerThread.quitSafely();
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

    public void onCmdData(String cmd, byte data) {
        switch (cmd) {
            case MCU_CMD_LED_HEART:
                // 心跳
                bleDeviceControl.writeBle(BleDataUtils.heartBeatResponseData());
                break;
            case MCU_CMD_LED_HIT:
                //
                if (state == ModelState.READY) {
                    state = ModelState.RUN;
                    show3sCountdown();
                    return;
                }

                ledHit(data);
                break;

        }

    }

    public void ledHit(byte data) {

    }

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

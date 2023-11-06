package com.digital.shoots.model;

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

    public void onData(byte[] datas) {
        StringBuilder msg = new StringBuilder("data size:" + datas.length + " ;data: ");
        for (byte b : datas) {
            msg.append(BleDataUtils.byte2HexStr(b)).append(" ");
        }
        Log.d(TAG, msg.toString());

        if (datas.length != 5) {
            return;
        }
        String cmd = BleDataUtils.byte2HexStr(datas[2]);
        byte data = datas[3];
        onCmdData(cmd, data);
    }

    public void onCmdData(String cmd, byte data) {
        switch (cmd) {
            case MCU_CMD_LED_HEART:
                // 心跳
                break;
            case MCU_CMD_LED_HIT:
                //
                ledHit(data);
                break;

        }

    }

    abstract void ledHit(byte data);

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

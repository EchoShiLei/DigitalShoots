package com.digital.shoots.model;

import com.digital.shoots.ble.BleDataUtils;
import com.digital.shoots.ble.BleDeviceControl;
import com.digital.shoots.ble.BleDeviceControl.DataCallback;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public abstract class BaseModel {
    public static final int BLEControlStatuDefault = 0;
    public static final int BLEControlStatuRedControl = 2;// 红灯控制
    public static final int BLEControlStatuBlueControl = 4;// 蓝灯控制确认
    public static final int BLEControlStatuCountDownControl = 8;// 倒计时控制确认
    public static final int BLEControlStatuAutoReport = 10;// 目标信息主动上报
    public static final int BLEControlStatuAutoReportBegain = 12;// 目标信息主动上报；(击中任意标靶开始工作)
    public static final int BLEControlStatuCountDownResidue = 14;// 倒计时剩余字段 信息主动上报
    public static final int BLEControlStatuOnPower = 20;// 开关机状态
    public static final int BLEControlStatuOnLineStatu = 22;// APP上下线
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

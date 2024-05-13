package com.digital.shoots.model;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;

import com.digital.shoots.ble.BleDataUtils;
import com.digital.shoots.ble.BleDeviceControl;
import com.digital.shoots.db.greendao.GreenDaoManager;
import com.digital.shoots.db.greendao.bean.GameAchievement;
import com.digital.shoots.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.digital.shoots.model.BaseModel.ModelType.BATTLE;
import static com.digital.shoots.utils.BaseConstant.MCU_CMD_LED_HEART;
import static com.digital.shoots.utils.BaseConstant.MCU_CMD_LED_HIT;

public abstract class BaseModel {
    private static final String TAG = "BaseModel";
    private ModelState state = ModelState.IDLE;
    public static long TIME_PERIOD = 10;
    public static long OUT_TIME = 4500;

    BleDeviceControl bleDeviceControl;
    HandlerThread handlerThread = new HandlerThread("BaseModel");
    Handler handler;
    ModelCallback callback;
    Timer timer;

    long time = 0;

    int redScore = 0;
    int blueScore = 0;
    int maxSpeed = 0;

    String videoPath = "";

    ModelType type;

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
            callback.cutDown("3");
            handler.postDelayed(() -> {
                bleDeviceControl.writeBle(BleDataUtils.showNumber(2));
                callback.cutDown("2");
                handler.postDelayed(() -> {
                    bleDeviceControl.writeBle(BleDataUtils.showNumber(1));
                    callback.cutDown("1");
                    handler.postDelayed(() -> {
                        Log.d("BaseModel", "start");
                        bleDeviceControl.writeBle(BleDataUtils.showNumber(0));
                        callback.cutDown("GO");
                        startTime();
                        start();
                    }, 1000);
                }, 1000);
            }, 1000);
        }, 1000);

    }

    public synchronized void start() {

    }

    public abstract void ready();

    public synchronized void run() {

    }

    public void end() {
        try {
            timer.cancel();
            handler.removeCallbacksAndMessages(null);
            // 蓝灯闪烁三次
            handler.postDelayed(() -> {
                bleDeviceControl.writeBle(BleDataUtils.openAllBlueLight());
                handler.postDelayed(() -> {
                    bleDeviceControl.writeBle(BleDataUtils.closeAllBlueLight());
                    handler.postDelayed(() -> {
                        bleDeviceControl.writeBle(BleDataUtils.openAllBlueLight());
                        handler.postDelayed(() -> {
                            bleDeviceControl.writeBle(BleDataUtils.closeAllBlueLight());
                            handler.postDelayed(() -> {
                                bleDeviceControl.writeBle(BleDataUtils.openAllBlueLight());
                                handler.postDelayed(() -> {
                                    bleDeviceControl.writeBle(BleDataUtils.closeAllBlueLight());
                                    handlerThread.quitSafely();
                                }, 100);
                            }, 100);
                        }, 100);
                    }, 100);
                }, 100);
            }, 100);


            ToastUtils.showToast("end!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    abstract void saveToDB();

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

    synchronized void doTime() {

    }

    public void onCmdData(String cmd, byte data) {
        switch (cmd) {
            case MCU_CMD_LED_HEART:
                // 心跳
                bleDeviceControl.realWriteBle(BleDataUtils.heartBeatResponseData());
                break;
            case MCU_CMD_LED_HIT:
                //
                if (state == ModelState.READY) {
                    state = ModelState.RUN;
                    bleDeviceControl.writeBle(BleDataUtils.closeAllBlueLight());
                    show3sCountdown();
                    return;
                }

                if (state == ModelState.RUN) {
                    ledHit(data);
                }
                break;

        }

    }

    public void onSpeed(int speed) {
        if (callback == null) {
            return;
        }
        if (speed > maxSpeed) {
            maxSpeed = speed;
        }
        callback.updateScore(blueScore, redScore, speed);

    }

    public void ledHit(byte data) {

    }

    public interface ModelCallback {
        void countdownTime(long time);

        void updateScore(int blueScore, int redScore, int speed);

        void cutDown(String cutDown);
    }

    public enum ModelType {
        NOVICE, JUNIOR, BATTLE, JUNIOR_PREVIEW;
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

    protected int getRandomNum() {
        return new Random().nextInt(6) + 1;
    }

}

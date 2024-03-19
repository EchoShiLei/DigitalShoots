package com.digital.shoots.model;

import com.digital.shoots.ble.BleDataUtils;
import com.digital.shoots.ble.BleDeviceControl;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class BattleModel extends BaseModel {

    private static final String TAG = "BattleModel";
    int blueLed, redLed;
    long blueLedTime, redLedTime;
    int redScore = 0;
    int blueScore = 0;

    public BattleModel(BleDeviceControl bleDeviceControl, ModelCallback callback) {
        super(bleDeviceControl, callback);

    }

    @Override
    public void init() {
        super.init();
    }

    public void start() {
        time = 75000;
        redScore = 0;
        blueScore = 0;
        openBlueLed();
        openRedLed();
    }

    @Override
    public void ready() {
    }

    @Override
    public void run() {

    }

    @Override
    void doTime() {
        if (time == 0) {
            end();
            return;
        }
        time -= TIME_PERIOD;
        checkTime();
    }

    private void checkTime() {
        if (blueLedTime - time >= OUT_TIME) {
            closeBlueLed(blueLed, false);
        }
        if (redLedTime - time >= OUT_TIME) {
            closeRedLed(redLed, false);
        }
    }


    @Override
    public void ledHit(byte data) {
        closeBlueLed(data, true);
        closeRedLed(data, true);
    }

    private void openBlueLed() {
        blueLedTime = time;
        int index = getRandomNum();
        while (index == redLed) {
            index = getRandomNum();
        }
        blueLed = index;
        bleDeviceControl.writeBle(BleDataUtils.openBlueData(blueLed));

    }

    private void openRedLed() {
        redLedTime = time;
        int index = getRandomNum();
        while (index == blueLed) {
            index = getRandomNum();
        }
        redLed = index;
        bleDeviceControl.writeBle(BleDataUtils.openRedData(redLed));

    }

    private void closeBlueLed(int index, boolean isHit) {
        if (index != blueLed) {
            return;
        }
        if (isHit) {
            blueScore += BleDataUtils.getScore(index);
        }
        openBlueLed();

        callback.updateScore(blueScore, redScore, 0);

    }

    private void closeRedLed(int index, boolean isHit) {
        if (index != redLed) {
            return;
        }
        if (isHit) {
            redScore += BleDataUtils.getScore(index);
        }
        openRedLed();
        callback.updateScore(blueScore, redScore, 0);
    }

    @Override
    public void end() {
        super.end();
    }
}

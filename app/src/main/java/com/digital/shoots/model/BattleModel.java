package com.digital.shoots.model;

import com.digital.shoots.ble.BleDataUtils;
import com.digital.shoots.ble.BleDeviceControl;

import java.util.HashSet;
import java.util.Set;

public class BattleModel extends BaseModel {

    private static final String TAG = "JuniorModel";
    Set<Byte> hitList = new HashSet<>();
    int blueLed, redLed;
    long blueLedTime, redLedTime;
    int redScore = -1;
    int blueScore = -1;

    public BattleModel(BleDeviceControl bleDeviceControl, ModelCallback callback) {
        super(bleDeviceControl, callback);

    }

    @Override
    public void init() {
        super.init();
        hitList = new HashSet<>();
    }

    public void start() {
        time = 75000;
        redScore = -1;
        blueLed = -1;
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
        if (blueLed - time >= OUT_TIME) {
            closeBlueLed(blueLed, false);
        }
        if (redLed - time >= OUT_TIME) {
            closeRedLed(redLed, false);
        }
    }


    @Override
    public void ledHit(byte data) {

        if (!hitList.add(data)) {
            return;
        }
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
            blueScore++;
        }
        bleDeviceControl.writeBle(BleDataUtils.closeBlueData(blueLed));
        openBlueLed();

        callback.updateScore(blueScore, redScore, 0);

    }

    private void closeRedLed(int index, boolean isHit) {
        if (index != redLed) {
            return;
        }
        if (isHit) {
            redScore++;
        }
        bleDeviceControl.writeBle(BleDataUtils.closeRedData(redLed));
        openRedLed();
        callback.updateScore(blueScore, redScore, 0);
    }

    @Override
    public void end() {
        super.end();
    }
}

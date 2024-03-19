package com.digital.shoots.model;

import com.digital.shoots.ble.BleDataUtils;
import com.digital.shoots.ble.BleDeviceControl;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class JuniorModel extends BaseModel {

    private static final String TAG = "JuniorModel";
    int[] blueBase = {1, 4, 6};
    int[] redBase = {2, 3, 5};
    int blueLed, redLed;
    long ledTime;
    int score = 0;

    public JuniorModel(BleDeviceControl bleDeviceControl, ModelCallback callback) {
        super(bleDeviceControl, callback);

    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void start() {
        time = 75000;
        score = 0;
        openLed();
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
        if (ledTime - time >= OUT_TIME) {
            closeAllLed(blueLed, false);
        }
    }


    @Override
    public void ledHit(byte data) {
        closeAllLed(data, true);
    }

    private void openLed() {
        ledTime = time;

        //开蓝灯
        int index1 = blueBase[getRandomNum()];
        while (index1 == blueLed) {
            index1 = blueBase[getRandomNum()];
        }
        blueLed = index1;
        bleDeviceControl.writeBle(BleDataUtils.openBlueData(blueLed));

        //开红灯
        int index2 = redBase[getRandomNum()];
        while (index2 == redLed) {
            index2 = redBase[getRandomNum()];
        }
        redLed = index2;
        bleDeviceControl.writeBle(BleDataUtils.openRedData(redLed));
    }

    private void closeAllLed(int index, boolean isHit) {
        if (isHit) {
            if (index != blueLed && index != redLed) {
                return;
            }
            score += BleDataUtils.getScore(index);
            callback.updateScore(score, 0, 0);
        }

        // 关灯
        bleDeviceControl.writeBle(BleDataUtils.closeAllLight());
        openLed();
    }

    @Override
    public void end() {
        super.end();
    }


    @Override
    protected int getRandomNum() {
        return new Random().nextInt(3);
    }
}

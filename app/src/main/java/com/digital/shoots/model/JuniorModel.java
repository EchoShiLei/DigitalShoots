package com.digital.shoots.model;

import android.text.TextUtils;

import com.digital.shoots.ble.BleDataUtils;
import com.digital.shoots.ble.BleDeviceControl;
import com.digital.shoots.db.greendao.GreenDaoManager;
import com.digital.shoots.db.greendao.bean.GameAchievement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class JuniorModel extends BaseModel {

    private static final String TAG = "JuniorModel";
    int[] blueBase = {1, 4, 6};
    int[] redBase = {2, 3, 5};
    int blueLed, redLed;
    long ledTime;


    public JuniorModel(BleDeviceControl bleDeviceControl, ModelCallback callback) {
        super(bleDeviceControl, callback);
        type = ModelType.JUNIOR;

    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public synchronized void start() {
        time = 75000;
        blueScore = 0;
        openLed();
    }

    @Override
    public void ready() {
    }

    @Override
    public void run() {

    }

    @Override
    synchronized void doTime() {
        if (time == 0) {
            end();
            return;
        }
        time -= TIME_PERIOD;
        checkTime();
    }

    private synchronized void checkTime() {
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
            blueScore += BleDataUtils.getScore(index);
            callback.updateScore(blueScore, 0, 0);
        }

        // 关灯
        bleDeviceControl.writeBle(BleDataUtils.closeAllLight());
        openLed();
    }

    @Override
    public synchronized void end() {
        super.end();
        saveToDB();
    }

    @Override
    void saveToDB() {
        if (time >= 10) {
            return;
        }
        // 记数据库
        GameAchievement gameAchievement = new GameAchievement();
        gameAchievement.setType(type.ordinal());
        gameAchievement.setBlueScore(blueScore);
        gameAchievement.setRedScore(redScore);
        gameAchievement.setSpeed(maxSpeed);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String currentDate = sdf.format(date);
        gameAchievement.setDay(currentDate);
        gameAchievement.setCurrentTime(System.currentTimeMillis());
        GreenDaoManager.insert(gameAchievement);
    }


    @Override
    protected int getRandomNum() {
        return new Random().nextInt(3);
    }
}

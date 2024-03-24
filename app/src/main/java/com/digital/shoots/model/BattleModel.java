package com.digital.shoots.model;

import com.digital.shoots.ble.BleDataUtils;
import com.digital.shoots.ble.BleDeviceControl;
import com.digital.shoots.db.greendao.GreenDaoManager;
import com.digital.shoots.db.greendao.bean.GameAchievement;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BattleModel extends BaseModel {

    private static final String TAG = "BattleModel";
    int blueLed, redLed;
    long blueLedTime, redLedTime;

    public BattleModel(BleDeviceControl bleDeviceControl, ModelCallback callback) {
        super(bleDeviceControl, callback);
        type= ModelType.BATTLE;

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
        saveToDB();
    }

    @Override
    void saveToDB() {
        // 记数据库
        GameAchievement gameAchievement = new GameAchievement();
        gameAchievement.setType(type.ordinal());
        gameAchievement.setBlueScore(blueScore);
        gameAchievement.setRedScore(redScore);
        gameAchievement.setRedScore(speed);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String currentDate = sdf.format(date);
        gameAchievement.setDay(currentDate);
        gameAchievement.setCurrentTime(System.currentTimeMillis());
        GreenDaoManager.insert(gameAchievement);
    }
}

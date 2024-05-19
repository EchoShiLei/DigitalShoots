package com.digital.shoots.model;

import android.text.TextUtils;

import com.digital.shoots.ble.BleDataUtils;
import com.digital.shoots.ble.BleDeviceControl;
import com.digital.shoots.db.greendao.GreenDaoManager;
import com.digital.shoots.db.greendao.bean.GameAchievement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class NoviceModel extends BaseModel {

    private static final String TAG = "NoviceModel";
    Set<Integer> hitList = new HashSet<>();
    int count = -1;

    public NoviceModel(BleDeviceControl bleDeviceControl, ModelCallback callback) {
        super(bleDeviceControl, callback);
        type = ModelType.NOVICE;

    }

    @Override
    public void init() {
        super.init();
        hitList = new HashSet<>();
        time = 0;
        blueScore = 0;
        count = -1;
        callback.countdownTime(time);
        callback.updateScore(blueScore,redScore,maxSpeed);
    }

    public synchronized void start() {
        bleDeviceControl.writeBle(BleDataUtils.openAllBlueLight());
    }

    @Override
    public void ready() {
    }

    @Override
    public void run() {

    }

    @Override
    synchronized void doTime() {
        time += TIME_PERIOD;
        checkTime();
    }

    @Override
    protected synchronized void checkTime() {
        super.checkTime();
        if (time >= MAX_TIME) {
            end();
        }
    }

    @Override
    public void ledHit(byte data) {
        if (!hitList.add((int) data)) {
            return;
        }
        blueScore ++;
        count = hitList.size();
        sendMsg(BleDataUtils.closeBlueData(hitList));
        callback.updateScore(blueScore, 0, 0);

        if (count == 6) {
            //end
            end();
        }
    }

    @Override
    public synchronized void end() {
        super.end();
        saveToDB();
    }

    @Override
    void saveToDB() {
        if (count != 6) {
            //end
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
}

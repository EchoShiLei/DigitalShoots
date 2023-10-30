package com.digital.shoots.model;

import android.util.Log;

import com.digital.shoots.ble.BleDataUtils;
import com.digital.shoots.ble.BleDeviceControl;
import com.digital.shoots.utils.ThreadPoolManager;

import java.util.HashSet;
import java.util.Set;

public class NoviceModel extends BaseModel {

    private static final String TAG = "NoviceModel";
    Set<Byte> hitList = new HashSet<>();
    int count = -1;

    public NoviceModel(BleDeviceControl bleDeviceControl, ModelCallback callback) {
        super(bleDeviceControl, callback);

    }

    public void start() {
        time = 0;
        count = -1;
        hitList = new HashSet<>();
        sendMsg(BleDataUtils.openAllBlueLight());
//        for (int i = 1; i < 7; i++) {
//            sendMsg(BleDataUtils.openBlueData(i));
//        }
    }

    @Override
    public void ready() {
    }

    @Override
    public void run() {

    }

    @Override
    void doTime() {
        time += TIME_PERIOD;
    }


    @Override
    void ledHit(byte data) {
        if (count == -1) {
            count = 0;
            for (int i = 1; i < 7; i++) {
                sendMsg(BleDataUtils.openRedData(i));
            }
            startTime();
        } else {
            hitList.add(data);
            count = hitList.size();
            sendMsg(BleDataUtils.closeRedData(data));
        }


        if (count == 6) {
            //end

        }
    }

    private void starPlayCountdown() {
        int time = 4;
        while (time == 0) {
            time--;

        }

    }
}

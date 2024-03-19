package com.digital.shoots.model;

import com.digital.shoots.ble.BleDataUtils;
import com.digital.shoots.ble.BleDeviceControl;

import java.util.HashSet;
import java.util.Set;

public class NoviceModel extends BaseModel {

    private static final String TAG = "NoviceModel";
    Set<Integer> hitList = new HashSet<>();
    int count = -1;

    public NoviceModel(BleDeviceControl bleDeviceControl, ModelCallback callback) {
        super(bleDeviceControl, callback);
        type= ModelType.NOVICE;

    }

    @Override
    public void init() {
        super.init();
        hitList = new HashSet<>();
    }

    public void start() {
        time = 0;
        blueScore = 0;
        count = -1;
        bleDeviceControl.writeBle(BleDataUtils.openAllBlueLight());
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
    public void ledHit(byte data) {
        if (!hitList.add((int) data)) {
            return;
        }
        blueScore+= BleDataUtils.getScore(data);
        count = hitList.size();
        sendMsg(BleDataUtils.closeBlueData(hitList));
        callback.updateScore(blueScore, 0, 0);

        if (count == 6) {
            //end
            end();
        }
    }

    @Override
    public void end() {
        super.end();
    }
}

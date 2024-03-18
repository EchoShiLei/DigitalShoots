package com.digital.shoots.model;

import com.digital.shoots.ble.BleDataUtils;
import com.digital.shoots.ble.BleDeviceControl;

import java.util.HashSet;
import java.util.Set;

public class NoviceModel extends BaseModel {

    private static final String TAG = "NoviceModel";
    Set<Byte> hitList = new HashSet<>();
    int count = -1;

    public NoviceModel(BleDeviceControl bleDeviceControl, ModelCallback callback) {
        super(bleDeviceControl, callback);

    }

    @Override
    public void init() {
        super.init();
        hitList = new HashSet<>();
    }

    public void start() {
        time = 0;
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

        if (!hitList.add(data)) {
            return;
        }
        count = hitList.size();
        sendMsg(BleDataUtils.closeRedData());
        callback.updateScore(count, 0, 0);

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

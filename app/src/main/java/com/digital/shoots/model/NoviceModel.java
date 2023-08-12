package com.digital.shoots.model;

import com.digital.shoots.ble.BleDataUtils;
import com.digital.shoots.ble.BleDeviceControl;

import java.util.TimerTask;

public class NoviceModel extends BaseModel {

    public NoviceModel(BleDeviceControl bleDeviceControl, ModelCallback callback) {
        super(bleDeviceControl, callback);
    }

    public void start() {
        super.start();
        time = 0;
        callback.countdownTime(time);
        bleDeviceControl.writeBle(BleDataUtils.getCountdownData());
    }

    @Override
    void doTime() {
        time += TIME_PERIOD;
    }
}

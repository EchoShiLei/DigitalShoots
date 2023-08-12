package com.digital.shoots.model;

import com.digital.shoots.ble.BleDataUtils;
import com.digital.shoots.ble.BleDeviceControl;

public class NoviceModel extends BaseModel {

    public NoviceModel(BleDeviceControl bleDeviceControl, ModelCallback callback) {
        super(bleDeviceControl, callback);

    }

    public void start() {
        super.start();
        time = 0;
        callback.countdownTime(time);
//        bleDeviceControl.writeBle(BleDataUtils.getCountdownData());
        for (int i = 1; i < 6; i++) {
            beExecuteList.add(BleDataUtils.OpenRedData(i));
        }
        bleDeviceControl.writeBle(beExecuteList.pop());
    }

    @Override
    void doTime() {
        time += TIME_PERIOD;
    }

    @Override
    void onData(byte[] data) {
        if (data.length == 6) {
            if (data[3] == 1) {
                bleDeviceControl.writeBle(beExecuteList.pop());
            }
        }

    }
}

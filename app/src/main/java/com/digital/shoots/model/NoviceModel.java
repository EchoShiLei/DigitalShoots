package com.digital.shoots.model;

import com.digital.shoots.ble.BleDataUtils;
import com.digital.shoots.ble.BleDeviceControl;

public class NoviceModel extends BaseModel {

    public NoviceModel(BleDeviceControl bleDeviceControl, ModelCallback callback) {
        super(bleDeviceControl, callback);

    }

    public void start() {
        time = 0;
        sendMsg(BleDataUtils.openRedData());
    }

    @Override
    public void ready() {
        beExecuteList.add(BleDataUtils.openAllLight());
    }

    @Override
    public void run() {

    }

    @Override
    void doTime() {
        time += TIME_PERIOD;
    }

    @Override
    void onData(byte[] data) {

    }
}

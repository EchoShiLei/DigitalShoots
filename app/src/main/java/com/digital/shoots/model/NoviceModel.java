package com.digital.shoots.model;

import com.digital.shoots.ble.BleDataUtils;
import com.digital.shoots.ble.BleDeviceControl;
import com.digital.shoots.utils.ThreadPoolManager;

import java.util.HashSet;
import java.util.Set;

public class NoviceModel extends BaseModel {

    Set<Byte> hitList = new HashSet<>();
    int count = -1;

    public NoviceModel(BleDeviceControl bleDeviceControl, ModelCallback callback) {
        super(bleDeviceControl, callback);

    }

    public void start() {
        time = 0;
        count = -1;
        hitList = new HashSet<>();
//        sendMsg(BleDataUtils.openAllBlueLight());
        sendMsg(BleDataUtils.openRedData());
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
    public void onData(byte[] data) {
        if (data.length != 5) {
            return;
        }
        int cmd = data[3];
        switch (cmd) {
            case BLEControlStatuAutoReportBegain:
                starPlayCountdown();
                break;
            case BLEControlStatuCountDownControl:
                for (int i = 1; i < 7; i++) {
                    sendMsg(BleDataUtils.openRedData(i));
                }

                break;
            case BLEControlStatuAutoReport:
                if (count == -1) {
                    count = 0;
                    sendMsg(BleDataUtils.closeAllBlueLight());
                    startTime();
                } else {
                    hitList.add(data[4]);
                    count = hitList.size();
//                    sendMsg(BleDataUtils.closeRedData());
                }
                break;
            default:
                break;
        }
        if (count == 6) {

        }


    }

    private void starPlayCountdown() {
        int time = 4;
        while (time == 0) {
            time--;

        }

    }
}

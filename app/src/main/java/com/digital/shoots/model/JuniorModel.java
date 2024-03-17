package com.digital.shoots.model;

import com.digital.shoots.ble.BleDataUtils;
import com.digital.shoots.ble.BleDeviceControl;

import java.util.HashSet;
import java.util.Set;

public class JuniorModel extends BaseModel {

    private static final String TAG = "JuniorModel";
    Set<Byte> hitList = new HashSet<>();
    int blueLed,redLed;
    int count = -1;

    public JuniorModel(BleDeviceControl bleDeviceControl, ModelCallback callback) {
        super(bleDeviceControl, callback);

    }

    @Override
    public void init() {
        super.init();
        hitList = new HashSet<>();
    }

    public void start() {
        time = 7500;
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
        time -= TIME_PERIOD;
    }


    @Override
    public void ledHit(byte data) {

        if (!hitList.add(data)) {
            return;
        }
        count = hitList.size();
        sendMsg(BleDataUtils.closeRedData(data));
        callback.updateScore(count,0,0);

        if (count == 6) {
            //end
            end();
        }
    }

    private void openBlueLed(){

    }
    private void openRedLed(){

    }

    @Override
    public void end() {
        super.end();
    }
}

package com.digital.shoots.main;

import android.app.Application;

import com.digital.shoots.ble.BleDeviceControl;
import com.digital.shoots.ble.BleDeviceManager;
import com.digital.shoots.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class MainViewModel extends AndroidViewModel {

    Map<String, BleDeviceControl> deviceControlMap = new HashMap<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
    }


    public void deviceClick(String mac) {
        BleDeviceControl control = deviceControlMap.get(mac);
        if (control == null) {
            control = new BleDeviceControl(this, new BleDeviceControl.UiConnectCallback() {
                @Override
                public void onSuccess() {
                    ToastUtils.showToastD("Success");
                }

                @Override
                public void onFailed() {
                    ToastUtils.showToastD("fail");
                }
            });
            deviceControlMap.put(mac, control);
            control.connect(BleDeviceManager.getInstance().getDevice(mac));
        } else {
            control.disConnect();
        }

    }

    public void deviceConnected() {

    }


}

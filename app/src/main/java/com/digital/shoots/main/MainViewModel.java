package com.digital.shoots.main;

import android.app.Application;

import com.digital.shoots.R;
import com.digital.shoots.ble.BleDeviceControl;
import com.digital.shoots.ble.BleDeviceManager;
import com.digital.shoots.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class MainViewModel extends AndroidViewModel {

    public Map<String, BleDeviceControl> deviceControlMap = new HashMap<>();

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

    public void deviceConnected(String mac) {

    }

    public void startModel1() {
        if (deviceControlMap.size() == 0) {
            ToastUtils.showToast(R.string.pls_connect);
            return;
        }
        BleDeviceControl control = deviceControlMap.entrySet().stream().findFirst().get().getValue();
        control.startModel(1);
    }


}

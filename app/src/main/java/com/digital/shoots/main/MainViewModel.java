package com.digital.shoots.main;

import android.app.Application;

import com.digital.shoots.R;
import com.digital.shoots.ble.BleDeviceControl;
import com.digital.shoots.ble.BleDeviceManager;
import com.digital.shoots.model.BaseModel;
import com.digital.shoots.model.NoviceModel;
import com.digital.shoots.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MainViewModel extends AndroidViewModel {

    public BleDeviceControl modelControl;
    BaseModel model;
    private MutableLiveData<Long> livTime = new MutableLiveData<>();


    public MainViewModel(@NonNull Application application) {
        super(application);
    }


    public void deviceClick(String mac) {
        if (modelControl == null) {
            modelControl = new BleDeviceControl(this, new BleDeviceControl.UiConnectCallback() {
                @Override
                public void onSuccess() {
                    ToastUtils.showToastD("Success");
                }

                @Override
                public void onFailed() {
                    ToastUtils.showToastD("fail");
                }
            });
            modelControl.connect(BleDeviceManager.getInstance().getDevice(mac));
        } else {
            modelControl.disConnect();
        }

    }


    public void deviceConnected(String mac) {

    }

    public void startModel1() {
        if (modelControl == null) {
            ToastUtils.showToast(R.string.pls_connect);
            return;
        }

        model = new NoviceModel(modelControl, new BaseModel.ModelCallback() {
            @Override
            public void countdownTime(long time) {
                livTime.postValue(time);
            }
        });
        model.start();
    }

    public void endModel() {
        if (model == null) {
            return;
        }
        model.getClass();
    }

    public MutableLiveData<Long> getLivTime() {
        return livTime;
    }
}

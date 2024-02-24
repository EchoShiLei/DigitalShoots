package com.digital.shoots.main;

import android.app.Application;

import com.digital.shoots.R;
import com.digital.shoots.ble.BleDataUtils;
import com.digital.shoots.ble.BleDeviceControl;
import com.digital.shoots.ble.BleDeviceManager;
import com.digital.shoots.model.BaseModel;
import com.digital.shoots.model.BaseModel.ModelType;
import com.digital.shoots.model.NoviceModel;
import com.digital.shoots.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class MainViewModel extends AndroidViewModel {

    public BleDeviceControl deviceControl;
    BaseModel model;
    private MutableLiveData<Long> livTime = new MutableLiveData<>();
    private MutableLiveData<Integer> liveScore = new MutableLiveData<>();
    private MutableLiveData<Set<String>> liveConnectedMacs = new MutableLiveData<>();

    private Set<String> connectedMacs = new HashSet<>();


    public MainViewModel(@NonNull Application application) {
        super(application);
    }


    public void deviceClick(String mac) {
        if (deviceControl == null) {
            deviceControl = new BleDeviceControl(this, new BleDeviceControl.UiConnectCallback() {
                @Override
                public void onSuccess(String mac) {
                    ToastUtils.showToastD("Success");
                    connectedMacs.add(mac);
                    liveConnectedMacs.postValue(connectedMacs);
                }

                @Override
                public void onFailed(String mac) {
                    ToastUtils.showToastD("fail");
                    connectedMacs.remove(mac);
                    liveConnectedMacs.postValue(connectedMacs);
                }
            });
            deviceControl.connect(BleDeviceManager.getInstance().getDevice(mac));
        } else {
            deviceControl.disConnect();
        }
        deviceControl.setDataCallback((cmd, data) -> {
            if (model == null) {
                return;
            }
            model.onCmdData(cmd, data);
        });

    }

    public void online() {
        deviceControl.writeBle(BleDataUtils.appOnlineControl());
        BleDataUtils.isOnline = true;
    }

    public void offline() {
        deviceControl.writeBle(BleDataUtils.appOfflineControl());
        BleDataUtils.isOnline = false;
    }

    public void startModel(ModelType modelType) {
        if (connectedMacs.size() == 0) {
            ToastUtils.showToast(R.string.pls_connect);
            return;
        }

        BaseModel.ModelCallback callback = new BaseModel.ModelCallback() {
            @Override
            public void countdownTime(long time) {
                livTime.postValue(time);
            }

            @Override
            public void updateScore(int score, int speed) {
                deviceControl.writeBle(BleDataUtils.showNumber(score));
                liveScore.postValue(score);
            }

            @Override
            public void endGame() {

            }
        };

        switch (modelType) {
            case NOVICE:
                model = new NoviceModel(deviceControl, callback);
                break;
            case BATTLE:
                break;
            case JUNIOR:
                break;
            default:
                break;
        }
    }

    public void endModel() {
        if (model == null) {
            return;
        }
        model.end();
    }

    public MutableLiveData<Long> getLivTime() {
        return livTime;
    }

    public MutableLiveData<Integer> getLiveScore() {
        return liveScore;
    }

    // 获取已连接设备数
    public MutableLiveData<Set<String>> getLiveConnectedMacs() {
        return liveConnectedMacs;
    }
}

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

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class MainViewModel extends AndroidViewModel {

    public BleDeviceControl modelControl;
    BaseModel model;
    private MutableLiveData<Long> livTime = new MutableLiveData<>();
    private MutableLiveData<Integer>  liveScore= new MutableLiveData<>();


    public MainViewModel(@NonNull Application application) {
        super(application);
    }


    public void deviceClick(String mac) {
        if (modelControl == null) {
            modelControl = new BleDeviceControl(this, new BleDeviceControl.UiConnectCallback() {
                @Override
                public void onSuccess() {
                    modelControl.writeBle(BleDataUtils.appOnlineControl());
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
        modelControl.setDataCallback(data -> {
            liveScore.postValue(BleDataUtils.bytes2Hex(data));
        });

    }


    public void deviceConnected(String mac) {

    }

    public void startModel(ModelType modelType) {
        if (modelControl == null) {
            ToastUtils.showToast(R.string.pls_connect);
            return;
        }

        BaseModel.ModelCallback callback = new BaseModel.ModelCallback() {
            @Override
            public void countdownTime(long time) {
                livTime.postValue(time);
            }
        };

        switch (modelType) {
            case NOVICE:
                model = new NoviceModel(modelControl, callback);
                break;
            case BATTLE:
                break;
            case JUNIOR:
                break;
            default:
                break;
        }
        model.start();
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


}

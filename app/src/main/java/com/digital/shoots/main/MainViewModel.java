package com.digital.shoots.main;

import android.app.Application;
import android.bluetooth.BluetoothDevice;

import com.digital.shoots.R;
import com.digital.shoots.base.SpUtil;
import com.digital.shoots.ble.BleDataUtils;
import com.digital.shoots.ble.BleDeviceControl;
import com.digital.shoots.ble.BleDeviceManager;
import com.digital.shoots.db.greendao.GreenDaoManager;
import com.digital.shoots.db.greendao.bean.GameAchievement;
import com.digital.shoots.model.BaseModel;
import com.digital.shoots.model.BaseModel.ModelType;
import com.digital.shoots.model.BattleModel;
import com.digital.shoots.model.JuniorModel;
import com.digital.shoots.model.JuniorPreviewModel;
import com.digital.shoots.model.NoviceModel;
import com.digital.shoots.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import static com.digital.shoots.base.SpUtil.KEY_LAST_BLE_MAC;
import static com.digital.shoots.base.SpUtil.KEY_LAST_BLE_MAC_SPEED;

public class MainViewModel extends AndroidViewModel {

    public BleDeviceControl deviceControl;
    public BaseModel model;
    private MutableLiveData<Long> livTime = new MutableLiveData<>();
    private MutableLiveData<Integer> liveBlueScore = new MutableLiveData<>();
    private MutableLiveData<Integer> liveRedScore = new MutableLiveData<>();
    private MutableLiveData<Set<String>> liveConnectedMacs = new MutableLiveData<>();

    private Set<String> connectedMacs = new HashSet<>();


    public MainViewModel(@NonNull Application application) {
        super(application);
    }


    public void deviceClick(String mac) {
        if (deviceControl == null) {
            deviceControl = new BleDeviceControl(this, new BleDeviceControl.UiConnectCallback() {
                @Override
                public void onSuccess(BluetoothDevice device) {
                    ToastUtils.showToastD("Success");
                    if ("StarShots".equals(device.getName())) {
                        SpUtil.getInstance(getApplication()).putString(KEY_LAST_BLE_MAC, mac);
                    }
                    if ("MySpeedz".equals(device.getName())) {
                        SpUtil.getInstance(getApplication()).putString(KEY_LAST_BLE_MAC_SPEED, mac);
                    }
                    connectedMacs.add(device.getAddress());
                    liveConnectedMacs.postValue(connectedMacs);
                }

                @Override
                public void onFailed(String mac) {
                    connectedMacs.remove(mac);
                    ToastUtils.showToastD("fail");
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
        deviceControl.realWriteBle(BleDataUtils.appOnlineControl());
        BleDataUtils.isOnline = true;
    }

    public void offline() {
        deviceControl.realWriteBle(BleDataUtils.appOfflineControl());
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
            public void updateScore(int blueScore, int redScore, int speed) {
                deviceControl.writeBle(BleDataUtils.showNumber(blueScore));
                liveBlueScore.postValue(blueScore);
                liveRedScore.postValue(redScore);
            }

            @Override
            public void endGame() {

            }
        };

        switch (modelType) {
            case NOVICE:
                model = new NoviceModel(deviceControl, callback);
                break;
            case JUNIOR:
                model = new JuniorModel(deviceControl, callback);
                break;
            case BATTLE:
                model = new BattleModel(deviceControl, callback);
                break;
            case JUNIOR_PREVIEW:
                model = new JuniorPreviewModel(deviceControl, callback);
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

    public MutableLiveData<Integer> getLiveBlueScore() {
        return liveBlueScore;
    }

    public MutableLiveData<Integer> getLiveRedScore() {
        return liveRedScore;
    }

    // 获取已连接设备数
    public MutableLiveData<Set<String>> getLiveConnectedMacs() {
        return liveConnectedMacs;
    }
}

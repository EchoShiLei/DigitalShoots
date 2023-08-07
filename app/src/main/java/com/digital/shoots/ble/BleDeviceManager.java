package com.digital.shoots.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import com.digital.shoots.DigitalApplication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.RequiresApi;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;
import static android.content.Context.BLUETOOTH_SERVICE;

public class BleDeviceManager {
    private static final String TAG = "BleDeviceManager";


    private Handler handler;

    List<BluetoothDevice> devices = new ArrayList<>();

    private Set<String> mDeviceMac = new HashSet<>();

    private static BleDeviceManager instance;

    private List<ScanFilter> filters = new ArrayList<ScanFilter>();

    private ScanSettings scanSettings;

    private ScanCallback scanCallback;

    private UiScanCallback uiScanCallback;

    private BluetoothDevice selectDevice;

    /**
     * 蓝牙适配器
     */
    private BluetoothAdapter bluetoothAdapter;

    static {
        instance = new BleDeviceManager();
    }

    //构造函数私有
    private BleDeviceManager() {
        handler = new Handler(Looper.getMainLooper());
        //获取蓝牙适配器
        BluetoothManager bluetoothManager = (BluetoothManager) DigitalApplication.getContext().getSystemService(BLUETOOTH_SERVICE);//这里与标准蓝牙略有不同
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }


        //这里使用的SEARCH_SERVICE_UUID可以向蓝牙芯片厂商获取
//        filters.add(0, new ScanFilter.Builder().setServiceUuid(new ParcelUuid(UUID.fromString(SERVICE_UUID))).build());

        scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                .build();

        scanCallback = new ScanCallback() {

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(TAG, "onScanFailed errorCode : " + errorCode);
                showToast("onScanFailed errorCode : " + errorCode);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                for (ScanResult result : results) {
                    handleResult(result);
                }
            }

            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                handleResult(result);
            }
        };
    }

    private void handleResult(ScanResult result) {
        BluetoothDevice device = result.getDevice();
        // If it's already paired, skip it, because it's been listed already
        if (device.getName() == null || device.getName() == "") {
            return;
        }
        if (mDeviceMac.contains(device.getAddress())) {
            return;
        }
        mDeviceMac.add(device.getAddress());
        devices.add(device);

        if (uiScanCallback != null) {
            uiScanCallback.onResult(device.getName(), device.getAddress());
        }
    }

    //提供静态方法
    public static BleDeviceManager getInstance() {
        return instance;
    }

    public List<BluetoothDevice> getDevices() {
        return devices;
    }

    public void addDevice(BluetoothDevice device) {
        devices.add(device);
    }


    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public BluetoothDevice getSelectDevice() {
        return selectDevice;
    }

    public void scan() {
        devices.clear();
        mDeviceMac.clear();
        stopScan();
        bluetoothAdapter.getBluetoothLeScanner().startScan(filters, scanSettings, scanCallback);
    }

    public void stopScan() {
        // Make sure we're not doing discovery anymore
        if (bluetoothAdapter == null) {
            return;
        }
        bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);

    }

    public BluetoothDevice getDevice(String address) {
        selectDevice = bluetoothAdapter.getRemoteDevice(address);
        return selectDevice;
    }


    public static int bytes2Hex(byte[] b) {
        String r = "";

        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            r += hex.toUpperCase();
        }

        // 十六进制转10进制
        return Integer.parseInt(r, 16);
    }


    //官方特征值


    public void setUiScanCallback(UiScanCallback uiScanCallback) {
        this.uiScanCallback = uiScanCallback;
    }

    public interface UiScanCallback {
        void onResult(String name, String address);
    }


    private void showToast(String str) {
        handler.post(() -> Toast.makeText(DigitalApplication.getContext(), str, Toast.LENGTH_LONG).show());
    }

}

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


    //接收设备的UUID
    private static final String RECEIVE_SERVICE_UUID = "0000ffe0-0000-1000-8000-00805f9b34fb";
    //发送设备的UUID
    private static final String WRITE_SERVICE_UUID = "0000ffe5-0000-1000-8000-00805f9b34fb";

    //DATA_UUID
    private static final String RECEIVE_DATA_UUID = "0000ffe4-0000-1000-8000-00805f9b34fb";
    //DATA_UUID
    private static final String WRITE_DATA_UUID = "0000ffe9-0000-1000-8000-00805f9b34fb";

    public static final String ALERT_MSG = "ALERT_MSG";

    public static final String INCOMING_MSG = "INCOMING_MSG";

    private Handler handler;
    List<BluetoothDevice> devices = new ArrayList<>();

    private Set<String> mDeviceMac = new HashSet<>();

    private static BleDeviceManager instance;

    private List<ScanFilter> filters = new ArrayList<ScanFilter>();

    private ScanSettings scanSettings;

    private ScanCallback scanCallback;

    private UiScanCallback uiScanCallback;

    private UiConnectCallback uiConnectCallback;

    private BluetoothDevice selectDevice;

    private BluetoothGatt bluetoothGatt;

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

    public void connect(String address, UiConnectCallback connectCallback) {
        this.uiConnectCallback = connectCallback;

        bluetoothGatt = null;
        selectDevice = bluetoothAdapter.getRemoteDevice(address);
        connectToDevice(selectDevice);

    }

    public void connectToDevice(BluetoothDevice device) {
        if (bluetoothGatt == null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                bluetoothGatt = device.connectGatt(DigitalApplication.getContext(),
                        true, gattCallback, TRANSPORT_LE);
            } else {
                bluetoothGatt = device.connectGatt(DigitalApplication.getContext(),
                        true, gattCallback);
            }

            if (bluetoothGatt == null) {
                Log.d(TAG, "firstGatt is null!");
            }
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.d(TAG, "onConnectionStateChange,Status: " + status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                switch (newState) {
                    case BluetoothProfile.STATE_CONNECTED:
                        Log.i(TAG, "gattCallback,STATE_CONNECTED");
                        if (uiConnectCallback != null) {
                            uiConnectCallback.onSuccess();
                        }
                        gatt.discoverServices();
                        break;
                    case BluetoothProfile.STATE_DISCONNECTED:
                        Log.i(TAG, "gattCallback,STATE_DISCONNECTED");
                        break;
                    default:
                        Log.e(TAG, "gattCallback,STATE_OTHER");
                }
            } else {
                //连接失败

                if (uiConnectCallback != null) {
                    uiConnectCallback.onFailed();
                }
                Log.e(TAG, "onConnectionStateChange fail,Status:" + status);
                showToast("onConnectionStateChange fail,Status: " + status);
                gatt.close();
            }
        }

        /**
         * 发现设备（真正建立连接）
         * */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = bluetoothGatt.getServices();
            Log.d(TAG, "onServicesDiscovered:" + services.toString());
            setNotification();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            //接收数据
            byte[] bytes = characteristic.getValue();
            int speed = bytes2Hex(bytes);
            Log.i(TAG, "收到数据str:" + speed);
            sendBroadcast(speed + "", INCOMING_MSG);

        }

        @Override
        public void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "onCharacteristicRead:" + characteristic.toString());
        }
    };

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


    private void setNotification() {
        BluetoothGattService service = bluetoothGatt.getService(UUID.fromString(RECEIVE_SERVICE_UUID));
        if (service == null) {
            return;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(RECEIVE_DATA_UUID));
        boolean isEnableNotification = bluetoothGatt.setCharacteristicNotification(characteristic, true);

        if (isEnableNotification) {
            List<BluetoothGattDescriptor> descriptorList = characteristic.getDescriptors();
            if (descriptorList != null && descriptorList.size() > 0) {
                for (BluetoothGattDescriptor descriptor : descriptorList) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    bluetoothGatt.writeDescriptor(descriptor);
                }
            }
        }

    }

    public void setUiScanCallback(UiScanCallback uiScanCallback) {
        this.uiScanCallback = uiScanCallback;
    }

    public interface UiScanCallback {
        void onResult(String name, String address);
    }

    public interface UiConnectCallback {
        void onSuccess();

        void onFailed();
    }


    private void sendBroadcast(String str, String action) {
        Intent i = new Intent(action);
        i.putExtra("STR", str);
        i.putExtra("COUNTER", -1);
        DigitalApplication.getContext().sendBroadcast(i);
    }

    private void showToast(String str) {
        handler.post(() -> Toast.makeText(DigitalApplication.getContext(), str, Toast.LENGTH_LONG).show());
    }

    public void writeBle(byte[] value) {
        BluetoothGattService service = bluetoothGatt.getService(UUID.fromString(WRITE_SERVICE_UUID));
        if (service == null) {
            return;
        }
        BluetoothGattCharacteristic writeChar = service.getCharacteristic(UUID.fromString(WRITE_DATA_UUID));

        writeChar.setValue(value);
        bluetoothGatt.writeCharacteristic(writeChar);
    }
}

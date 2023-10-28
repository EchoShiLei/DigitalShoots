package com.digital.shoots.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import android.util.Log;

import com.digital.shoots.DigitalApplication;
import com.digital.shoots.main.MainViewModel;
import com.digital.shoots.model.BaseModel;
import com.digital.shoots.utils.ToastUtils;

import java.util.List;
import java.util.UUID;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;

public class BleDeviceControl {
    private static final String TAG = "BleDeviceControl";

    //接收设备的UUID
    private static final String RECEIVE_SERVICE_UUID = "0000ffe0-0000-1000-8000-00805f9b34fb";
    //发送设备的UUID
    private static final String WRITE_SERVICE_UUID = "0000ffe5-0000-1000-8000-00805f9b34fb";

    //DATA_UUID
    private static final String RECEIVE_DATA_UUID = "0000ffe4-0000-1000-8000-00805f9b34fb";
    //DATA_UUID
    private static final String WRITE_DATA_UUID = "0000ffe9-0000-1000-8000-00805f9b34fb";


    private BluetoothGatt bluetoothGatt;

    private UiConnectCallback uiConnectCallback;
    private DataCallback dataCallback;
    private MainViewModel mainViewModel;


    public BleDeviceControl(MainViewModel viewModel, UiConnectCallback connectCallback) {
        this.uiConnectCallback = connectCallback;
        mainViewModel = viewModel;
    }

    public void setDataCallback(DataCallback callback) {
        dataCallback = callback;
    }


    public void connect(BluetoothDevice device) {
        if (bluetoothGatt == null) {
            bluetoothGatt = device.connectGatt(DigitalApplication.getContext(),
                    true, gattCallback, TRANSPORT_LE);

            if (bluetoothGatt == null) {
                Log.d(TAG, "firstGatt is null!");
            }
        }
    }

    public void disConnect() {

    }

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
                            uiConnectCallback.onSuccess(gatt.getDevice().getAddress());
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
                    uiConnectCallback.onFailed(gatt.getDevice().getAddress());
                }
                Log.e(TAG, "onConnectionStateChange fail,Status:" + status);
                ToastUtils.showToastD("onConnectionStateChange fail,Status: " + status);
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
//            int speed = bytes2Hex(bytes);
            Log.i(TAG, "收到数据str:" + bytes.toString());
//            sendBroadcast(speed + "", INCOMING_MSG);
            handleReceiveData(bytes);
        }

        @Override
        public void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "onCharacteristicRead:" + characteristic.toString());
        }
    };

    public void writeBle(byte[] value) {
        BluetoothGattService service = bluetoothGatt.getService(UUID.fromString(WRITE_SERVICE_UUID));
        if (service == null) {
            return;
        }
        BluetoothGattCharacteristic writeChar = service.getCharacteristic(UUID.fromString(WRITE_DATA_UUID));

        writeChar.setValue(value);
        bluetoothGatt.writeCharacteristic(writeChar);
    }


    //处理接收到到数据
    private void handleReceiveData(byte[] bytes) {
        if (dataCallback != null) {
            dataCallback.onData(bytes);
        }

    }


    public interface UiConnectCallback {
        void onSuccess(String mac);

        void onFailed(String mac);
    }

    public interface DataCallback {
        void onData(byte[] data);
    }
}

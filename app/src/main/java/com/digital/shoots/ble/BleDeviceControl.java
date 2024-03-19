package com.digital.shoots.ble;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import com.digital.shoots.DigitalApplication;
import com.digital.shoots.R;
import com.digital.shoots.main.MainViewModel;
import com.digital.shoots.model.BaseModel;
import com.digital.shoots.utils.ThreadPoolManager;
import com.digital.shoots.utils.ToastUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import androidx.core.app.ActivityCompat;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;
import static com.digital.shoots.utils.BaseConstant.MCU_CMD_LED_HEART;
import static com.digital.shoots.utils.BaseConstant.MCU_CMD_LED_HIT;

public class BleDeviceControl {
    private static final String TAG = "BleDeviceControl";

    //接收设备的UUID
    private static final String RECEIVE_SERVICE_UUID = "0000ffe0-0000-1000-8000-00805f9b34fb";

    //DATA_UUID
    private static final String RECEIVE_DATA_UUID = "0000ffe4-0000-1000-8000-00805f9b34fb";

    private static final String DESCRIPTOR = "00002902-0000-1000-8000-00805f9b34fb";

    //发送设备的UUID
    private static final String WRITE_SERVICE_UUID = "0000ffe5-0000-1000-8000-00805f9b34fb";

    //DATA_UUID
    private static final String WRITE_DATA_UUID = "0000ffe9-0000-1000-8000-00805f9b34fb";


    private BluetoothGatt bluetoothGatt;

    private UiConnectCallback uiConnectCallback;
    private DataCallback dataCallback;
    private MainViewModel mainViewModel;

    HandlerThread thread;
    Handler handler;

    LinkedList<byte[]> doList = new LinkedList<>();


    public BleDeviceControl(MainViewModel viewModel, UiConnectCallback connectCallback) {
        this.uiConnectCallback = connectCallback;
        mainViewModel = viewModel;
        thread = new HandlerThread("BleDeviceControl");
        thread.start();
        handler = new Handler(thread.getLooper());
        doWrite();
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

    public void setNotification() {
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
                    boolean result = bluetoothGatt.writeDescriptor(descriptor);
                    Log.d(TAG, "setNotification: result: " + result);
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
            super.onServicesDiscovered(gatt, status);
            List<BluetoothGattService> services = bluetoothGatt.getServices();
            Log.d(TAG, "onServicesDiscovered:" + services.toString());
            setNotification();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.d(TAG, "onCharacteristicChanged 2 gatt=" + gatt
                    + ", characteristic=" + characteristic
                    + ", value=" + Arrays.toString(characteristic.getValue()));

            //接收数据
            byte[] bytes = characteristic.getValue();
//            int speed = bytes2Hex(bytes);
//            sendBroadcast(speed + "", INCOMING_MSG);
            onData(bytes);
        }

        @Override
        public void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.d(TAG, "onCharacteristicRead:" + characteristic.toString());
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.d(TAG, "onCharacteristicWrite gatt=" + gatt + ", characteristic value="
                    + Arrays.toString(characteristic.getValue()) + ", status=" + status);

        }
    };

    public void writeBle(byte[] value) {
        doList.add(value);
    }

    public void realWriteBle(byte[] value) {
        StringBuilder msg = new StringBuilder(" writeBle data size:" + value.length + " ;data: ");
        for (byte b : value) {
            msg.append(BleDataUtils.byte2HexStr(b)).append(" ");
        }
        Log.d(TAG, msg.toString());
        BluetoothGattService service = bluetoothGatt.getService(UUID.fromString(WRITE_SERVICE_UUID));
        if (service == null) {
            return;
        }
        BluetoothGattCharacteristic writeChar = service.getCharacteristic(UUID.fromString(WRITE_DATA_UUID));

        writeChar.setValue(value);
        bluetoothGatt.writeCharacteristic(writeChar);
    }


    //处理接收到到数据
    public void onData(byte[] datas) {
        StringBuilder msg = new StringBuilder("data size:" + datas.length + " ;data: ");
        for (byte b : datas) {
            msg.append(BleDataUtils.byte2HexStr(b)).append(" ");
        }
        Log.d(TAG, msg.toString());

        if (datas.length < 5) {
            return;
        }
        String cmd = BleDataUtils.byte2HexStr(datas[2]);
        byte data = datas[3];
        onCmdData(cmd, data);
    }

    public void onCmdData(String cmd, byte data) {
        switch (cmd) {
            case MCU_CMD_LED_HEART:
                // 心跳
                realWriteBle(BleDataUtils.heartBeatResponseData());
                break;
            default:
                //
                if (dataCallback != null) {
                    dataCallback.onData(cmd, data);
                }
                break;

        }

    }

    public interface UiConnectCallback {
        void onSuccess(String mac);

        void onFailed(String mac);
    }

    public interface DataCallback {
        void onData(String cmd, byte data);
    }

    private void doWrite() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // 在这里执行你想要每隔100毫秒执行的代码
                if (doList.size() != 0) {
                    realWriteBle(doList.pop());
                }

                // 再次调度此Runnable，实现每隔100毫秒执行
                handler.postDelayed(this, 100);
            }
        };
        handler.postDelayed(runnable, 100);
    }
}

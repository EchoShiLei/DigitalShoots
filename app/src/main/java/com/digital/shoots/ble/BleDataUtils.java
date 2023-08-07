package com.digital.shoots.ble;

public class BleDataUtils {

    // 改变红灯颜色
    public static byte[] getChangeRedData(int i) {
        byte[] value = new byte[6];
        value[0] = (byte) 0XA5;
        value[1] = (byte) 0X06;
        value[2] = (byte) 0X01;
        value[3] = (byte) i;
        // cs
        value[4] = (byte) (value[0] + value[1] + value[2] + value[3]);

        value[5] = (byte) 0XAA;
        return value;
    }

    //倒计时
    public static byte[] getCountdownData() {
        byte[] value = new byte[6];
        value[0] = (byte) 0XA5;
        value[1] = (byte) 0X06;
        value[2] = (byte) 0X07;
        value[3] = (byte) 0X01;
        // cs
        value[4] = (byte) (value[0] + value[1] + value[2] + value[3]);

        value[5] = (byte) 0XAA;
        return value;
    }
}

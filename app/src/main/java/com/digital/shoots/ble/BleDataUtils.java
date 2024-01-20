package com.digital.shoots.ble;

public class BleDataUtils {

    // 改变红灯颜色
    public static byte[] openRedData() {
        return openRedData(random());
    }

    // 改变红灯颜色
    public static byte[] openRedData(int i) {
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


    // 关闭红灯
    public static byte[] closeRedData(int i) {
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

    // 改变蓝灯颜色
    public static byte[] openBlueData() {
        return openBlueData(random());
    }


    // 改变蓝灯颜色
    public static byte[] openBlueData(int i) {
        byte[] value = new byte[6];
        value[0] = (byte) 0XA5;
        value[1] = (byte) 0X06;
        value[2] = (byte) 0X03;
        value[3] = (byte) i;
        // cs
        value[4] = (byte) (value[0] + value[1] + value[2] + value[3]);

        value[5] = (byte) 0XAA;
        return value;
    }

    // 改变红灯颜色
    public static byte[] closeBlueData(int i) {
        byte[] value = new byte[6];
        value[0] = (byte) 0XA5;
        value[1] = (byte) 0X06;
        value[2] = (byte) 0X03;
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


    //倒计时90
    public static byte[] getCountdownData90() {
        byte[] data = new byte[6];
        data[0] = (byte) 0XA5;

        //Length
        data[1] = (byte) 0x06;

        //cmd
        data[2] = (byte) 0X07;

        //Data
        data[3] = (byte) 0X02;

        //CS
        data[4] = (byte) (data[0] + data[1] + data[2] + data[3]);

        //End
        data[5] = (byte) 0XAA;
        return data;
    }

    // App上线
    public static byte[] appOnlineControl() {
        byte[] value = new byte[7];
        value[0] = (byte) 0xA5;
        value[1] = (byte) 0x07;
        value[2] = (byte) 0x22;

        value[3] = (byte) 0x01;
        value[4] = (byte) 0x01;
        // cs
        value[5] = (byte) (value[0] + value[1] + value[2] + value[3] + value[4]);

        value[6] = (byte) 0xAA;
        return value;
    }

    // App下线
    public static byte[] appOfflineControl() {
        byte[] value = new byte[6];
        value[0] = (byte) 0XA5;
        value[1] = (byte) 0X07;
        value[2] = (byte) 0X16;

        value[3] = (byte) 0X00;
        // cs
        value[4] = (byte) (value[0] + value[1] + value[2] + value[3]);

        value[5] = (byte) 0XAA;
        return value;
    }

    // 关闭所有灯
    public static byte[] closeAllLight() {
        byte[] value = new byte[6];
        value[0] = (byte) 0XA5;
        value[1] = (byte) 0X06;
        value[2] = (byte) 0X01;

        value[3] = (byte) 0XC0;
        // cs
        value[4] = (byte) (value[0] + value[1] + value[2] + value[3]);

        value[5] = (byte) 0XAA;
        return value;
    }

    // 打开所有灯
    public static byte[] openAllLight() {
        byte[] value = new byte[6];
        value[0] = (byte) 0XA5;
        value[1] = (byte) 0X06;
        value[2] = (byte) 0X01;

        value[3] = (byte) 0XC0;
        // cs
        value[4] = (byte) (value[0] + value[1] + value[2] + value[3]);

        value[5] = (byte) 0XAA;
        return value;
    }

    // 关闭所有灯
    public static byte[] closeAllBlueLight() {
        byte[] value = new byte[6];
        value[0] = (byte) 0XA5;
        value[1] = (byte) 0X06;
        value[2] = (byte) 0X01;

        value[3] = (byte) 0X80;
        // cs
        value[4] = (byte) (value[0] + value[1] + value[2] + value[3]);

        value[5] = (byte) 0XAA;
        return value;
    }

    // 打开所有蓝灯
    public static byte[] openAllBlueLight() {
        byte[] value = new byte[6];
        value[0] = (byte) 0XA5;
        value[1] = (byte) 0X06;
        value[2] = (byte) 0X01;

        value[3] = (byte) 0XBF;
        // cs
        value[4] = (byte) (value[0] + value[1] + value[2] + value[3]);

        value[5] = (byte) 0XAA;
        return value;
    }

    // 关闭所有灯
    public static byte[] closeAllRedLight() {
        byte[] value = new byte[6];
        value[0] = (byte) 0XA5;
        value[1] = (byte) 0X06;
        value[2] = (byte) 0X01;

        value[3] = (byte) 0X80;
        // cs
        value[4] = (byte) (value[0] + value[1] + value[2] + value[3]);

        value[5] = (byte) 0XAA;
        return value;
    }

    //    显示得分
    public static byte[] showNumber(int number) {
        byte[] value = new byte[8];
        value[0] = (byte) 0XA5;
        value[1] = (byte) 0X08;
        value[2] = (byte) 0X09;

        value[3] = (byte) 0X80;
        // cs
        value[4] = (byte) (value[0] + value[1] + value[2] + value[3]);

        value[5] = (byte) 0XAA;
        return value;
    }


    public static int random() {
        return (int) (1 + Math.random() * (6));
    }

    public static int bytes2HexInt(byte[] b) {
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

    public static String byte2HexStr(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return hex;
    }
}

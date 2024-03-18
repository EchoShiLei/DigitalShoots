package com.digital.shoots.ble;

public class BleDataUtils {
    public static boolean isOnline;

    // 改变红灯颜色
    public static byte[] openRedData() {
        return openRedData(random());
    }

    // 改变红灯颜色
    public static byte[] openRedData(int index) {
        StringBuilder cmd = new StringBuilder("01");
        for (int i = 1; i < 7; i++) {
            if (i == index) {
                cmd.append("1");
            } else {
                cmd.append("0");
            }
        }
        byte decimal = bit2byte(cmd.toString());
        byte[] value = new byte[6];
        value[0] = (byte) 0XA5;
        value[1] = (byte) 0X06;
        value[2] = (byte) 0X01;
        value[3] = (byte) decimal;
        // cs
        value[4] = (byte) (value[0] + value[1] + value[2] + value[3]);

        value[5] = (byte) 0XAA;
        return value;
    }


    // 关闭红灯
    public static byte[] closeRedData() {
        byte decimal = bit2byte("01000000");
        byte[] value = new byte[6];
        value[0] = (byte) 0XA5;
        value[1] = (byte) 0X06;
        value[2] = (byte) 0X01;
        value[3] = (byte) decimal;
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
    public static byte[] openBlueData(int index) {
        StringBuilder cmd = new StringBuilder("10");
        for (int i = 1; i < 7; i++) {
            if (i == index) {
                cmd.append("1");
            } else {
                cmd.append("0");
            }
        }
        byte decimal = bit2byte(cmd.toString());
        byte[] value = new byte[6];
        value[0] = (byte) 0XA5;
        value[1] = (byte) 0x06;
        value[2] = (byte) 0X01;
        value[3] = (byte) decimal;
        // cs
        value[4] = (byte) (value[0] + value[1] + value[2] + value[3]);

        value[5] = (byte) 0XAA;
        return value;
    }

    // 改变红灯颜色
    public static byte[] closeBlueData() {
        byte decimal = bit2byte("10000000");
        byte[] value = new byte[6];
        value[0] = (byte) 0XA5;
        value[1] = (byte) 0X06;
        value[2] = (byte) 0X03;
        value[3] = (byte) decimal;
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

        int three = number % 10 + 48;
        int two = ((int) (number / 10)) % 10 + 48;
        int one = ((int) (number / 100)) % 10 + 48;
        //Data
        value[3] = (byte) one;
        value[4] = (byte) two;
        value[5] = (byte) three;

        //CS
        value[6] = (byte) (value[0] + value[1] + value[2] + value[3] + value[4] + value[5]);
        // data[4] = 0xB3;

        //End
        value[7] = (byte) 0XAA;
        return value;
    }


    public static int random() {
        return (int) (1 + Math.random() * (6));
    }


    //心跳查询
    public static byte[] heartBeatRequesrData() {
        byte[] data = new byte[5];

        // Start
        data[0] = (byte) 0XA5;

        //Length
        data[1] = (byte) 0x05;

        //cmd
        data[2] = (byte) 0X30;


        //CS
        data[3] = (byte) (data[0] + data[1] + data[2]);
        // data[4] = 0xB3;

        //End
        data[4] = (byte) 0XAA;
        // NSLog(@"心跳查询--%@",adata);

        return data;
    }

    //心跳响应
    public static byte[] heartBeatResponseData() {
        byte[] data = new byte[5];

        // Start
        data[0] = (byte) 0XA5;

        //Length
        data[1] = (byte) 0x05;

        //cmd
        data[2] = (byte) 0X31;

        //Data

        //CS
        data[3] = (byte) (data[0] + data[1] + data[2]);
        // data[4] = 0xB3;

        //End
        data[4] = (byte) 0XAA;
        // NSLog(@"心跳响应--%@",adata);

        return data;
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


    public static byte bit2byte(String bString) {
        byte result = 0;
        for (int i = bString.length() - 1, j = 0; i >= 0; i--, j++) {
            result += (Byte.parseByte(bString.charAt(i) + "") * Math.pow(2, j));
        }
        return result;
    }


    public static int getScore(int index) {
        switch (index) {
            case 1:
                return 11;
            case 2:
                return 25;
            case 3:
                return 9;
            case 4:
                return 13;
            case 5:
                return 23;
            case 6:
                return 9;
        }
        return -1;

    }
}

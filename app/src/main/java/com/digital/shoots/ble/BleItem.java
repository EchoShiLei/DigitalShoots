package com.digital.shoots.ble;

public class BleItem {
    public BleItem(String name, String mac) {
        this.mac = mac;
        this.name = name;
    }

    public String mac;
    public String name;
    public boolean isConnect;
}

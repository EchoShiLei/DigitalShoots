package com.digital.shoots.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class SpUtil{

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private static SpUtil spUtil;
    public static String KEY_LAST_BLE_MAC="key_last_ble_mac";
    public static String KEY_LAST_BLE_MAC_SPEED="key_last_ble_mac_speed";

    @SuppressLint("CommitPrefEdits")
    private SpUtil(Context context){
        sharedPreferences = context.getSharedPreferences("sp_data",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static SpUtil getInstance (Context context){
        if (spUtil == null){
            spUtil = new SpUtil(context);
        }
        return spUtil;
    }
    public void putInt(String key, int num){
        editor.putInt(key, num);
        editor.apply();
    }

    public void putString(String key, String content){
        editor.putString(key, content);
        editor.apply();
    }

    public void putBoolean(String key, boolean value){
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void putLong(String key, long value){
        editor.putLong(key, value);
        editor.apply();
    }

    //读取
    public int getInt(String key){
        return sharedPreferences.getInt(key, 0);
    }

    public String getString(String key){
        return sharedPreferences.getString(key,"");
    }

    public boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key,false);
    }

    public long getLong(String key){
        return sharedPreferences.getLong(key,0);
    }
}

package com.digital.shoots;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.digital.shoots.utils.ThreadPoolManager;
import com.zyq.easypermission.EasyPermissionHelper;

public class DigitalApplication extends Application {

    private static Context mContext;
    public static Handler mainHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=getApplicationContext();
        mainHandler= new Handler();
        EasyPermissionHelper.getInstance().init(this);
        ThreadPoolManager.getInstance().initThreadPool();
    }

    public static Context getContext(){
        return mContext;
    }
}

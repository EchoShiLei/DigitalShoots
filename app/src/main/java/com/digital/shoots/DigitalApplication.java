package com.digital.shoots;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.digital.shoots.db.greendao.GreenDaoManager;
import com.digital.shoots.db.greendao.bean.GameAchievement;
import com.digital.shoots.utils.ThreadPoolManager;
import com.zyq.easypermission.EasyPermissionHelper;

import java.util.List;
import java.util.Random;

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
        GreenDaoManager.getDaoSession(this);
    }

    public static Context getContext(){
        return mContext;
    }
}

package com.digital.shoots.db.greendao;

import android.app.Activity;

import com.digital.shoots.base.SpUtil;
import com.digital.shoots.db.greendao.bean.User;

public class UserDataManager {
    private boolean mIsChangeUserInfo = false;
    private User mUser = new User();
    private static volatile UserDataManager mHelper;

    private UserDataManager() {
    }

    /**
     * Constructor privatization
     * 获取单例对象 mHelper
     *
     * @return
     */
    public static UserDataManager getInstance() {
        if (mHelper == null) {
            synchronized (UserDataManager.class) {
                if (mHelper == null) {
                    mHelper = new UserDataManager();
                }
            }
        }
        return mHelper;
    }

    public User getUser() {
        return mUser;
    }

    public void setUserIconPath(Activity activity, String userIconPath) {
        mIsChangeUserInfo = true;
        mUser.iconPath = userIconPath;
        SpUtil.getInstance(activity).putString(User.KEY_ICON_PATH, userIconPath);
    }

    public void setUserName(Activity activity, String useName) {
        mIsChangeUserInfo = true;
        mUser.name = useName;
        SpUtil.getInstance(activity).putString(User.KEY_USER_NAME, useName);
    }


    public void setUserTeamName(Activity activity, String teamName) {
        mIsChangeUserInfo = true;
        mUser.teamName = teamName;
        SpUtil.getInstance(activity).putString(User.KEY_TEAM_NAME, teamName);
    }


    public void setUserBirthdate(Activity activity, String birthdate) {
        mIsChangeUserInfo = true;
        mUser.birthdate = birthdate;
        SpUtil.getInstance(activity).putString(User.KEY_USER_BIRTHDATE, birthdate);
    }

    public void fullUser(Activity activity) {
        mUser.iconPath = SpUtil.getInstance(activity).getString(User.KEY_ICON_PATH);
        mUser.name = SpUtil.getInstance(activity).getString(User.KEY_USER_NAME);
        mUser.teamName = SpUtil.getInstance(activity).getString(User.KEY_TEAM_NAME);
        mUser.birthdate = SpUtil.getInstance(activity).getString(User.KEY_USER_BIRTHDATE);
    }

}

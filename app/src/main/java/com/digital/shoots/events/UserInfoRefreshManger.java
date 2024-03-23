package com.digital.shoots.events;

import java.util.ArrayList;
import java.util.List;

public class UserInfoRefreshManger {

    private final List<IUserInfoRefreshEvent> mInfoRefreshEvents = new ArrayList<>();
    private static UserInfoRefreshManger mInstance;

    private UserInfoRefreshManger() {
    }

    public static UserInfoRefreshManger getInstance() {
        if (mInstance == null) {
            synchronized (UserInfoRefreshManger.class) {
                if (mInstance == null) {
                    mInstance = new UserInfoRefreshManger();
                }
            }
        }
        return mInstance;
    }

    public void addInfoRefreshEvents(IUserInfoRefreshEvent iUserInfoRefreshEvent) {
        mInfoRefreshEvents.add(iUserInfoRefreshEvent);
    }

    /**
     * 通知用户信息刷新
     */
    public void notifyUserInfoRefresh() {
        for (IUserInfoRefreshEvent event : mInfoRefreshEvents) {
            event.onUserInfoRefresh();
        }
    }

    /**
     * 通知游戏数据刷新
     */
    public void notifyPlayDataRefresh() {
        for (IUserInfoRefreshEvent event : mInfoRefreshEvents) {
            event.onUserInfoRefresh();
        }
    }

    public void destroyInfoRefreshEvents(IUserInfoRefreshEvent iUserInfoRefreshEvent) {
        mInfoRefreshEvents.remove(iUserInfoRefreshEvent);
    }
}

package com.digital.shoots.events;

public interface IUserInfoRefreshEvent {

    //用户形象更新
    default void onUserInfoRefresh() {
    }

    /**
     * 游戏数据更新
     */
    default void playDataRefresh() {
    }
}

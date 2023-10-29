package com.digital.shoots.stats;

import android.content.Context;

public abstract class BaseStatsPager {
    public Context mContext;
    public HolderStatsFragment mHolder;

    public BaseStatsPager(Context context, HolderStatsFragment holder) {
        mContext = context;
        mHolder = holder;
    }

    abstract void initView();

}

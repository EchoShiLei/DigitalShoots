package com.digital.shoots.stats;

public abstract class BaseStatsPager {
    public HolderStatsFragment mHolder;

    public BaseStatsPager(HolderStatsFragment holder) {
        this.mHolder = holder;
    }

    abstract void initView();

}

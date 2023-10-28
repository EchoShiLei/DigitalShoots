package com.digital.shoots.stats;

public class PagerStatsFirst extends BaseStatsPager {

    private HolderStatsFirstFragment mStatsFirstFragmentsHolder;

    public PagerStatsFirst(HolderStatsFragment holder) {
        super(holder);
    }

    @Override
    public void initView() {
        if (!(mHolder instanceof HolderStatsFirstFragment)) {
            return;
        }
        mStatsFirstFragmentsHolder = (HolderStatsFirstFragment) mHolder;
        mStatsFirstFragmentsHolder.mTvScoreNum.setText("102");
        mStatsFirstFragmentsHolder.mTvSpeedNum.setText("113");
    }
}

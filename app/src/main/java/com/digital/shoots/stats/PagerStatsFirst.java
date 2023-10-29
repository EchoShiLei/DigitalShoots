package com.digital.shoots.stats;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.digital.shoots.R;
import com.digital.shoots.utils.Utils;

public class PagerStatsFirst extends BaseStatsPager {

    private HolderStatsFirstFragment mFirstHolder;
    private Context mContext;

    public PagerStatsFirst(Context context, HolderStatsFragment holder) {
        super(context, holder);
    }

    @Override
    public void initView() {
        if (!(mHolder instanceof HolderStatsFirstFragment)) {
            return;
        }
        mFirstHolder = (HolderStatsFirstFragment) mHolder;

        mFirstHolder.mIvScoreStatus.post(new Runnable() {
            @Override
            public void run() {
                initScoreView(0);
            }
        });

        mFirstHolder.mTvSpeedNum.setText("113");
        mFirstHolder.mIvStatsProgress.post(new Runnable() {
            @Override
            public void run() {
                moveProgress(3);
            }
        });
    }


    private void initScoreView(int score) {
        ViewGroup.LayoutParams layoutParams = mFirstHolder.mScoreIndicator.getLayoutParams();
        if (!(layoutParams instanceof RelativeLayout.LayoutParams)) {
            return;
        }

        RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) layoutParams;
        mFirstHolder.mScoreIndicator.setPivotX(mFirstHolder.mScoreIndicator.getWidth() / 2);
        mFirstHolder.mScoreIndicator.setPivotY(mFirstHolder.mScoreIndicator.getHeight() / 2);
        int height = mFirstHolder.mIvScoreStatus.getHeight();
        int width = mFirstHolder.mIvScoreStatus.getWidth();

        if (score == 1) {
            mFirstHolder.mTvScoreNum.setText(String.valueOf(20));
            mFirstHolder.mIvScoreStatus.setImageDrawable(Utils.getDrawable(mContext, R.drawable.stats_score_1));
            mFirstHolder.mScoreIndicator.setRotation(90);
            int top = height * 4 / 6;
            layoutParams1.setMargins(Utils.dp2px(mContext, 4.0f), top, 0, 0);
            mFirstHolder.mScoreIndicator.setLayoutParams(layoutParams1);
        }
    }

    /**
     * 从0开始最大3
     *
     * @param step
     */
    private void moveProgress(int step) {
        ViewGroup.LayoutParams layoutParams = mFirstHolder.mFlStatsIndicator.getLayoutParams();
        if (layoutParams instanceof RelativeLayout.LayoutParams) {
            int width = mFirstHolder.mIvStatsProgress.getWidth();
            int preStepWith = width / 4;
            RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) layoutParams;
            layoutParams1.setMargins(step * preStepWith, layoutParams1.topMargin, layoutParams1.rightMargin, layoutParams1.bottomMargin);
            mFirstHolder.mFlStatsIndicator.setLayoutParams(layoutParams1);
        }
    }
}

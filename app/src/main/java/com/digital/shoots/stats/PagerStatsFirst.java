package com.digital.shoots.stats;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.DrawableRes;
import androidx.viewpager2.widget.ViewPager2;

import com.digital.shoots.R;
import com.digital.shoots.utils.Utils;

import java.util.Calendar;

public class PagerStatsFirst extends BaseStatsPager {
    private static final String[] monthStr = {"Jan.", "Feb.", "Mar.", "Apr.", "May.", "Jun.", "Jul.", "Aug.", "Sep.", "Oct.", "Nov.", "Dec."};
    private HolderStatsFirstFragment mFirstHolder;
    private Context mContext;
    private ViewPager2 mViewPager2;

    public PagerStatsFirst(Context context, HolderStatsFragment holder, ViewPager2 viewPager2) {
        super(context, holder);
        mViewPager2 = viewPager2;
    }

    @Override
    public void initView() {
        if (!(mHolder instanceof HolderStatsFirstFragment)) {
            return;
        }
        mFirstHolder = (HolderStatsFirstFragment) mHolder;
        mFirstHolder.mUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mFirstHolder.mUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager2.setCurrentItem(1);
            }
        });

        Calendar instance = Calendar.getInstance();
        int year = instance.get(Calendar.YEAR);
        int month = instance.get(Calendar.MONTH);
        try {
            mFirstHolder.mTvTime.setText(String.format("%s%d", monthStr[month], year));
        } catch (Exception e) {
        }
        mFirstHolder.mIvScoreStatus.post(new Runnable() {
            @Override
            public void run() {
                initScoreView(150);
                moveByScoreProgress(150);
                initSpeedView(200);
            }
        });
        mFirstHolder.mIvStatsProgress.post(new Runnable() {
            @Override
            public void run() {
            }
        });
    }


    private void initScoreView(int score) {
        mFirstHolder.mTvScoreNum.setText(String.valueOf(score));
        mFirstHolder.mIvScoreStatus.setImageDrawable(Utils.getDrawable(mContext, getScoreIvId(score)));
    }

    private void initSpeedView(int speed) {
        mFirstHolder.mTvSpeedNum.setText(String.valueOf(speed));
        mFirstHolder.mIvSpeedStatus.setImageDrawable(Utils.getDrawable(mContext, getSpeedIvId(speed)));
        mFirstHolder.mIvSpeedEmoji.setImageDrawable(Utils.getDrawable(mContext, speed >= 50 ? R.drawable.emoji_good : R.drawable.emoji_pro));
    }


    private @DrawableRes int getScoreIvId(int score) {
        int ivId = 0;
        if (score < 75) {
            ivId = R.drawable.stats_score_1;
        } else if (score <= 150) {
            ivId = R.drawable.stats_score_2;
        } else if (score <= 225) {
            ivId = R.drawable.stats_score_3;
        } else {
            ivId = R.drawable.stats_score_4;
        }

        return ivId;
    }

    private @DrawableRes int getSpeedIvId(int speed) {
        int ivId = 0;
        if (speed < 50) {
            ivId = R.drawable.stats_speed_1;
        } else if (speed <= 80) {
            ivId = R.drawable.stats_speed_2;
        } else {
            ivId = R.drawable.stats_speed_3;
        }

        return ivId;
    }

    /**
     * 从0开始最大3
     *
     * @param score
     */
    private void moveByScoreProgress(int score) {
        int ivId = 0;
        int step = 0;
        if (score < 75) {
            ivId = R.drawable.stats_indicator_1;
            step = 1;
        } else if (score <= 150) {
            ivId = R.drawable.stats_indicator_2;
            step = 2;
        } else if (score <= 225) {
            ivId = R.drawable.stats_indicator_3;
        } else {
            ivId = R.drawable.stats_indicator_4;
            step = 4;
        }

        mFirstHolder.mIvStatsProgress.setImageDrawable(Utils.getDrawable(mContext, ivId));

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

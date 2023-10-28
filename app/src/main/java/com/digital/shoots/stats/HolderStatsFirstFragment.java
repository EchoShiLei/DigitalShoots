package com.digital.shoots.stats;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.digital.shoots.R;

public class HolderStatsFirstFragment extends HolderStatsFragment {


    public ImageView mUserIcon;
    public TextView mUserName;
    public TextView mTvTime;
    public ImageView mIvScoreStatus;
    public TextView mTvScoreNum;
    public TextView mTvScoreStatus;
    public ImageView mIvSpeedStatus;
    public TextView mTvSpeedNum;
    public TextView mTvSpeedStatus;
    public FrameLayout mFlStatsIndicator;
    public ImageView mIvStatsIndicator;

    public HolderStatsFirstFragment(@NonNull View itemView) {
        super(itemView);
        mUserIcon = itemView.findViewById(R.id.iv_user_icon);
        mUserName = itemView.findViewById(R.id.tv_user_name);
        mTvTime = itemView.findViewById(R.id.tv_time);

        mIvScoreStatus = itemView.findViewById(R.id.iv_stats_score_status);
        mTvScoreNum = itemView.findViewById(R.id.tv_stats_score_num);
        mTvScoreStatus = itemView.findViewById(R.id.tv_stats_score_status);

        mIvSpeedStatus = itemView.findViewById(R.id.iv_stats_speed_status);
        mTvSpeedNum = itemView.findViewById(R.id.tv_stats_speed_num);
        mTvSpeedStatus = itemView.findViewById(R.id.tv_stats_speed_status);

        mFlStatsIndicator = itemView.findViewById(R.id.fl_stats_indicator);
        mIvStatsIndicator = itemView.findViewById(R.id.iv_stats_indicator);
    }

}

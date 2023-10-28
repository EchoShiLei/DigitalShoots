package com.digital.shoots.stats;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.digital.shoots.R;
import com.github.mikephil.charting.charts.LineChart;

public class HolderStatsSecondFragment extends HolderStatsFragment {


    public final ImageView mIvUserIcon;
    public final TextView mTvScoreNum;
    public final LinearLayout mLlDataTime;
    public final LineChart mLineChart;

    public HolderStatsSecondFragment(@NonNull View itemView) {
        super(itemView);
        mIvUserIcon = itemView.findViewById(R.id.iv_user_icon);
        mTvScoreNum = itemView.findViewById(R.id.tv_stats_second_pager_score_num);
        mLlDataTime = itemView.findViewById(R.id.ll_data_time);
        mLineChart = itemView.findViewById(R.id.lineChart);
    }

}

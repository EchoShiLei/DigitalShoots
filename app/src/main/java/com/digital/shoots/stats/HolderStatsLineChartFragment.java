package com.digital.shoots.stats;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.digital.shoots.R;
import com.github.mikephil.charting.charts.LineChart;

public class HolderStatsLineChartFragment extends HolderStatsFragment {


    public final ImageView mIvUserIcon;
    public final TextView mTvScoreNum;
    public final LinearLayout mLlDataTime;
    public final LinearLayout mLlPlayView;
    public final LineChart mLineChart;

    public HolderStatsLineChartFragment(@NonNull View itemView) {
        super(itemView);
        mIvUserIcon = itemView.findViewById(R.id.iv_user_icon);
        mTvScoreNum = itemView.findViewById(R.id.tv_stats_second_pager_score_num);
        mLlDataTime = itemView.findViewById(R.id.ll_data_time);
        mLlPlayView = itemView.findViewById(R.id.ll_video_play);
        mLineChart = itemView.findViewById(R.id.lineChart);
    }

}

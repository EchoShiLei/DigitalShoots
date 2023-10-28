package com.digital.shoots.stats;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.digital.shoots.R;
import com.github.mikephil.charting.charts.BarChart;

public class HolderStatsThirdFragment extends HolderStatsFragment {

    public final ImageView mIvUserIcon;
    public final TextView mTvSeepNum;
    public final LinearLayout mLlDataTime;
    public final BarChart mBarChart;

    public HolderStatsThirdFragment(@NonNull View itemView) {
        super(itemView);
        mIvUserIcon = itemView.findViewById(R.id.iv_user_icon);
        mTvSeepNum = itemView.findViewById(R.id.tv_stats_third_pager_speed_num);
        mLlDataTime = itemView.findViewById(R.id.ll_data_time);
        mBarChart = itemView.findViewById(R.id.barChart);
    }

}

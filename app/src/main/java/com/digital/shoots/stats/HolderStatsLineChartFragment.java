package com.digital.shoots.stats;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.digital.shoots.R;
import com.digital.shoots.db.greendao.GreenDaoManager;
import com.github.mikephil.charting.charts.LineChart;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYStateUiListener;
import com.shuyu.gsyvideoplayer.listener.GSYVideoProgressListener;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

public class HolderStatsLineChartFragment extends HolderStatsFragment {


    public final ImageView mIvUserIcon;
    public final TextView mTvScoreNum;
    public final LinearLayout mLlDataTime;
    public final LinearLayout mLlPlayView;
    public final LineChart mLineChart;

    public final StandardGSYVideoPlayer videoPlayer;

    public HolderStatsLineChartFragment(@NonNull View itemView) {
        super(itemView);
        mIvUserIcon = itemView.findViewById(R.id.iv_user_icon);
        mTvScoreNum = itemView.findViewById(R.id.tv_stats_second_pager_score_num);
        mLlDataTime = itemView.findViewById(R.id.ll_data_time);
        mLlPlayView = itemView.findViewById(R.id.ll_video_play);
        mLineChart = itemView.findViewById(R.id.lineChart);
        videoPlayer = itemView.findViewById(R.id.video_player);
    }


    public void destroy() {
//        GSYVideoManager.releaseAllVideos();
//        //释放所有
//        videoPlayer.setVideoAllCallBack(null);
    }

}

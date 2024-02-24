package com.digital.shoots.stats;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.digital.shoots.db.greendao.GreenDaoManager;
import com.digital.shoots.db.greendao.bean.GameAchievement;
import com.digital.shoots.utils.Utils;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.shuyu.gsyvideoplayer.listener.GSYStateUiListener;
import com.shuyu.gsyvideoplayer.listener.GSYVideoProgressListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PagerLineChart extends BaseStatsPager {

    private HolderStatsLineChartFragment mLineChartHolder;

    public PagerLineChart(Context context, HolderStatsFragment holder) {
        super(context, holder);
    }

    @Override
    public void initView() {
        if (!(mHolder instanceof HolderStatsLineChartFragment)) {
            return;
        }
        initData();
        mLineChartHolder = (HolderStatsLineChartFragment) mHolder;
        mLineChartHolder.mLineChart.setRenderer(new LineCharTextRenderer(mLineChartHolder.mLineChart,
                mLineChartHolder.mLineChart.getAnimator(),
                mLineChartHolder.mLineChart.getViewPortHandler()));
        mLineChartHolder.mLlDataTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mLineChartHolder.mLlPlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("zyw", "mLlPlayView onClick2");
                play();
            }
        });

        //查询最高分
        List<GameAchievement> highestScores = GreenDaoManager.getHighestScores();
        if (highestScores.size() > 0) {
            int maxScore = highestScores.get(0).getScore();
            mLineChartHolder.mTvScoreNum.setText(String.valueOf(maxScore));
        }

        mLineChartHolder.mLineChart.getDescription().setEnabled(false);
        mLineChartHolder.mLineChart.setTouchEnabled(false);
        mLineChartHolder.mLineChart.getLegend().setEnabled(false);
        //设置X轴
        XAxis xAxis = mLineChartHolder.mLineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(10, true);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(10);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //设置Y轴
        YAxis yAxis = mLineChartHolder.mLineChart.getAxisLeft();
        YAxis axisRight = mLineChartHolder.mLineChart.getAxisRight();
        axisRight.setEnabled(false);
        yAxis.setDrawGridLines(false);
        yAxis.setDrawAxisLine(true);
        mLineChartHolder.mLineChart.setData(setData());
    }

    private void initData() {
        //创建假数据
        Random random = new Random();
        int score = 0;
        for (int i = 0; i < 30; i++) {
            score = 50 + (int) (Math.random() * 99 + 1);
//            GreenDaoManager.insert(new GameAchievement(System.currentTimeMillis() + random.nextInt(1000000),
//                    0, score, 50, "20231021", ""));
        }


    }

    private LineData setData() {
//        创建一个Entry类型的集合，并添加数据
        List<Entry> entries = new ArrayList<>();
        List<GameAchievement> top10Scores = GreenDaoManager.getTop10Scores("20231021");
        for (int i = 0; i < top10Scores.size(); i++) {
//            添加Entry对象，传入纵轴的索引和纵轴的值
//            entries.add(new Entry(i + 1, top10Scores.get(i).getScore()));
        }

//        实例化LineDataSet类，并将Entry集合中的数据和这组数据名(或者说这个图形名)，通过这个类可以对线段进行设置
        LineDataSet lineDataSet = new LineDataSet(entries, "线型图测试");
        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        lineDataSet.setColor(Color.parseColor("#848283"));
        lineDataSet.setLineWidth(Utils.dp2px(mContext, 1));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setCircleColor(Color.parseColor("#848283"));
        lineDataSet.setCircleHoleColor(Color.parseColor("#ffffff"));
//        这个就是线型图所需的数据了
        LineData lineData = new LineData(lineDataSet);
        return lineData;
    }


    private void play() {
        mLineChartHolder.videoPlayer.setVisibility(View.VISIBLE);
        List<GameAchievement> test = GreenDaoManager.queryAll();
        for (GameAchievement achievement :
                test) {
            Log.i("zyw", "achievement source1 = " + achievement.toString());
        }
        String source1 = test.get(test.size() - 1).getVideoPath();
        Log.i("zyw", "play source1 = " + source1);
        mLineChartHolder.videoPlayer.setUp(source1, true, "测试视频");

        //增加title
        mLineChartHolder.videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        //设置返回键
        mLineChartHolder.videoPlayer.getBackButton().setVisibility(View.VISIBLE);
        mLineChartHolder.videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLineChartHolder.videoPlayer.setVisibility(View.GONE);
            }
        });
        //是否可以滑动调整
        mLineChartHolder.videoPlayer.setIsTouchWiget(true);
        //不需要屏幕旋转
        mLineChartHolder.videoPlayer.setNeedOrientationUtils(false);

        mLineChartHolder.videoPlayer.startPlayLogic();
        mLineChartHolder.videoPlayer.setGSYVideoProgressListener(new GSYVideoProgressListener() {
            @Override
            public void onProgress(long progress, long secProgress, long currentPosition, long duration) {
                Log.i("zyw", "progress = " + progress);
                Log.i("zyw", "secProgress = " + secProgress);
                Log.i("zyw", "currentPosition = " + currentPosition);
                Log.i("zyw", "duration = " + duration);
            }
        });
        mLineChartHolder.videoPlayer.setGSYStateUiListener(new GSYStateUiListener() {
            @Override
            public void onStateChanged(int state) {
                Log.i("zyw", "state = " + state);
            }
        });
    }
}

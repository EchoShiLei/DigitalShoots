package com.digital.shoots.stats;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.digital.shoots.db.greendao.GreenDaoManager;
import com.digital.shoots.db.greendao.bean.GameAchievement;
import com.digital.shoots.utils.Utils;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PagerStatsSecond extends BaseStatsPager {

    private HolderStatsSecondFragment mSecondHolder;

    public PagerStatsSecond(Context context, HolderStatsFragment holder) {
        super(context, holder);
    }

    @Override
    public void initView() {
        if (!(mHolder instanceof HolderStatsSecondFragment)) {
            return;
        }
        initData();
        mSecondHolder = (HolderStatsSecondFragment) mHolder;
        mSecondHolder.mLlDataTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mSecondHolder.mLlPlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //查询最高分
        List<GameAchievement> highestScores = GreenDaoManager.getHighestScores();
        if (highestScores.size() > 0) {
            int maxScore = highestScores.get(0).getScore();
            mSecondHolder.mTvScoreNum.setText( String.valueOf(maxScore));
        }

        mSecondHolder.mLineChart.getDescription().setEnabled(false);
        mSecondHolder.mLineChart.setTouchEnabled(false);
        mSecondHolder.mLineChart.getLegend().setEnabled(false);
        //设置X轴
        XAxis xAxis = mSecondHolder.mLineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //设置Y轴
        YAxis yAxis = mSecondHolder.mLineChart.getAxisLeft();
        YAxis axisRight = mSecondHolder.mLineChart.getAxisRight();
        axisRight.setEnabled(false);
        yAxis.setDrawGridLines(false);
        yAxis.setDrawAxisLine(true);
        mSecondHolder.mLineChart.setData(setData());
    }

    private void initData() {
        //创建假数据
        Random random = new Random();
        int score = 0;
        for (int i = 0; i < 30; i++) {
            score = 50 + (int) (Math.random() * 99 + 1);
            GreenDaoManager.insert(new GameAchievement(System.currentTimeMillis() + random.nextInt(1000000),
                    0, score, 50, "20231021", ""));
        }


    }

    private LineData setData() {
//        创建一个Entry类型的集合，并添加数据
        List<Entry> entries = new ArrayList<>();
        List<GameAchievement> top10Scores = GreenDaoManager.getTop10Scores("20231021");
        for (int i = 0; i < top10Scores.size(); i++) {
//            添加Entry对象，传入纵轴的索引和纵轴的值
            entries.add(new Entry(i + 1, top10Scores.get(i).getScore()));
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
}

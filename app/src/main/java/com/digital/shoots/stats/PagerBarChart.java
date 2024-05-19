package com.digital.shoots.stats;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.digital.shoots.R;
import com.digital.shoots.db.greendao.GreenDaoManager;
import com.digital.shoots.db.greendao.UserDataManager;
import com.digital.shoots.db.greendao.bean.GameAchievement;
import com.digital.shoots.db.greendao.bean.User;
import com.digital.shoots.events.IUserInfoRefreshEvent;
import com.digital.shoots.events.UserInfoRefreshManger;
import com.digital.shoots.tab.ChangePagerListener;
import com.digital.shoots.utils.ImageUtils;
import com.digital.shoots.utils.Utils;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class PagerBarChart extends BaseStatsPager {

    private HolderStatsBarChartFragment mBarChartFragmentsHolder;
    private ChangePagerListener mChangePagerListener;
    private BarCharTextRenderer mBarCharTextRenderer;
    private IUserInfoRefreshEvent mIUserInfoRefreshEvent = new IUserInfoRefreshEvent() {
        @Override
        public void onUserInfoRefresh() {
            User user = UserDataManager.getInstance().getUser();
            if (!TextUtils.isEmpty(user.iconPath)) {
                ImageUtils.loadLocalPic((Activity) mContext, mBarChartFragmentsHolder.mIvUserIcon, user.iconPath);
            }
        }

        @Override
        public void playDataRefresh() {
            refreshBarData();
        }
    };

    public PagerBarChart(Context context, HolderStatsFragment holder, ChangePagerListener changePagerListener) {
        super(context, holder);
        mChangePagerListener = changePagerListener;
        UserInfoRefreshManger.getInstance().addInfoRefreshEvents(mIUserInfoRefreshEvent);
    }


    @Override
    public void initView() {
        if (!(mHolder instanceof HolderStatsBarChartFragment)) {
            return;
        }
        mBarChartFragmentsHolder = (HolderStatsBarChartFragment) mHolder;
        mBarChartFragmentsHolder.mLlDataTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mChangePagerListener != null) {
                    mChangePagerListener.onChangerPager(R.id.myStatsFragment);
                }
            }
        });
        ImageUtils.createCircleImage((Activity) mContext, mBarChartFragmentsHolder.mIvUserIcon);
        initIcon();
        List<GameAchievement> highestSpeeds = GreenDaoManager.getHighestSpeeds();
        if (highestSpeeds.size() > 0) {
            GameAchievement gameAchievement = highestSpeeds.get(0);
            if (gameAchievement != null) {
                mBarChartFragmentsHolder.mLlSpeedLayout.setVisibility(View.VISIBLE);
                mBarChartFragmentsHolder.mTvSeepNum.setText(String.valueOf(gameAchievement.getBlueScore()));
            }
        }
        initBarChartBaseSetting();
        mBarChartFragmentsHolder.mBarChart.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d("ZZQ", "mBarChart: mBarChart");
                mBarChartFragmentsHolder.mBarChart.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int[] location = new int[2];
                mBarChartFragmentsHolder.mBarChart.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];
                int measuredHeight = mBarChartFragmentsHolder.mBarChart.getMeasuredHeight();
                mBarCharTextRenderer.setBottomLocation(measuredHeight + y);
                refreshBarData();
            }
        });

    }

    private void refreshBarData() {
        BarData barData = getBarData();
        if (barData != null) {
            mBarChartFragmentsHolder.mBarChart.setData(barData);
        }
    }

    private BarData getBarData() {
        //        创建一个Entry类型的集合，并添加数据
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
//            添加Entry对象，传入纵轴的索引和纵轴的值
            entries.add(new BarEntry(i, new Random().nextInt(100)));
        }
//        实例化barDataSet类，并将Entry集合中的数据和这组数据名(或者说这个图形名)，通过这个类可以对线段进行设置
        BarDataSet barDataSet = new BarDataSet(entries, "线型图测试");
        barDataSet.setColor(Color.parseColor("#353535"));
//        这个就是线型图所需的数据了
        return new BarData(barDataSet);
    }

    private void initBarChartBaseSetting() {
        mBarCharTextRenderer = new BarCharTextRenderer(mBarChartFragmentsHolder.mBarChart,
                mBarChartFragmentsHolder.mBarChart.getAnimator(),
                mBarChartFragmentsHolder.mBarChart.getViewPortHandler());
        mBarChartFragmentsHolder.mBarChart.setRenderer(mBarCharTextRenderer);
        mBarChartFragmentsHolder.mBarChart.getDescription().setEnabled(false);
        mBarChartFragmentsHolder.mBarChart.setTouchEnabled(false);
        mBarChartFragmentsHolder.mBarChart.getLegend().setEnabled(false);
        //设置X轴
        XAxis xAxis = mBarChartFragmentsHolder.mBarChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        //设置Y轴
        YAxis yAxis = mBarChartFragmentsHolder.mBarChart.getAxisLeft();
        YAxis axisRight = mBarChartFragmentsHolder.mBarChart.getAxisRight();
        axisRight.setEnabled(false);
        yAxis.setDrawGridLines(false);
        yAxis.setDrawAxisLine(true);
    }

    public void initIcon() {
        User user = UserDataManager.getInstance().getUser();
        if (!TextUtils.isEmpty(user.iconPath)) {
            ImageUtils.loadLocalPic((Activity) mContext, mBarChartFragmentsHolder.mIvUserIcon, user.iconPath);
        }
    }

//    private BarData setData() {
////        创建一个Entry类型的集合，并添加数据
//        List<BarEntry> entries = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
////            添加Entry对象，传入纵轴的索引和纵轴的值
//            entries.add(new BarEntry(i, new Random().nextInt(100)));
//        }
////        实例化barDataSet类，并将Entry集合中的数据和这组数据名(或者说这个图形名)，通过这个类可以对线段进行设置
//        BarDataSet barDataSet = new BarDataSet(entries, "线型图测试");
////        这个就是线型图所需的数据了
//        BarData barData = new BarData(barDataSet);
//        return barData;
//    }

    private BarData setData() {
//        创建一个Entry类型的集合，并添加数据
        List<BarEntry> entries = new ArrayList<>();
        Date date = new Date();
        String strDateFormat = "yyyyMMdd";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        String time = sdf.format(date);
        List<GameAchievement> top10Scores = GreenDaoManager.getTop10Scores(time);
        for (int i = 0; i < top10Scores.size(); i++) {
//            添加Entry对象，传入纵轴的索引和纵轴的值
            GameAchievement gameAchievement = top10Scores.get(i);
            int blueScore = gameAchievement.getBlueScore();
            int redScore = gameAchievement.getRedScore();
            int score = Math.max(blueScore, redScore);
            entries.add(new BarEntry(i + 1, score));
        }

        BarDataSet barDataSet = new BarDataSet(entries, "条型图");
        BarData barData = new BarData(barDataSet);
        return barData;
    }

}

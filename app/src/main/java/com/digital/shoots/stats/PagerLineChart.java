package com.digital.shoots.stats;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Outline;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.camera.core.impl.utils.MainThreadAsyncHandler;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.digital.shoots.R;
import com.digital.shoots.db.greendao.GreenDaoManager;
import com.digital.shoots.db.greendao.UserDataManager;
import com.digital.shoots.db.greendao.bean.GameAchievement;
import com.digital.shoots.db.greendao.bean.User;
import com.digital.shoots.events.IUserInfoRefreshEvent;
import com.digital.shoots.events.UserInfoRefreshManger;
import com.digital.shoots.model.BaseModel;
import com.digital.shoots.tab.ChangePagerListener;
import com.digital.shoots.utils.ImageUtils;
import com.digital.shoots.utils.Utils;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PagerLineChart extends BaseStatsPager {
    private int maxScore = 0;
    private HolderStatsLineChartFragment mLineChartHolder;
    private ChangePagerListener mChangePagerListener;
    private Fragment fragment;
    private LineCharTextRenderer mLineCharTextRenderer;
    private IUserInfoRefreshEvent mIUserInfoRefreshEvent = new IUserInfoRefreshEvent() {
        @Override
        public void onUserInfoRefresh() {
            User user = UserDataManager.getInstance().getUser();
            if (!TextUtils.isEmpty(user.iconPath)) {
                ImageUtils.loadLocalPic((Activity) mContext, mLineChartHolder.mIvUserIcon, user.iconPath);
            }
        }

        @Override
        public void playDataRefresh() {
            refreshLineData();
        }
    };


    public PagerLineChart(Context context, HolderStatsFragment holder, ChangePagerListener changePagerListener, Fragment fragment) {
        super(context, holder);
        mChangePagerListener = changePagerListener;
        UserInfoRefreshManger.getInstance().addInfoRefreshEvents(mIUserInfoRefreshEvent);
        this.fragment = fragment;
    }

    @Override
    public void initView() {
        if (!(mHolder instanceof HolderStatsLineChartFragment)) {
            return;
        }
        mLineChartHolder = (HolderStatsLineChartFragment) mHolder;
        ImageUtils.createCircleImage((Activity) mContext, mLineChartHolder.mIvUserIcon);
        initIcon();

        mLineChartHolder.mLlDataTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mChangePagerListener != null) {
                    mChangePagerListener.onChangerPager(R.id.myStatsFragment);
                }
            }
        });

        mLineChartHolder.videoPlayer.setClipToOutline(true);
        mLineChartHolder.videoPlayer.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 80);
            }
        });
        mLineChartHolder.mLlPlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("zyw", "mLlPlayView onClick2");
                play();
            }
        });
        fragment.getViewLifecycleOwner().getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onResume(@NonNull LifecycleOwner owner) {
                DefaultLifecycleObserver.super.onResume(owner);
                Log.i("zyw", "onResume");
            }

            @Override
            public void onPause(@NonNull LifecycleOwner owner) {
                DefaultLifecycleObserver.super.onPause(owner);
                Log.i("zyw", "onPause");
                mLineChartHolder.videoPlayer.setVisibility(View.GONE);
                mLineChartHolder.videoPlayer.onVideoPause();
                mLineChartHolder.videoPlayer.release();
                backPressedCallback.remove();
            }
        });

        //查询最高分
        List<GameAchievement> highestScores = getMaxJuniorData();
        if (highestScores.size() > 0) {
            GameAchievement gameAchievement = highestScores.get(0);
            if (gameAchievement != null) {
                mLineChartHolder.mTvScoreNum.setText(String.valueOf(gameAchievement.getBlueScore()));

            }
        }
        initLineCharBaseSetting();
        mLineChartHolder.mLineChart.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d("ZZQ", "mLineChart: mBarChart");
                mLineChartHolder.mLineChart.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int[] location = new int[2];
                mLineChartHolder.mLineChart.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];
                int measuredHeight = mLineChartHolder.mLineChart.getMeasuredHeight();
                mLineCharTextRenderer.setBottomLocation(measuredHeight + y);
                refreshLineData();
            }
        });

    }

    private void refreshLineData() {
        LineData data = getData();
        if (data != null) {
            mLineChartHolder.mLineChart.setData(data);
        }
    }

    private void initLineCharBaseSetting() {
        mLineCharTextRenderer = new LineCharTextRenderer(mLineChartHolder.mLineChart,
                mLineChartHolder.mLineChart.getAnimator(),
                mLineChartHolder.mLineChart.getViewPortHandler());
        mLineChartHolder.mLineChart.setRenderer(mLineCharTextRenderer);
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
    }


    public void initIcon() {
        User user = UserDataManager.getInstance().getUser();
        if (!TextUtils.isEmpty(user.iconPath)) {
            ImageUtils.loadLocalPic((Activity) mContext, mLineChartHolder.mIvUserIcon, user.iconPath);
        }
    }


    private LineData getData() {
//        创建一个Entry类型的集合，并添加数据
        List<Entry> entries = new ArrayList<>();

        List<GameAchievement> highestScores = GreenDaoManager.getHighestScores();
        if (highestScores.size() < 1) {
            return null;
        }
        int maxCount = Math.min(highestScores.size(), 10);
        for (int i = 0; i < maxCount; i++) {
//            添加Entry对象，传入纵轴的索引和纵轴的值
            GameAchievement gameAchievement = highestScores.get(i);
            int blueScore = gameAchievement.getBlueScore();
            int redScore = gameAchievement.getRedScore();
            int score = Math.max(blueScore, redScore);
            entries.add(new Entry(i + 1, score));
        }

//        ArrayList<Integer> datas = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            int score = (int) (1 + Math.random() * (200 - 30 + 1));
//            datas.add(Integer.valueOf(score));
//            entries.add(new Entry(i + 1, score));
//        }

//        实例化LineDataSet类，并将Entry集合中的数据和这组数据名(或者说这个图形名)，通过这个类可以对线段进行设置
        LineDataSet lineDataSet = new LineDataSet(entries, "线型图");
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);
        lineDataSet.setColor(Color.parseColor("#848283"));
        lineDataSet.setLineWidth(Utils.dp2px(mContext, 1));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setCircleColor(Color.parseColor("#848283"));
        lineDataSet.setCircleHoleColor(Color.parseColor("#ffffff"));
//        这个就是线型图所需的数据了
        return new LineData(lineDataSet);
    }


    private List<GameAchievement> getMaxJuniorData() {
        List<GameAchievement> highestScores = GreenDaoManager.getHighestScores();
        if (highestScores.size() < 1) {
            return null;
        }
        List<GameAchievement> JuniorScores = new ArrayList<>();
        for (GameAchievement gameData : highestScores) {
            if ((gameData.getType() == BaseModel.ModelType.JUNIOR.ordinal() ||
                    gameData.getType() == BaseModel.ModelType.JUNIOR_PREVIEW.ordinal())
                    && gameData.getBlueScore() > 0) {
                JuniorScores.add(gameData);
            }
        }
        return JuniorScores;
    }


    private void play() {
        mLineChartHolder.videoPlayer.setVisibility(View.VISIBLE);
        List<GameAchievement> test = GreenDaoManager.queryAll();
        if (test == null || test.size() < 1) {
            mLineChartHolder.videoPlayer.setVisibility(View.GONE);
            return;
        }
        for (GameAchievement achievement :
                test) {
            Log.i("zyw", "achievement source1 = " + achievement.toString());
        }
        String source1 = test.get(test.size() - 1).getVideoPath();
        Log.i("zyw", "play source1 = " + source1);
        mLineChartHolder.videoPlayer.setUp(source1, true, "");

        //增加title
//        mLineChartHolder.videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        //设置返回键
        mLineChartHolder.videoPlayer.getBackButton().setVisibility(View.VISIBLE);
        mLineChartHolder.videoPlayer.getFullscreenButton().setVisibility(View.GONE);
        mLineChartHolder.videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLineChartHolder.videoPlayer.onVideoPause();
                mLineChartHolder.videoPlayer.release();
                mLineChartHolder.videoPlayer.setVisibility(View.GONE);
                backPressedCallback.remove();
            }
        });
        //是否可以滑动调整
        mLineChartHolder.videoPlayer.setIsTouchWiget(true);
        //不需要屏幕旋转
        mLineChartHolder.videoPlayer.setNeedOrientationUtils(false);

        mLineChartHolder.videoPlayer.setAutoFullWithSize(true);

        mLineChartHolder.videoPlayer.startPlayLogic();

        fragment.getActivity().getOnBackPressedDispatcher().addCallback(backPressedCallback);
    }

    private OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (mLineChartHolder.videoPlayer.getVisibility() == View.VISIBLE) {
                mLineChartHolder.videoPlayer.onVideoPause();
                mLineChartHolder.videoPlayer.release();
                mLineChartHolder.videoPlayer.setVisibility(View.GONE);
                backPressedCallback.remove();
            }
        }
    };
}

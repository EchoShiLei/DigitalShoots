package com.digital.shoots.stats;

import android.view.View;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PagerStatsSecond extends BaseStatsPager {

    private HolderStatsSecondFragment mStatsSecondFragmentsHolder;

    public PagerStatsSecond(HolderStatsFragment holder) {
        super(holder);
    }

    @Override
    public void initView() {
        if (!(mHolder instanceof HolderStatsSecondFragment)) {
            return;
        }
        mStatsSecondFragmentsHolder = (HolderStatsSecondFragment) mHolder;
        mStatsSecondFragmentsHolder.mLlDataTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mStatsSecondFragmentsHolder.mTvScoreNum.setText("112");
        mStatsSecondFragmentsHolder.mLineChart.setData(setData());
    }

    private LineData setData() {
//        创建一个Entry类型的集合，并添加数据
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
//            添加Entry对象，传入纵轴的索引和纵轴的值
            entries.add(new Entry(i, new Random().nextInt(100)));
        }
//        实例化LineDataSet类，并将Entry集合中的数据和这组数据名(或者说这个图形名)，通过这个类可以对线段进行设置
        LineDataSet lineDataSet = new LineDataSet(entries, "线型图测试");
//        这个就是线型图所需的数据了
        LineData lineData = new LineData(lineDataSet);
        return lineData;
    }
}

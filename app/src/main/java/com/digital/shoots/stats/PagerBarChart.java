package com.digital.shoots.stats;

import android.content.Context;
import android.view.View;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PagerBarChart extends BaseStatsPager {

    private HolderStatsBarChartFragment mBarChartFragmentsHolder;

    public PagerBarChart(Context context, HolderStatsFragment holder) {
        super(context, holder);
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

            }
        });

        mBarChartFragmentsHolder.mTvSeepNum.setText("68");
        mBarChartFragmentsHolder.mBarChart.getLegend().setEnabled(false);
        mBarChartFragmentsHolder.mBarChart.getDescription().setEnabled(false);
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

        mBarChartFragmentsHolder.mBarChart.setData(setData());
    }

    private BarData setData() {
//        创建一个Entry类型的集合，并添加数据
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
//            添加Entry对象，传入纵轴的索引和纵轴的值
            entries.add(new BarEntry(i, new Random().nextInt(100)));
        }
//        实例化barDataSet类，并将Entry集合中的数据和这组数据名(或者说这个图形名)，通过这个类可以对线段进行设置
        BarDataSet barDataSet = new BarDataSet(entries, "线型图测试");
//        这个就是线型图所需的数据了
        BarData barData = new BarData(barDataSet);
        return barData;
    }
}

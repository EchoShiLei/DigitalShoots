package com.digital.shoots.stats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.digital.shoots.R;
import com.digital.shoots.tab.ChangePagerListener;

import java.util.ArrayList;

public class StatsFragmentsAdapter extends RecyclerView.Adapter<HolderStatsFragment> {
    private static final int PAGER_LINE_CHART = 0;
    private static final int PAGER_BAR_CHART = 1;
    private ArrayList<Integer> arrayList = new ArrayList<>();
    private Context mContext;
    private ViewPager2 mViewPager2;
    private ChangePagerListener mChangePagerListener;
    private Fragment fragment;


    public StatsFragmentsAdapter(Context context, ViewPager2 viewPager2, ChangePagerListener changePagerListener, Fragment fragment) {
        mContext = context;
        mViewPager2 = viewPager2;
        mChangePagerListener = changePagerListener;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public HolderStatsFragment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = 0;
        HolderStatsFragment holder;
        View rootView;
        if (viewType == PAGER_BAR_CHART) {
            //条形
            layoutId = R.layout.stats_bar_chart_pager;
            rootView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            holder = new HolderStatsBarChartFragment(rootView);
        } else {
            //折线
            layoutId = R.layout.stats_line_chart_pager;
            rootView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            holder = new HolderStatsLineChartFragment(rootView);
        }

        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        return arrayList.get(position);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderStatsFragment holder, int position) {
        BaseStatsPager pager = null;
        if (holder instanceof HolderStatsLineChartFragment) {
            pager = new PagerLineChart(mContext, holder, mChangePagerListener, fragment);
        }
        if (holder instanceof HolderStatsBarChartFragment) {
            pager = new PagerBarChart(mContext, holder, mChangePagerListener);
        }
        if (pager != null) {
            pager.initView();
        }
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void initPagerData() {
        arrayList.add(PAGER_LINE_CHART);
        arrayList.add(PAGER_BAR_CHART);
        notifyDataSetChanged();
    }
}

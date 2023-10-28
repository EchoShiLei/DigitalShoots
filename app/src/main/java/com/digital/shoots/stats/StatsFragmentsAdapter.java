package com.digital.shoots.stats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digital.shoots.R;

import java.util.ArrayList;

public class StatsFragmentsAdapter extends RecyclerView.Adapter<HolderStatsFragment> {
    private static final int PAGER_FIRST = 0;
    private static final int PAGER_SECOND = 1;
    private static final int PAGER_THIRD = 2;
    private ArrayList<Integer> arrayList = new ArrayList<>();
    private Context mContext;

    public StatsFragmentsAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public HolderStatsFragment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = 0;
        HolderStatsFragment holder;
        View rootView;
        switch (viewType) {
            case PAGER_SECOND:
                layoutId = R.layout.stats_second_pager;
                rootView = LayoutInflater.from(parent.getContext())
                        .inflate(layoutId, parent, false);
                holder = new HolderStatsSecondFragment(rootView);
                break;
            case PAGER_THIRD:
                layoutId = R.layout.stats_third_pager;
                rootView = LayoutInflater.from(parent.getContext())
                        .inflate(layoutId, parent, false);
                holder = new HolderStatsThirdFragment(rootView);
                break;
            default:
                layoutId = R.layout.stats_first_pager;
                rootView = LayoutInflater.from(parent.getContext())
                        .inflate(layoutId, parent, false);
                holder = new HolderStatsFirstFragment(rootView);
                break;
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
        if (holder instanceof HolderStatsFirstFragment) {
            pager = new PagerStatsFirst(holder, mContext);
        }
        if (holder instanceof HolderStatsSecondFragment) {
            pager = new PagerStatsSecond(holder);
        }
        if (holder instanceof HolderStatsThirdFragment) {
            pager = new PagerStatsThird(holder);
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
        arrayList.add(PAGER_FIRST);
        arrayList.add(PAGER_SECOND);
        arrayList.add(PAGER_THIRD);
        notifyDataSetChanged();
    }
}

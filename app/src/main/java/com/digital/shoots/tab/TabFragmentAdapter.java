package com.digital.shoots.tab;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.digital.shoots.main.MainActivity;

import java.util.ArrayList;

public class TabFragmentAdapter extends FragmentStateAdapter {

    public static final String TAG = "TabFragmentAdapter";

    public static final int FRAGMENT_TYPE_TRAINERS = 0;
    public static final int FRAGMENT_TYPE_DRILLS = 1;
    public static final int FRAGMENT_TYPE_TRACKING = 2;
    public static final int FRAGMENT_TYPE_MY_STATS = 3;
    public static final int FRAGMENT_TYPE_MY_ACCOUNT = 4;

    TrainersFragment trainersFragment;
    Fragment drillsFragment;
    Fragment trackingFragment;
    Fragment myStatsFragment;
    Fragment myAccountFragment;

    private ArrayList<Fragment> fragments = new ArrayList<>();


    public TabFragmentAdapter(@NonNull FragmentActivity fragmentActivity,
                              ChangePagerListener changePagerListener) {
        super(fragmentActivity);
        trainersFragment = TrainersFragment.newInstance();

        drillsFragment = DrillsFragment.newInstance();

        trackingFragment = TrackingFragment.newInstance();
        myStatsFragment = MyStatsFragment.newInstance();
        ((MyStatsFragment) myStatsFragment).setChangePagerListener(changePagerListener);
        myAccountFragment = MyAccountFragment.newInstance();

        fragments.add(trainersFragment);
        fragments.add(drillsFragment);
        fragments.add(trackingFragment);
        fragments.add(myStatsFragment);
        fragments.add(myAccountFragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case FRAGMENT_TYPE_TRAINERS:
                return trainersFragment;
            case FRAGMENT_TYPE_DRILLS:
                return drillsFragment;
            case FRAGMENT_TYPE_TRACKING:
                return trackingFragment;
            case FRAGMENT_TYPE_MY_STATS:
                return myStatsFragment;
            case FRAGMENT_TYPE_MY_ACCOUNT:
            default:
                return myAccountFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }


    public TrainersFragment getTrainersFragment() {
        return trainersFragment;
    }
}

package com.digital.shoots.tab;

import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.digital.shoots.R;
import com.digital.shoots.db.greendao.UserDataManager;
import com.digital.shoots.db.greendao.bean.User;
import com.digital.shoots.events.IUserInfoRefreshEvent;
import com.digital.shoots.events.UserInfoRefreshManger;
import com.digital.shoots.stats.StatsFragmentsAdapter;
import com.digital.shoots.utils.ImageUtils;
import com.digital.shoots.utils.Utils;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyStatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyStatsFragment extends Fragment {
    private static final String[] monthStr = {"Jan.", "Feb.", "Mar.", "Apr.", "May.",
            "Jun.", "Jul.", "Aug.", "Sep.", "Oct.", "Nov.", "Dec."};

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ChangePagerListener mChangePagerListener;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public ImageView mIvSpeedEmoji;
    public ImageView mIvScoreEmoji;
    public ImageView mUserIcon;
    public TextView mTracking;
    public TextView mTvTime;
    public ImageView mIvScoreStatus;
    public TextView mTvScoreNum;
    public TextView mTvScoreStatus;
    public ImageView mIvSpeedStatus;
    public TextView mTvSpeedNum;
    public TextView mTvSpeedStatus;
    public FrameLayout mFlStatsIndicator;
    public ImageView mIvProgressIndicator;
    public ImageView mIvStatsProgress;
    IUserInfoRefreshEvent mIUserInfoRefreshEvent = new IUserInfoRefreshEvent() {
        @Override
        public void onUserInfoRefresh() {
            fullUser();
        }

        @Override
        public void playDataRefresh() {

        }
    };


    public MyStatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyStatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyStatsFragment newInstance(String param1, String param2) {
        MyStatsFragment fragment = new MyStatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static MyStatsFragment newInstance() {
        MyStatsFragment fragment = new MyStatsFragment();
        return fragment;
    }

    public void setChangePagerListener(ChangePagerListener changePagerListener) {
        mChangePagerListener = changePagerListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UserInfoRefreshManger.getInstance().addInfoRefreshEvents(mIUserInfoRefreshEvent);
        mUserIcon = view.findViewById(R.id.iv_user_icon);
        ImageUtils.createCircleImage(getActivity(), mUserIcon);
        mTracking = view.findViewById(R.id.tv_tracking);
        mTvTime = view.findViewById(R.id.tv_time);

        mIvScoreStatus = view.findViewById(R.id.iv_stats_score_status);
        mTvScoreNum = view.findViewById(R.id.tv_stats_score_num);
        mTvScoreStatus = view.findViewById(R.id.tv_stats_score_status);
        mIvScoreEmoji = view.findViewById(R.id.iv_score_emoji);
        mIvSpeedStatus = view.findViewById(R.id.iv_stats_speed_status);
        mTvSpeedNum = view.findViewById(R.id.tv_stats_speed_num);
        mTvSpeedStatus = view.findViewById(R.id.tv_stats_speed_status);
        mIvSpeedEmoji = view.findViewById(R.id.iv_speed_emoji);

        mFlStatsIndicator = view.findViewById(R.id.fl_stats_indicator);
        mIvProgressIndicator = view.findViewById(R.id.iv_progress_indicator);
        mIvStatsProgress = view.findViewById(R.id.iv_stats_progress);
        initView();
        fullUser();
    }


    private void fullUser() {
        User user = UserDataManager.getInstance().getUser();
        if (!TextUtils.isEmpty(user.iconPath)) {
            ImageUtils.loadLocalPic(getActivity(), mUserIcon, user.iconPath);
        }
    }

    private void initView() {

        mUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2024/1/28 切到MyAccountFragment
                if (mChangePagerListener != null) {
                    mChangePagerListener.onChangerPager(R.id.myAccountFragment);
                }
            }
        });
        mTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2024/1/28 切到tracking
                if (mChangePagerListener != null) {
                    mChangePagerListener.onChangerPager(R.id.trackingFragment);
                }
            }
        });

        Calendar instance = Calendar.getInstance();
        int year = instance.get(Calendar.YEAR);
        int month = instance.get(Calendar.MONTH);
        try {
            mTvTime.setText(String.format("%s%d", monthStr[month], year));
        } catch (Exception e) {
        }
        mIvScoreStatus.post(new Runnable() {
            @Override
            public void run() {
                initScoreView(150);
                moveByScoreProgress(260);
                initSpeedView(200);
            }
        });
        mIvStatsProgress.post(new Runnable() {
            @Override
            public void run() {
            }
        });
    }


    private void initScoreView(int score) {
        mTvScoreNum.setText(String.valueOf(score));
        mIvScoreStatus.setImageDrawable(Utils.getDrawable(getContext(), getScoreIvId(score)));
    }

    private void initSpeedView(int speed) {
        mTvSpeedNum.setText(String.valueOf(speed));
        mIvSpeedStatus.setImageDrawable(Utils.getDrawable(getContext(), getSpeedIvId(speed)));
        mIvSpeedEmoji.setImageDrawable(Utils.getDrawable(getContext(),
                speed >= 50 ? R.drawable.emoji_good : R.drawable.emoji_pro));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UserInfoRefreshManger.getInstance().destroyInfoRefreshEvents(mIUserInfoRefreshEvent);
    }

    private @DrawableRes int getScoreIvId(int score) {
        int ivId = 0;
        if (score < 75) {
            ivId = R.drawable.stats_score_1;
        } else if (score <= 150) {
            ivId = R.drawable.stats_score_2;
        } else if (score <= 225) {
            ivId = R.drawable.stats_score_3;
        } else {
            ivId = R.drawable.stats_score_4;
        }

        return ivId;
    }

    private @DrawableRes int getSpeedIvId(int speed) {
        int ivId = 0;
        if (speed < 50) {
            ivId = R.drawable.stats_speed_1;
        } else if (speed <= 80) {
            ivId = R.drawable.stats_speed_2;
        } else {
            ivId = R.drawable.stats_speed_3;
        }

        return ivId;
    }

    /**
     * 从0开始最大3
     *
     * @param score
     */
    private void moveByScoreProgress(int score) {
        int ivId = 0;
        int step = 0;
        if (score < 75) {
            ivId = R.drawable.stats_indicator_1;
            step = 0;
        } else if (score <= 150) {
            ivId = R.drawable.stats_indicator_2;
            step = 1;
        } else if (score <= 225) {
            ivId = R.drawable.stats_indicator_3;
            step = 2;
        } else {
            ivId = R.drawable.stats_indicator_4;
            step = 3;
        }
        Log.d("ZZQ", "score:" + score + " step:" + step);
        mIvProgressIndicator.setImageDrawable(Utils.getDrawable(getContext(), ivId));

        ViewGroup.LayoutParams layoutParams = mFlStatsIndicator.getLayoutParams();
        if (layoutParams instanceof RelativeLayout.LayoutParams) {
            int width = mIvStatsProgress.getWidth();
            int preStepWith = width / 4;
            RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) layoutParams;
            layoutParams1.setMargins(preStepWith * step, 0, 0, 0);
            mFlStatsIndicator.setLayoutParams(layoutParams1);
        }
    }
}
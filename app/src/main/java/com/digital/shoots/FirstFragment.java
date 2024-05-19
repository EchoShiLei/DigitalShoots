package com.digital.shoots;


import android.graphics.Outline;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;

import com.digital.shoots.base.BaseFragment;
import com.digital.shoots.db.greendao.UserDataManager;
import com.digital.shoots.db.greendao.bean.User;
import com.digital.shoots.events.IUserInfoRefreshEvent;
import com.digital.shoots.events.UserInfoRefreshManger;
import com.digital.shoots.utils.ImageUtils;
import com.digital.shoots.utils.ToastUtils;
import com.digital.shoots.views.MySurfaceView;

import java.io.IOException;

public class FirstFragment extends BaseFragment {
    public static final String TAG = "FirstFragment";

    private MySurfaceView player;
    private ImageView mUserIcon;
    IUserInfoRefreshEvent mIUserInfoRefreshEvent = new IUserInfoRefreshEvent() {
        @Override
        public void onUserInfoRefresh() {
            fullUser();
        }
    };

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    int i = 1;

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("zyw", "onViewCreated");
        player = view.findViewById(R.id.surfaceView);
        mUserIcon = view.findViewById(R.id.iv_user_icon);
        ImageUtils.createCircleImage(getActivity(), mUserIcon);
        player.setClipToOutline(true);
        player.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 80);
            }
        });

        try {
            player.setDataPath(getContext().getAssets().openFd("splash.mp4"));
        } catch (IOException e) {
            Log.w(TAG, "catch IOException when play splash video");
        }

//        player.getTitleTextView().setVisibility(View.GONE);
//        player.getBackButton().setVisibility(View.GONE);
//        player.setBottomProgressBarDrawable(null);
//        player.getStartButton().setVisibility(View.GONE);
//        player.getFullscreenButton().setVisibility(View.GONE);
//        player.setIsTouchWigetFull(false);
//        player.setThumbPlay(false);
//        player.setIsTouchWiget(false);
//        player.setLooping(true);
//        String url = "android.resource://" + getActivity().getPackageName() + "/" + R.raw.splash;
//        player.setUp(url, true, "");
//        player.startPlayLogic();
//        player.setDismissControlTime(0);
//        player.setClickable(false);
//        player.setOnClickListener(null);


        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mainViewModel.deviceControl == null || !mainViewModel.isConnected()) {
                    ToastUtils.showToast(R.string.pls_connect);
                    return;
                }
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_mainModelFragment);

            }
        });
        UserInfoRefreshManger.getInstance().addInfoRefreshEvents(mIUserInfoRefreshEvent);
        fullUser();
    }

    private void fullUser() {
        User user = UserDataManager.getInstance().getUser();
        if (!TextUtils.isEmpty(user.iconPath)) {
            ImageUtils.loadLocalPic(getActivity(), mUserIcon, user.iconPath);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        UserInfoRefreshManger.getInstance().destroyInfoRefreshEvents(mIUserInfoRefreshEvent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player != null) {
            try {
                player.setDataPath(getContext().getAssets().openFd("splash.mp4"));
            } catch (IOException e) {
                Log.w(TAG, "catch IOException when play splash video");
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            player.release();
        }
    }
}
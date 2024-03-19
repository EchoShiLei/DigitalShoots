package com.digital.shoots;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;

import com.digital.shoots.base.BaseFragment;
import com.digital.shoots.ble.BleDataUtils;
import com.digital.shoots.ble.BleDeviceManager;
import com.digital.shoots.utils.ToastUtils;
import com.digital.shoots.views.MySurfaceView;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.io.IOException;

public class FirstFragment extends BaseFragment {
    public static final String TAG = "FirstFragment";

    private MySurfaceView player;

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
        player = view.findViewById(R.id.surfaceView);
        player.getHolder().setFixedSize(100, 100);
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
                if (mainViewModel.deviceControl == null) {
                    ToastUtils.showToast(R.string.pls_connect);
                    return;
                }
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_mainModelFragment);

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }
}
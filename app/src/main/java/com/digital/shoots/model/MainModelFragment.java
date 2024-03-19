package com.digital.shoots.model;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.fragment.NavHostFragment;

import com.digital.shoots.R;
import com.digital.shoots.base.BaseFragment;
import com.digital.shoots.camera.CameraUtil;
import com.digital.shoots.db.greendao.GreenDaoManager;
import com.digital.shoots.db.greendao.bean.GameAchievement;
import com.digital.shoots.utils.BaseConstant;
import com.digital.shoots.utils.ToastUtils;

import java.util.Calendar;
import java.util.Random;


public class MainModelFragment extends BaseFragment {

    ConstraintLayout model1;
    ConstraintLayout model2;
    ConstraintLayout model3;

    private boolean JuniorChecked = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_model, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        model1 = view.findViewById(R.id.model1_layout);
        model2 = view.findViewById(R.id.model2_layout);
        model3 = view.findViewById(R.id.model3_layout);
        mainViewModel.online();

        model1.setOnClickListener(view1 -> {
            if (mainViewModel.deviceControl == null) {
                ToastUtils.showToast(R.string.pls_connect);
                return;
            }
            NavHostFragment.findNavController(MainModelFragment.this)
                    .navigate(R.id.action_mainModelFragment_to_model1Fragment);

        });

        model3.setOnClickListener(view1 -> {
            if (mainViewModel.deviceControl == null) {
                ToastUtils.showToast(R.string.pls_connect);
                return;
            }
            NavHostFragment.findNavController(MainModelFragment.this)
                    .navigate(R.id.action_mainModelFragment_to_battleFragment);

        });

        view.findViewById(R.id.model2_textView6).setOnClickListener(view12 -> {
            ToastUtils.showToast("start");
//            CameraUtil.getInstance().startPreview(MainModelFragment.this, mPreview);
            if(JuniorChecked) {
                NavHostFragment.findNavController(MainModelFragment.this)
                        .navigate(R.id.action_mainModelFragment_to_previewFragment);
            } else {
                NavHostFragment.findNavController(MainModelFragment.this)
                        .navigate(R.id.action_mainModelFragment_to_juniorFragment);
            }
        });

        ((CheckBox) view.findViewById(R.id.cb_model2_novice)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                JuniorChecked = b;
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mainViewModel.offline();
        super.onDestroy();
    }
}
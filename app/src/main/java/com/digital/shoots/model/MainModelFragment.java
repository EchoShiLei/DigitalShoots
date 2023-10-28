package com.digital.shoots.model;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digital.shoots.R;
import com.digital.shoots.base.BaseFragment;
import com.digital.shoots.utils.ToastUtils;

public class MainModelFragment extends BaseFragment {

    ConstraintLayout model1;
    ConstraintLayout model2;
    ConstraintLayout model3;

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

        model1.setOnClickListener(view1 -> {
            if (mainViewModel.deviceControl == null) {
                ToastUtils.showToast(R.string.pls_connect);
                return;
            }
            NavHostFragment.findNavController(MainModelFragment.this)
                    .navigate(R.id.action_mainModelFragment_to_model1Fragment);

        });
    }
}
package com.digital.shoots.model;

import android.os.Bundle;
import android.view.View;

import com.digital.shoots.base.BaseFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Model1Fragment extends BaseFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainViewModel.startModel1();
    }
}
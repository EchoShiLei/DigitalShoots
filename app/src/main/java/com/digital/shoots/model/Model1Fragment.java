package com.digital.shoots.model;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digital.shoots.R;
import com.digital.shoots.base.BaseFragment;
import com.digital.shoots.views.LedTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Model1Fragment extends BaseFragment {
    LedTextView time;
    LedTextView score;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_model1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        time=view.findViewById(R.id.game_time);
        score=view.findViewById(R.id.score);
        score.setLedFont();
        time.setLedFont();
        mainViewModel.startModel1();
    }
}
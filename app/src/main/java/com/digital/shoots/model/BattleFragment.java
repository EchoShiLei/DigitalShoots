package com.digital.shoots.model;

import static com.digital.shoots.model.BaseModel.ModelType.BATTLE;
import static com.digital.shoots.model.BaseModel.ModelType.JUNIOR;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.digital.shoots.R;
import com.digital.shoots.base.BaseFragment;
import com.digital.shoots.views.LedTextView;

public class BattleFragment extends BaseFragment {
    LedTextView time;
    LedTextView blueScore, redScore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_battle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        time = view.findViewById(R.id.game_time);
        blueScore = view.findViewById(R.id.blue_score);
        redScore = view.findViewById(R.id.red_score);
        mainViewModel.getLivTime().observe(getActivity(), liveTime -> {
            double dbTime = liveTime;
            double ss = dbTime / 1000;
            String stTime = df2.format(ss);
//            Log.d("time",stTime);
            time.setText(stTime);
        });

        mainViewModel.getLiveCutDown().observe(getActivity(), cutDownTime -> {
            blueScore.setText(cutDownTime);
        });
        mainViewModel.getLiveBlueScore().observe(getActivity(), liveScore -> {
            blueScore.setText(liveScore.toString());
        });
        mainViewModel.getLiveRedScore().observe(getActivity(), liveScore -> {
            redScore.setText(liveScore.toString());
        });
        mainViewModel.startModel(BATTLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        mainViewModel.endModel();
    }
}
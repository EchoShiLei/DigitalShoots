package com.digital.shoots.model;

import static com.digital.shoots.model.BaseModel.ModelType.JUNIOR;
import static com.digital.shoots.model.BaseModel.ModelType.NOVICE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.digital.shoots.R;
import com.digital.shoots.base.BaseFragment;
import com.digital.shoots.views.LedTextView;

public class JuniorFragment extends BaseFragment {
    LedTextView time;
    LedTextView score;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_junior, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        time = view.findViewById(R.id.game_time);
        score = view.findViewById(R.id.score);
        mainViewModel.getLivTime().observe(getActivity(), liveTime -> {
            double dbTime = liveTime;
            double ss = dbTime / 1000;
            String stTime = df2.format(ss);
//            Log.d("time",stTime);
            time.setText(stTime);
        });
        mainViewModel.getLiveScore().observe(getActivity(), liveScore -> {
            score.setText(liveScore.toString());
        });
        mainViewModel.startModel(JUNIOR);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mainViewModel.endModel();
    }
}
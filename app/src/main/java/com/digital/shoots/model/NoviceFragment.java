package com.digital.shoots.model;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digital.shoots.R;
import com.digital.shoots.base.BaseFragment;
import com.digital.shoots.views.LedTextView;
import com.digital.shoots.views.LedTextView2;
import com.erlei.videorecorder.util.LogUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.digital.shoots.model.BaseModel.ModelType.NOVICE;

import java.text.DecimalFormat;

public class NoviceFragment extends BaseFragment {
    LedTextView2 time;
    LedTextView score;
    LedTextView speed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_model1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        time = view.findViewById(R.id.game_time);
        score = view.findViewById(R.id.score);
        speed = view.findViewById(R.id.speed);
        mainViewModel.getLivTime().observe(getActivity(), liveTime -> {
            double dbTime = liveTime;
            int sss = (int) (dbTime % 1000)/10;
            int ss = (int) (dbTime / 1000);
            if(ss >= 60) {
                ss = ss % 60;
            }
            int mm = (int) (dbTime / 60000);
            String sssString = String.format("%02d", sss);
            String ssString = String.format("%02d", ss);
            String mmString = String.format("%02d", mm);
//            Log.d("time",stTime);
            time.setText(mmString + "：" + ssString + "：" + sssString);
        });

        mainViewModel.getLiveCutDown().observe(getActivity(), cutDownTime -> {
            score.setText(cutDownTime);
        });
        mainViewModel.getLiveBlueScore().observe(getActivity(), liveScore -> {
            score.setText(liveScore.toString());
        });
        mainViewModel.getLiveSpeed().observe(getActivity(), liveSpeed -> {
            speed.setText(liveSpeed.toString());
        });
        mainViewModel.startModel(NOVICE);
    }

    @Override
    public void onPause() {
        super.onPause();
        mainViewModel.endModel();
    }
}
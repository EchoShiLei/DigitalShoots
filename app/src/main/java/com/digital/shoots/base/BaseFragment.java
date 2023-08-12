package com.digital.shoots.base;

import android.os.Bundle;
import android.view.View;

import com.digital.shoots.main.MainViewModel;

import java.text.DecimalFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class BaseFragment extends Fragment {

    public MainViewModel mainViewModel;
    public DecimalFormat df2;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        df2 = new DecimalFormat("000.00");
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
    }
}

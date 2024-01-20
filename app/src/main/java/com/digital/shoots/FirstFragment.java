package com.digital.shoots;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digital.shoots.base.BaseFragment;
import com.digital.shoots.ble.BleDataUtils;
import com.digital.shoots.ble.BleDeviceManager;
import com.digital.shoots.utils.ToastUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class FirstFragment extends BaseFragment {

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
}
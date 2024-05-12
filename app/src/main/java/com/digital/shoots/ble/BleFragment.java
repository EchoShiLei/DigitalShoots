package com.digital.shoots.ble;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digital.shoots.R;
import com.digital.shoots.base.BaseFragment;
import com.digital.shoots.ble.BleDeviceManager;
import com.digital.shoots.ble.BleItem;
import com.digital.shoots.main.MainViewModel;
import com.digital.shoots.utils.ToastUtils;
import com.digital.shoots.utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BleFragment extends BaseFragment {
    RecyclerView deviceList;
    DeviceAdapter adapter;
    List<BleItem> list = new ArrayList<>();
    Set<String> connectedList = new HashSet<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ble, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        deviceList = view.findViewById(R.id.device_list);
        View btnScan = view.findViewById(R.id.btn_scan_bluetooth);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanBluetooth();
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        deviceList.setLayoutManager(linearLayoutManager);

        adapter = new DeviceAdapter(getActivity(), list, mac -> mainViewModel.deviceClick(mac));
        deviceList.setAdapter(adapter);
        mainViewModel.getLiveConnectedMacs().observe(getActivity(), new Observer<Set<String>>() {
            @Override
            public void onChanged(Set<String> strings) {
                connectedList = strings;
                checkConnected();
            }
        });
        scanBluetooth();

    }

    private void checkConnected() {
        for (BleItem item : list) {
            if (!TextUtils.isEmpty(item.mac) && connectedList.contains(item.mac)) {
                item.isConnect = true;
            } else {
                item.isConnect = false;
            }
        }
        if (adapter == null) {
            return;
        }
        adapter.notifyDataSetChanged();
    }

    private void scanBluetooth() {
        list.clear();
        BleDeviceManager.getInstance().setUiScanCallback((name, address) -> {
            list.add(new BleItem(name, address));
            checkConnected();
        });
        BleDeviceManager.getInstance().scan();
    }

    private class DeviceAdapter extends RecyclerView.Adapter {
        List<BleItem> list = new ArrayList<>();
        ItemClick callback;
        Context mContext;

        public DeviceAdapter(Context context, List<BleItem> list, ItemClick callback) {
            this.list = list;
            mContext = context;
            this.callback = callback;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ble_item, parent, false);
            return new BleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            BleItem bleItem = list.get(position);
            holder.itemView.setOnClickListener(view -> callback.click(bleItem.mac));
            ((BleViewHolder) holder).name.setText(bleItem.name);
            ((BleViewHolder) holder).name.setTextColor( bleItem.isConnect ?Color.rgb(0,255,0):Color.rgb(127,127,127));
            ((BleViewHolder) holder).vStatus.setImageDrawable(Utils.getDrawable(getContext(),
                    bleItem.isConnect ? R.drawable.bluetooth_open : R.drawable.bluetooth_off));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        private class BleViewHolder extends RecyclerView.ViewHolder {
            public TextView name;
            public ImageView vStatus;

            public BleViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.ble_name);
                vStatus = itemView.findViewById(R.id.iv_bluetooth_status);
            }
        }

    }

    public interface ItemClick {
        void click(String mac);
    }
}
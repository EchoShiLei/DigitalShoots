package com.digital.shoots.ble;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digital.shoots.R;
import com.digital.shoots.base.BaseFragment;
import com.digital.shoots.ble.BleDeviceManager;
import com.digital.shoots.ble.BleItem;
import com.digital.shoots.main.MainViewModel;
import com.digital.shoots.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BleFragment extends BaseFragment {
    RecyclerView deviceList;
    DeviceAdapter adapter;
    List<BleItem> list = new ArrayList<>();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_ble, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        deviceList = view.findViewById(R.id.device_list);
        View btnScan = view.findViewById(R.id.btn_scan_bluetooth);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        deviceList.setLayoutManager(linearLayoutManager);

        adapter = new DeviceAdapter(getActivity(), list, mac -> mainViewModel.deviceClick(mac));
        deviceList.setAdapter(adapter);


        BleDeviceManager.getInstance().setUiScanCallback((name, address) -> {
            list.add(new BleItem(name, address));
            adapter.notifyDataSetChanged();
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
            holder.itemView.setOnClickListener(view -> callback.click(list.get(position).mac));
            ((BleViewHolder) holder).name.setText(list.get(position).name);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        private class BleViewHolder extends RecyclerView.ViewHolder {
            public TextView name;

            public BleViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.ble_name);
            }
        }

    }

    public interface ItemClick {
        void click(String mac);
    }
}
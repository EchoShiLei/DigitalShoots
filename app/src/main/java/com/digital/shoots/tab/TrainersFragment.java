package com.digital.shoots.tab;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.digital.shoots.R;
import com.digital.shoots.main.MainViewModel;
import com.zyq.easypermission.EasyPermission;
import com.zyq.easypermission.EasyPermissionHelper;
import com.zyq.easypermission.EasyPermissionResult;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrainersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrainersFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NavController navController;
    private String[] permissions = new String[]{
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
    };

    private final int MY_REQUEST_CODE = 1001;

    private MainViewModel mainViewModel;


    public TrainersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrainersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrainersFragment newInstance(String param1, String param2) {
        TrainersFragment fragment = new TrainersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static TrainersFragment newInstance() {
        TrainersFragment fragment = new TrainersFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trainers, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        navController = Navigation.findNavController(view.findViewById(R.id.nav_host_fragment));
        view.findViewById(R.id.btn_ble).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
                    gotoDevice();
                    return;
                }
                EasyPermission.build()
                        .mRequestCode(MY_REQUEST_CODE)//请求code，自己定义
                        .mPerms(permissions)//权限，可支持多个
                        .mResult(new EasyPermissionResult() {//回调
                            @Override
                            public void onPermissionsAccess(int requestCode) {
                                //权限已通过
                                super.onPermissionsAccess(requestCode);
                                gotoDevice();
                            }

                            @Override
                            public void onPermissionsDismiss(int requestCode, @NonNull List<String> permissions) {
                                super.onPermissionsDismiss(requestCode, permissions);
                                Log.e("onPermissionsDismiss", permissions.toString());
                                //权限被拒绝
                                StringBuilder stringBuilder = new StringBuilder();
                                for (String s : permissions) {
                                    stringBuilder.append(s);
                                }
                                Toast.makeText(getActivity(), stringBuilder, Toast.LENGTH_LONG).show();
                            }

                        }).requestPermission();
            }
        });
    }

    private void gotoDevice() {
        navController.navigate(R.id.SecondFragment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            EasyPermissionHelper.getInstance().onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissionHelper.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults, getActivity());
    }

    public boolean onBackPressed() {
        return navController.navigateUp();
    }
}
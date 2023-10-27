package com.digital.shoots.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.digital.shoots.R;
import com.digital.shoots.ble.BleDeviceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.zyq.easypermission.EasyPermission;
import com.zyq.easypermission.EasyPermissionHelper;
import com.zyq.easypermission.EasyPermissionResult;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public NavController navController;
    private String[] permissions = new String[]{
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
    };

    private final int MY_REQUEST_CODE = 1001;

    private MainViewModel mainViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        navController = Navigation.findNavController(findViewById(R.id.nav_host_fragment));
        NavigationUI.setupWithNavController(((BottomNavigationView)findViewById(R.id.bottom)), navController);
        findViewById(R.id.btn_ble).setOnClickListener(new View.OnClickListener() {
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
                                Toast.makeText(MainActivity.this, stringBuilder, Toast.LENGTH_LONG).show();
                            }

                        }).requestPermission();
            }
        });
    }


    private void gotoDevice() {
        navController.navigate(R.id.SecondFragment);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            EasyPermissionHelper.getInstance().onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissionHelper.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}
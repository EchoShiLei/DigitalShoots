package com.digital.shoots;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.digital.shoots.main.MainActivity;
import com.zyq.easypermission.EasyPermission;
import com.zyq.easypermission.EasyPermissionHelper;
import com.zyq.easypermission.EasyPermissionResult;
import com.zyq.easypermission.bean.EasyAppSettingDialogStyle;
import com.zyq.easypermission.bean.PermissionAlertInfo;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LauncherActivity extends Activity {

    private static final String TAG = "ActivityLungcher";
    private final int MY_REQUEST_CODE = 1000;
    private String[] permissions = new String[]{
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    int grantedSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lungcher);
        EasyPermissionHelper.getInstance().setDialogStyle(new EasyAppSettingDialogStyle(EasyAppSettingDialogStyle.DialogStyle.STYLE_CUSTOM)
        .setCancelText(LauncherActivity.this.getString(R.string.per_cancel) )
        .setConfirmText(LauncherActivity.this.getString(R.string.per_sure) ));
        EasyPermission.build()
                .mRequestCode(MY_REQUEST_CODE)//请求code，自己定义
                .mPerms(permissions)//权限，可支持多个
                .mAlertInfo(new PermissionAlertInfo(LauncherActivity.this.getString(R.string.per_title) ,
                        LauncherActivity.this.getString(R.string.per_message) ))
                .mResult(new EasyPermissionResult() {//回调
            @Override
            public void onPermissionsAccess(int requestCode) {
                //权限已通过
                super.onPermissionsAccess(requestCode);
                gotoMain();
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
                Toast.makeText(LauncherActivity.this, stringBuilder, Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public boolean onDismissAsk(int requestCode, @NonNull List<String> permissions) {
                //权限被拒绝并禁止再次询问
                return super.onDismissAsk(requestCode, permissions);
            }

        }).requestPermission();


    }

    private void gotoMain() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LauncherActivity.this, MainActivity.class));
                finish();
            }
        }, 1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissionHelper.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //使用EasyPermissionHelper注入回调(系统设置返回使用)
        EasyPermissionHelper.getInstance().onActivityResult(requestCode, resultCode, data);
    }
}

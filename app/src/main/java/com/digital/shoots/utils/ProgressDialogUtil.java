package com.digital.shoots.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogUtil {
    private static ProgressDialog progressDialog;//ProgressDialog这个对象你从上面的导入也可以看到，这是Android库自带的

    public static void showProgressDialog(Activity activity, String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage(message);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        });
    }

    public static void hideProgressDialog(Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }
}

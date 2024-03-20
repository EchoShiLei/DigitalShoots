package com.digital.shoots.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.digital.shoots.R;

public class UseInputDialog extends Dialog {


    private DialogClickListener dialogClickListener;
    private String mTintText;

    public UseInputDialog(@NonNull Context context) {
        super(context);
    }

    public UseInputDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public interface DialogClickListener {
        void onOkOnclick(String content);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_input_dialog_layout);
        EditText useInput = findViewById(R.id.et_user_input);
        if (!TextUtils.isEmpty(mTintText)) {
            useInput.setHint(mTintText);
        }
        View tvCancel = findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        View tvConfirm = findViewById(R.id.tv_confirm);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogClickListener != null) {
                    String input = useInput.getText().toString();
                    dialogClickListener.onOkOnclick(input);
                }
            }
        });
    }

    public void setDialogClickListener(DialogClickListener dialogClickListener) {
        this.dialogClickListener = dialogClickListener;
    }

    public void setTintText(String tintText) {
        mTintText = tintText;
    }
}

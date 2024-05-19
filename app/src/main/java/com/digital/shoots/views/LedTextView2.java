package com.digital.shoots.views;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.digital.shoots.DigitalApplication;

import java.io.File;

public class LedTextView2 extends androidx.appcompat.widget.AppCompatTextView {
    private static final String FONT_DIGITAL_6 = "fonts" + File.separator
            + "digital-6.ttf";//定义字体文件路径。


    public LedTextView2(@NonNull Context context) {
        super(context);
        setLedFont();
    }

    public LedTextView2(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLedFont();
    }

    public LedTextView2(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLedFont();
    }
    public void setLedFont(){
        AssetManager assets = DigitalApplication.getContext().getAssets();
        final Typeface font = Typeface.createFromAsset(assets, FONT_DIGITAL_6);
        setTypeface(font);// 设置字体
    }
}

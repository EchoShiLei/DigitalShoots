package com.digital.shoots.views;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.digital.shoots.DigitalApplication;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LedTextView extends androidx.appcompat.widget.AppCompatTextView {
    private static final String FONT_DIGITAL_7 = "fonts" + File.separator
            + "digital-7.ttf";//定义字体文件路径。


    public LedTextView(@NonNull Context context) {
        super(context);
        setLedFont();
    }

    public LedTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLedFont();
    }

    public LedTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLedFont();
    }
    public void setLedFont(){
        AssetManager assets = DigitalApplication.getContext().getAssets();
        final Typeface font = Typeface.createFromAsset(assets, FONT_DIGITAL_7);
        setTypeface(font);// 设置字体
    }
}

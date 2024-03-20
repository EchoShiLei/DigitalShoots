package com.digital.shoots.utils;

import android.app.Activity;
import android.graphics.Outline;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class ImageUtils {

    public static void loadLocalPic(Activity activity, ImageView imageView, String path) {
        if (imageView == null || TextUtils.isEmpty(path)) {
            return;
        }
        Glide.with(activity)
                .load(path)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }


    public static void createCircleImage(Activity activity, ImageView imageView) {
        if (imageView == null) {
            return;
        }
        imageView.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                Rect selfRect = new Rect(0, 0, view.getWidth(), view.getHeight());
                outline.setRoundRect(selfRect, Utils.dp2px(activity, 30));
            }
        });
        imageView.setClipToOutline(true);
    }
}

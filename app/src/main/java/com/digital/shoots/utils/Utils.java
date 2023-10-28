package com.digital.shoots.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;

public class Utils {

    public static Drawable getDrawable(Context context, @DrawableRes int id) {
        if (context == null) {
            return null;
        }
        return context.getDrawable(id);
    }
}

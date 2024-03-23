package com.digital.shoots.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnMediaEditInterceptListener;
import com.luck.picture.lib.utils.DateUtils;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropImageEngine;

import java.io.File;

public class MeOnMediaEditInterceptListener implements OnMediaEditInterceptListener {
    private final String outputCropPath;
    private final UCrop.Options options;

    public MeOnMediaEditInterceptListener(String outputCropPath, UCrop.Options options) {
        this.outputCropPath = outputCropPath;
        this.options = options;
    }

    @Override
    public void onStartMediaEdit(Fragment fragment, LocalMedia currentLocalMedia, int requestCode) {
        String currentEditPath = currentLocalMedia.getAvailablePath();
        Uri inputUri = PictureMimeType.isContent(currentEditPath)
                ? Uri.parse(currentEditPath) : Uri.fromFile(new File(currentEditPath));
        Uri destinationUri = Uri.fromFile(
                new File(outputCropPath, DateUtils.getCreateFileName("CROP_") + ".jpeg"));
        UCrop uCrop = UCrop.of(inputUri, destinationUri);
        options.setHideBottomControls(false);
        uCrop.withOptions(options);
        uCrop.setImageEngine(new UCropImageEngine() {
            @Override
            public void loadImage(Context context, String url, ImageView imageView) {

                Glide.with(context).load(url).override(180, 180).into(imageView);
            }

            @Override
            public void loadImage(Context context, Uri url, int maxWidth, int maxHeight, OnCallbackListener<Bitmap> call) {
                Glide.with(context).asBitmap().load(url).override(maxWidth, maxHeight).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (call != null) {
                            call.onCall(resource);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        if (call != null) {
                            call.onCall(null);
                        }
                    }
                });
            }
        });
        uCrop.startEdit(fragment.requireActivity(), fragment, requestCode);
    }
}

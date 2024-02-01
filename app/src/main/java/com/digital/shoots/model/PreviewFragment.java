package com.digital.shoots.model;

import static com.digital.shoots.model.BaseModel.ModelType.NOVICE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.digital.shoots.BuildConfig;
import com.digital.shoots.R;
import com.digital.shoots.base.BaseFragment;
import com.digital.shoots.camera.CameraUtil;
import com.digital.shoots.db.greendao.GreenDaoManager;
import com.digital.shoots.db.greendao.bean.GameAchievement;
import com.digital.shoots.utils.BaseConstant;
import com.digital.shoots.utils.Utils;
import com.digital.shoots.views.LedTextView;
import com.erlei.multipartrecorder.MultiPartRecorder;
import com.erlei.videorecorder.camera.Camera;
import com.erlei.videorecorder.camera.Size;
import com.erlei.videorecorder.effects.CanvasOverlayEffect;
import com.erlei.videorecorder.effects.EffectsManager;
import com.erlei.videorecorder.recorder.CameraController;
import com.erlei.videorecorder.recorder.DefaultCameraPreview;
import com.erlei.videorecorder.recorder.ICameraPreview;
import com.erlei.videorecorder.recorder.VideoRecorder;
import com.erlei.videorecorder.recorder.VideoRecorderHandler;
import com.erlei.videorecorder.util.FPSCounterFactory;
import com.erlei.videorecorder.util.LogUtil;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import javax.security.auth.callback.CallbackHandler;

public class PreviewFragment extends BaseFragment {

    private TextureView mTextureView;

    // test
    private Button start;
    private Button score;
    private Button speed;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    private void initView(View view) {
        mTextureView = view.findViewById(R.id.textureView);
        start = view.findViewById(R.id.start);
        score = view.findViewById(R.id.score);
        speed = view.findViewById(R.id.speed);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initData() {
        //DEMO
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraUtil.getInstance().startRecord(10, new CameraUtil.OnRecorderStateListener() {
                    @Override
                    public void onRecorderStart() {
                        Log.i("zyw", "onRecorderStart");
                    }

                    @Override
                    public void onRecorderSuccess(String outputFile) {
                        Log.i("zyw", "onRecorderSuccess outputFile = " + outputFile);
                        Toast.makeText(getContext(), "recordSuccess", Toast.LENGTH_SHORT).show();
                        GreenDaoManager.insert(new GameAchievement(System.currentTimeMillis(),
                                0, new Random().nextInt(100), new Random().nextInt(100),
                                String.valueOf(Calendar.getInstance().get(Calendar.YEAR)) + (Calendar.getInstance().get(Calendar.MONTH) + 1) + Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                                outputFile));
                    }

                    @Override
                    public void onRecorderFailure() {
                        Log.i("zyw", "onRecorderFailure");
                    }
                });
            }
        });

        speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraUtil.getInstance().setSpeed(CameraUtil.getInstance().getSpeed() + 1);
            }
        });
        score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraUtil.getInstance().setScore(CameraUtil.getInstance().getScore() + 1);
            }
        });

        CameraUtil.getInstance().setTextureView(getContext(), mTextureView);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mTextureView.getSurfaceTexture() != null) {
            CameraUtil.getInstance().startPreview();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CameraUtil.getInstance().cancelRecord();
        CameraUtil.getInstance().stopPreview();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        CameraUtil.getInstance().destroy();
        mainViewModel.endModel();
    }
}
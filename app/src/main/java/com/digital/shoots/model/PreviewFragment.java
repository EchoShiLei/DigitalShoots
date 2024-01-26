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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.digital.shoots.BuildConfig;
import com.digital.shoots.R;
import com.digital.shoots.base.BaseFragment;
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
import java.util.Locale;

import javax.security.auth.callback.CallbackHandler;

public class PreviewFragment extends BaseFragment {

    private TextureView mTextureView;

    private MultiPartRecorder mRecorder;
    private CameraController mCameraController;
    private EffectsManager mEffectsManager;


    @SuppressLint("HandlerLeak")
    private class CallbackHandler extends VideoRecorderHandler {

        private Toast mToast;

        @Override
        protected void handleUpdateFPS(float fps) {
        }

        @SuppressLint("ShowToast")
        @Override
        protected void handleVideoMuxerStopped(String output) {
            if (mToast != null) {
                mToast.setText(output);
            } else {
                mToast = Toast.makeText(getContext(), output, Toast.LENGTH_SHORT);
            }
            mToast.show();
        }
    }

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
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initData() {
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                startPreview();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                mRecorder.onSizeChanged(width, height);
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                //退到后台可能不会调用onSurfaceTextureDestroyed这个方法
                // , 如果之后又打开了其他相机app , 会导致重新进入时没有开启预览
                stopPreview();
                surface.release();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });

        mTextureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mCameraController.setFocusAreaOnTouch(event);
                mCameraController.setMeteringAreaOnTouch(event);
                mCameraController.setZoomOnTouch(event);
                return true;
            }
        });
    }


    private void initRecorder() {
        ICameraPreview cameraPreview = new DefaultCameraPreview(mTextureView);
//        ICameraPreview cameraPreview = new OffscreenCameraPreview(getContext(), 1920, 1920); //离屏录制

        Camera.CameraBuilder cameraBuilder = new Camera.CameraBuilder(getActivity())
                .useDefaultConfig()
                .setFacing(android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK)
                .setPreviewSize(new Size(2048, 1536))
                .setRecordingHint(true)
                .setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

        //视频效果管理器
        mEffectsManager = new EffectsManager();
        mEffectsManager.addEffect(new CanvasOverlayEffect() {
            private FPSCounterFactory.FPSCounter1 mCounter;
            Paint mPaint;

            int mScreenWidth;
            int mScreenHeight;

            Bitmap bitmap;

            @Override
            public void prepare(Size size) {
                super.prepare(size);
                mPaint = new Paint();
                mPaint.setColor(Color.BLACK);
                mPaint.setAlpha(230);
                mPaint.setTextSize(70);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setAntiAlias(true);

                mCounter = new FPSCounterFactory.FPSCounter1();

//                Point mScreenMetrics = Utils.getScreenMetrics(getContext());
//                mScreenWidth = mScreenMetrics.x;
//                mScreenHeight = mScreenMetrics.y;
                mScreenWidth = mTextureView.getMeasuredWidth();
                mScreenHeight = mTextureView.getMeasuredHeight();


                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.record_bg);
            }

            @Override
            protected void drawCanvas(Canvas canvas) {


                canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect((int) (mScreenWidth * 0.05), (int) (mScreenHeight*0.03), (int) (mScreenWidth * 0.95), (int) (mScreenHeight*0.3)), mPaint);

                // 绘制左侧积分
                Paint scorePaint = new Paint();
                scorePaint.setAlpha(230);
                scorePaint.setStyle(Paint.Style.FILL);
                scorePaint.setAntiAlias(true);
                scorePaint.setColor(Color.WHITE);
                scorePaint.setTextSize(120);
                scorePaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/digital-7.ttf"));
                canvas.drawText("000", mScreenWidth * 0.055f, mScreenHeight * 0.26f, scorePaint);



                /*// 绘标题
                mPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
                mPaint.setTextSkewX(-0.3f);
                canvas.drawText("DIGITAL SHOTS", mScreenWidth * 0.08f, mScreenHeight * 0.08f, mPaint);

                Paint iconPaint = new Paint();
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user_icon);
                Shader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                iconPaint.setShader(shader);
                canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect((int) (mScreenWidth * 0.7), (int) (mScreenHeight * 0.02), (int) (mScreenWidth * 0.85), (int) (mScreenHeight * 0.09)), mPaint);

                // 绘制左侧计分板
                Paint scoreBackPaint = new Paint();
                scoreBackPaint.setAlpha(230);
                scoreBackPaint.setStyle(Paint.Style.FILL);
                scoreBackPaint.setAntiAlias(true);
                scoreBackPaint.setColor(Color.argb(230, 255, 140, 0));
                canvas.drawRoundRect(new RectF((int) (mScreenWidth * 0.03), (int) (mScreenHeight * 0.12), (int) (mScreenWidth * 0.22), (int) (mScreenHeight * 0.25)), 30, 30, scoreBackPaint);
                // 绘制右侧速度板
                canvas.drawRoundRect(new RectF((int) (mScreenWidth * 0.68), (int) (mScreenHeight * 0.12), (int) (mScreenWidth * 0.87), (int) (mScreenHeight * 0.25)), 30, 30, scoreBackPaint);

                // 绘制左侧积分标题
                Paint scoreTitlePaint = new Paint();
                scoreTitlePaint.setAlpha(230);
                scoreTitlePaint.setStyle(Paint.Style.FILL);
                scoreTitlePaint.setAntiAlias(true);
                scoreTitlePaint.setColor(Color.WHITE);
                scoreTitlePaint.setTextSize(50);
                scoreTitlePaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
                scoreTitlePaint.setTextSkewX(-0.3f);
                canvas.drawText("SCORE", mScreenWidth * 0.05f, mScreenHeight * 0.15f, scoreTitlePaint);
                // 绘制右侧速度标题
                canvas.drawText("SPEED", mScreenWidth * 0.7f, mScreenHeight * 0.15f, scoreTitlePaint);


                // 绘制时间背景板
                Paint timeBackPaint = new Paint();
                timeBackPaint.setAlpha(125);
                timeBackPaint.setStyle(Paint.Style.FILL);
                timeBackPaint.setAntiAlias(true);
                timeBackPaint.setColor(Color.argb(230, 211, 211, 211));
                canvas.drawRoundRect(new RectF((int) (mScreenWidth * 0.3), (int) (mScreenHeight * 0.12), (int) (mScreenWidth * 0.60), (int) (mScreenHeight * 0.26)), 30, 30, timeBackPaint);


                Paint timeTitlePaint = new Paint();
                timeTitlePaint.setAlpha(230);
                timeTitlePaint.setStyle(Paint.Style.FILL);
                timeTitlePaint.setAntiAlias(true);
                timeTitlePaint.setColor(Color.WHITE);
                timeTitlePaint.setTextSize(55);
                timeTitlePaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
                canvas.drawText("TIME LEFT", mScreenWidth * 0.325f, mScreenHeight * 0.15f, timeTitlePaint);
                canvas.drawText("CLASSIC", mScreenWidth * 0.35f, mScreenHeight * 0.25f, timeTitlePaint);


                // 绘制左侧积分
                Paint scorePaint = new Paint();
                scorePaint.setAlpha(230);
                scorePaint.setStyle(Paint.Style.FILL);
                scorePaint.setAntiAlias(true);
                scorePaint.setColor(Color.WHITE);
                scorePaint.setTextSize(120);
                scorePaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/digital-7.ttf"));
                canvas.drawText("000", mScreenWidth * 0.045f, mScreenHeight * 0.20f, scorePaint);*/
            }
        });

        VideoRecorder.Builder builder = new VideoRecorder.Builder(cameraPreview)
                .setCallbackHandler(new CallbackHandler())
                .setLogFPSEnable(false)
                .setCameraBuilder(cameraBuilder)
                .setDrawTextureListener(mEffectsManager)
                .setOutPutPath(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), File.separator + "VideoRecorder").getAbsolutePath())
                .setFrameRate(30)
                .setChannelCount(1);

        //分段录制,回删支持
        MultiPartRecorder.Builder multiBuilder = new MultiPartRecorder.Builder(builder);
        mRecorder = multiBuilder
                .addPartListener(new MultiPartRecorder.VideoPartListener() {
                    @Override
                    public void onRecordVideoPartStarted(MultiPartRecorder.Part part) {
                        LogUtil.logd("onRecordVideoPartStarted \t" + part.toString());
                    }

                    @Override
                    public void onRecordVideoPartSuccess(MultiPartRecorder.Part part) {
                        LogUtil.logd("onRecordVideoPartSuccess \t" + part.toString());
                    }

                    @Override
                    public void onRecordVideoPartFailure(MultiPartRecorder.Part part) {
                        LogUtil.loge("onRecordVideoPartFailure \t" + part.file);
                        mRecorder.removePart(part.file.getAbsolutePath());
                    }
                })
                .setMergeListener(new MultiPartRecorder.VideoMergeListener() {
                    @Override
                    public void onStart() {
                        LogUtil.logd("merge onStart");
                    }

                    @Override
                    public void onSuccess(File outFile) {
                        LogUtil.logd("merge Success \t" + outFile);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        if (Build.VERSION.SDK_INT >= 24) {
                            intent.setDataAndType(FileProvider.getUriForFile(getContext().getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", outFile), "video/*");
                            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } else {
                            intent.setDataAndType(Uri.fromFile(outFile), "video/*");
                        }
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Exception e) {
                        LogUtil.logd("merge Error \t" + e.toString());
                    }

                    /**
                     * 合并进度
                     *
                     * @param value 0 - 1
                     */
                    @Override
                    public void onProgress(float value) {
                        LogUtil.logd("merge onProgress \t" + value);
                    }

                })
                .setFileFilter(new MultiPartRecorder.FileFilter() {
                    @Override
                    public boolean filter(MultiPartRecorder.Part part) {
                        return part.duration > 1500;
                    }
                })
                .build();

        mCameraController = mRecorder.getCameraController();
//        mCameraController.setFacing(android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK);
    }


    private void stopPreview() {
        mRecorder.stopPreview();
    }

    private void startPreview() {
        if (mRecorder == null) initRecorder();
        mRecorder.startPreview();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mTextureView.getSurfaceTexture() != null) {
            startPreview();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPreview();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mainViewModel.endModel();
    }
}
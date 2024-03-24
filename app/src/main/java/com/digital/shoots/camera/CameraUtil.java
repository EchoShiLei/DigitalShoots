package com.digital.shoots.camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import com.digital.shoots.R;
import com.digital.shoots.utils.ProgressDialogUtil;
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
import com.erlei.videorecorder.util.LogUtil;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;


public class CameraUtil {

    private static volatile CameraUtil mInstance = null;

    private TextureView mTextureView;

    private Activity mActivity;

    private Context mContext;

    private MultiPartRecorder mRecorder;
    private CameraController mCameraController;
    private EffectsManager mEffectsManager;

    private Timer mRecorderTimer;

    private int mCountDown = 3;

    private int score = 0;
    private int speed = 0;

    private int recordTime;
    private String time = "00:00";


/*    @SuppressLint("HandlerLeak")
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
                mToast = Toast.makeText(mContext, output, Toast.LENGTH_SHORT);
            }
            mToast.show();
        }


    }*/

    private OnRecorderStateListener onRecorderStateListener;
    public interface OnRecorderStateListener {
        void onRecorderStart();
        void onRecorderSuccess(String outputFile);
        void onRecorderFailure();
    }

    private CameraUtil() {
    }

    public static CameraUtil getInstance() {
        if (mInstance == null) {
            synchronized (CameraUtil.class) {
                if (mInstance == null) {
                    mInstance = new CameraUtil();
                }
            }
        }
        return mInstance;
    }


    @SuppressLint("ClickableViewAccessibility")
    public void setTextureView(Activity activity, TextureView textureView) {
        this.mActivity = activity;
        this.mContext = activity;
        this.mTextureView = textureView;

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

        com.erlei.videorecorder.camera.Camera.CameraBuilder cameraBuilder = new Camera.CameraBuilder(mContext)
                .useDefaultConfig()
                .setFacing(android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK)
                .setPreviewSize(new Size(2048, 1536))
                .setRecordingHint(true)
                .setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

        //视频效果管理器
        mEffectsManager = new EffectsManager();
        mEffectsManager.addEffect(new CanvasOverlayEffect() {
            Paint mPaint;

            Paint scorePaint;

            Paint timePaint;

            int mScreenWidth;
            int mScreenHeight;

            Bitmap bitmap;

            DecimalFormat decimalFormat = new DecimalFormat("000");

            @Override
            public void prepare(Size size) {
                super.prepare(size);
                mPaint = new Paint();
                mPaint.setColor(Color.BLACK);
                mPaint.setAlpha(230);
                mPaint.setTextSize(70);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setAntiAlias(true);

                scorePaint = new Paint();
                scorePaint.setAlpha(230);
                scorePaint.setStyle(Paint.Style.FILL);
                scorePaint.setAntiAlias(true);
                scorePaint.setColor(Color.WHITE);
                scorePaint.setTextSize(80);
                scorePaint.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/digital-6.ttf"));

                timePaint = new Paint();
                timePaint.setAlpha(230);
                timePaint.setStyle(Paint.Style.FILL);
                timePaint.setAntiAlias(true);
                timePaint.setColor(Color.RED);
                timePaint.setTextSize(70);
                timePaint.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/digital-6.ttf"));

//                Point mScreenMetrics = Utils.getScreenMetrics(getContext());
//                mScreenWidth = mScreenMetrics.x;
//                mScreenHeight = mScreenMetrics.y;
                mScreenWidth = mTextureView.getMeasuredWidth();
                mScreenHeight = mTextureView.getMeasuredHeight();


                bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.record_bg);
            }

            @Override
            protected void drawCanvas(Canvas canvas) {


                canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect((int) (mScreenWidth * 0.05), (int) (mScreenHeight * 0.03), (int) (mScreenWidth * 0.95), (int) (mScreenHeight * 0.3)), mPaint);

                // 绘制左侧积分
                canvas.drawText(decimalFormat.format(getScore()), mScreenWidth * 0.053f, mScreenHeight * 0.26f, scorePaint);
                canvas.drawText(decimalFormat.format(getSpeed()), mScreenWidth * 0.767f, mScreenHeight * 0.26f, scorePaint);
                // 绘制时间部分
                canvas.drawText(getTime(), mScreenWidth * 0.385f, mScreenHeight * 0.26f, timePaint);



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
//                .setCallbackHandler(new CallbackHandler())
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
                        LogUtil.logi("onRecordVideoPartStarted \t" + part.toString());
                        if(onRecorderStateListener != null) {
                            onRecorderStateListener.onRecorderStart();
                        }
                    }

                    @Override
                    public void onRecordVideoPartSuccess(MultiPartRecorder.Part part) {
                        LogUtil.logi("onRecordVideoPartSuccess \t" + part.toString());
                        if(onRecorderStateListener != null) {
                            onRecorderStateListener.onRecorderSuccess(part.file.getAbsolutePath());
                        }
                    }

                    @Override
                    public void onRecordVideoPartFailure(MultiPartRecorder.Part part) {
                        LogUtil.logi("onRecordVideoPartFailure \t" + part.file);
                        mRecorder.removePart(part.file.getAbsolutePath());
                        if(onRecorderStateListener != null) {
                            onRecorderStateListener.onRecorderFailure();
                        }
                    }
                })
                .setMergeListener(new MultiPartRecorder.VideoMergeListener() {
                    @Override
                    public void onStart() {
                        LogUtil.logd("merge onStart");
                        Log.i("zyw", "merge onStart");
                    }

                    @Override
                    public void onSuccess(File outFile) {
                        LogUtil.logd("merge Success \t" + outFile);
                        Log.i("zyw", "merge Success \t" + outFile);
                    }

                    @Override
                    public void onError(Exception e) {
                        LogUtil.logd("merge Error \t" + e.toString());
                        Log.i("zyw", "merge Error \t" + e.toString());
                    }

                    /**
                     * 合并进度
                     *
                     * @param value 0 - 1
                     */
                    @Override
                    public void onProgress(float value) {
                        LogUtil.logd("merge onProgress \t" + value);
                        Log.i("zyw", "merge onProgress \t" + value);
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
    }


    public void stopPreview() {
        if (mRecorder != null) {
            mRecorder.stopPreview();
        }
    }

    public void startPreview() {
        if (mRecorder == null) initRecorder();
        mRecorder.startPreview();
    }

    public void startRecord(int millions, OnRecorderStateListener onRecorderStateListener) {
        this.onRecorderStateListener = onRecorderStateListener;
        recordTime = millions;
        mCountDown = 3;
        if(mRecorderTimer != null) {
            mRecorderTimer.cancel();
        }
        mRecorderTimer = new Timer();
        mRecorderTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.i("zyw", "mRecoderTimer run mCountDown = " + mCountDown + " | recordTime = " + recordTime);
                if(mCountDown == 3) {
                    if (mRecorder != null && !mRecorder.isRecordEnable()) {
                        mRecorder.setRecordEnabled(true);
                    }
                }
                if (mCountDown > 0) {
//                    time = "  " + mCountDown;
                    mCountDown--;
                    return;
                }
                if (mCountDown == 0) {
//                    time = "  GO";
                    mCountDown--;
                    return;
                }
                if(recordTime < 0) {
                    if (mRecorder != null && mRecorder.isRecordEnable()) {
                        mRecorder.setRecordEnabled(false);
                        ProgressDialogUtil.showProgressDialog(mActivity, "正在保存视频，请稍后");
                    }
                    mRecorderTimer.cancel();
                    return;
                }
//                String millionsFormatter = String.format("%02d:%02d", recordTime / 60, recordTime % 60);
//                setTime(millionsFormatter);
                recordTime--;
            }
        }, 0, 1000);
    }

    public void cancelRecord() {
        if(mRecorderTimer != null) {
            mRecorderTimer.cancel();
        }
    }

    public void endRecord() {
        Log.i("zyw", "endRecord");
        if (mRecorder != null && mRecorder.isRecordEnable()) {
            recordTime = -1;
            Log.i("zyw", "set recordTime -1");
        }
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void destroy() {
        if(mRecorder != null) {
            mRecorder.release();
        }
        if(mRecorderTimer != null) {
            mRecorderTimer.cancel();
        }
        mRecorderTimer = null;
        mEffectsManager = null;
        mCameraController = null;
        mRecorder = null;
    }
}

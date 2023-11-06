package com.digital.shoots.camera;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.MediaStoreOutputOptions;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoCapture;
import androidx.camera.video.VideoRecordEvent;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.digital.shoots.DigitalApplication;
import com.digital.shoots.R;
import com.digital.shoots.utils.BaseConstant;
import com.digital.shoots.utils.ToastUtils;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;


public class CameraUtil {

    private static volatile CameraUtil mInstance = null;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private Preview preview;
    private ProcessCameraProvider cameraProvider;


    private CameraSelector cameraSelector;


    private VideoCapture<androidx.camera.video.Recorder> videoCapture;

    private Recording recording;

    private CameraUtil() {
        init();
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

    private void init() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(DigitalApplication.getContext());


        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
    }

    private void bindPreview(LifecycleOwner context, ProcessCameraProvider cameraProvider, PreviewView view) {
        preview = new Preview.Builder()
                .build();

        preview.setSurfaceProvider(view.getSurfaceProvider());

        cameraProvider.unbindAll();
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) context, cameraSelector, preview);
    }

    public void startPreview(LifecycleOwner context, PreviewView view) {
        if (cameraProvider == null) {
            cameraProviderFuture.addListener(() -> {
                try {
                    cameraProvider = cameraProviderFuture.get();
                    bindPreview(context, cameraProvider, view);
                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }, ContextCompat.getMainExecutor(DigitalApplication.getContext()));
        } else {
            bindPreview(context, cameraProvider, view);
        }
    }


    public void startRecord(LifecycleOwner context, PreviewView view, String recordFileName) {
        if (cameraProvider == null) {
            cameraProviderFuture.addListener(() -> {
                try {
                    cameraProvider = cameraProviderFuture.get();
                    preview = new Preview.Builder()
                            .build();
                    preview.setSurfaceProvider(view.getSurfaceProvider());

                    Recorder recorder = new Recorder.Builder().setQualitySelector(QualitySelector.from(Quality.HIGHEST)).build();
                    videoCapture = VideoCapture.withOutput(recorder);


                    cameraProvider.unbindAll();
                    cameraProvider.bindToLifecycle(context, cameraSelector, preview, videoCapture);

                    takeVideo(context, recordFileName);
                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }, ContextCompat.getMainExecutor(DigitalApplication.getContext()));
        } else {
            preview = new Preview.Builder()
                    .build();
            preview.setSurfaceProvider(view.getSurfaceProvider());

            Recorder recorder = new Recorder.Builder().setQualitySelector(QualitySelector.from(Quality.HIGHEST)).build();
            videoCapture = VideoCapture.withOutput(recorder);


            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(context, cameraSelector, preview, videoCapture);

            takeVideo(context, recordFileName);
        }
    }

    private void takeVideo(LifecycleOwner context, String recordFileName) {
        if (recording != null) {
            recording.stop();
            recording = null;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, recordFileName);
        contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, BaseConstant.VIDEO_PATH);
        }
        MediaStoreOutputOptions outputOptions = new MediaStoreOutputOptions.Builder(DigitalApplication.getContext().getContentResolver(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                .setContentValues(contentValues).build();
        if (ActivityCompat.checkSelfPermission(DigitalApplication.getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ToastUtils.showToast(R.string.request_camera_permission);
            return;
        }
        recording = videoCapture.getOutput().prepareRecording(DigitalApplication.getContext(), outputOptions)
                .withAudioEnabled().start(ContextCompat.getMainExecutor(DigitalApplication.getContext()), videoRecordEvent -> {
                    if (videoRecordEvent instanceof VideoRecordEvent.Start) {

                    } else if (videoRecordEvent instanceof VideoRecordEvent.Pause) {

                    } else if (videoRecordEvent instanceof VideoRecordEvent.Resume) {

                    } else if (videoRecordEvent instanceof VideoRecordEvent.Status) {

                    } else if (videoRecordEvent instanceof VideoRecordEvent.Finalize) {
                        VideoRecordEvent.Finalize finalize = (VideoRecordEvent.Finalize) videoRecordEvent;
                        if (!finalize.hasError()) {
                            Log.i("zyw", "record success");
                        } else {
                            int error = finalize.getError();
                            Log.i("zyw", "record error " + error);
                            if(recording != null) {
                                recording.close();
                                recording = null;
                            }
                        }
                    }
                });
    }


    public void stopRecordVideo() {
        if (recording != null) {
            recording.stop();
            recording = null;
        }
    }

    public void release() {
        if (recording != null) {
            recording.stop();
            recording = null;
        }
        if(cameraProvider != null) {
            cameraProvider.unbindAll();
            cameraProvider = null;
        }
        cameraProviderFuture = null;
    }


}

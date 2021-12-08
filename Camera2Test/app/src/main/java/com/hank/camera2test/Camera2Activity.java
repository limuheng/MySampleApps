package com.hank.camera2test;

import android.content.DialogInterface;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.hank.camera2test.camera2.CameraController;
import com.hank.camera2test.camera2.ICameraController;
import com.hank.camera2test.ui.BaseActivity;
import com.hank.camera2test.utils.PermissionUtils;

import java.io.File;
import java.util.List;

public class Camera2Activity extends BaseActivity {
    private static final String TAG = Camera2Activity.class.getSimpleName();

    private static final int CODE_REQUEST_PERMISSION = 1001;

    private ICameraController mCameraController;

    private HandlerThread mBackgroundThread;
    // The handler must be bound to a thread with looper
    // Callback will be execute on the handler thread.
    private Handler mCameraHandler;
    private Handler mMainHandler;

    private TextureView mTextureView;
    private ImageButton mCaptureBtn;
    private ImageButton mRecordBtn;

    private final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            mCameraHandler.sendEmptyMessage(CameraController.MSG_OPEN_CAMERA);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            if (mCameraController != null) {
                configureTransform(mCameraController.getPreviewSize(), width, height);
            }
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }
    };

    private Handler.Callback mHandlerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case CameraController.MSG_OPEN_CAMERA: {
                    mCameraController.openCamera(mCameraController.getBackCamId(),
                            mTextureView.getWidth(), mTextureView.getHeight());
                    break;
                }
                case CameraController.MSG_CREATE_SESSION: {
                    mCameraController.createSession(mTextureView.getSurfaceTexture());
                    break;
                }
                case CameraController.MSG_START_PREVIEW: {
                    mCameraController.startPreview();
                    break;
                }
                case CameraController.MSG_READY_TO_CAPTURE: {
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCaptureBtn.setEnabled(true);
                            mRecordBtn.setEnabled(true);
                        }
                    });
                    break;
                }
                case CameraController.MSG_TAKE_PICTURE: {
                    mCameraController.stopPreview();
                    mCameraController.takePicture();
                    break;
                }
                case CameraController.MSG_START_RECORDING: {
                    // TODO mCameraHandler.sendEmptyMessage(CameraController.MSG_STOP_PREVIEW);
                    mCameraController.stopPreview();
                    mCameraController.startRecording();
                    break;
                }
                case CameraController.MSG_STOP_RECORDING: {
                    mCameraController.stopRecording();
                    mCameraHandler.sendEmptyMessage(CameraController.MSG_START_PREVIEW);
                    break;
                }
            }
            return false;
        }
    };

    private View.OnClickListener mBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.capture_photo == v.getId()) {
                mCameraHandler.sendEmptyMessage(CameraController.MSG_TAKE_PICTURE);
            } else if (R.id.record == v.getId()) {
                boolean isRecording = mCameraController.isRecording();
                Log.d(TAG, "isRecording: " + isRecording);
                int iconRes = isRecording ? R.drawable.ic_start_record : R.drawable.ic_stop_record;
                mRecordBtn.setImageResource(iconRes);
                mCameraHandler.sendEmptyMessage(isRecording ?
                        CameraController.MSG_STOP_RECORDING : CameraController.MSG_START_RECORDING);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        mTextureView = findViewById(R.id.texture_view);

        mCaptureBtn = findViewById(R.id.capture_photo);
        mCaptureBtn.setEnabled(false);
        mCaptureBtn.setOnClickListener(mBtnListener);

        mRecordBtn = findViewById(R.id.record);
        mRecordBtn.setEnabled(false);
        mRecordBtn.setOnClickListener(mBtnListener);

        mMainHandler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();

        PermissionUtils permissionUtils = PermissionUtils.getInstance(getApplicationContext());
        List<String> missedPermissions = permissionUtils.getMissedPermissions();
        if (!missedPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                missedPermissions.toArray(new String[0]), CODE_REQUEST_PERMISSION
            );
        } else {
            // Create CameraController before TextureView is available
            if (mCameraController == null) {
                mCameraController = ICameraController.Factory.createCameraController(
                        getApplicationContext(), getWindowManager(), mCameraHandler);
                File outputDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                if (outputDir != null) {
                    mCameraController.setUpOutputDir(outputDir);
                } else {
                    Log.e(TAG, "Unable to get output directory");
                }
            }

            if (mTextureView.isAvailable()) {
                mCameraHandler.sendEmptyMessage(CameraController.MSG_OPEN_CAMERA);
            } else {
                mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
            }
        }
    }

    @Override
    protected void onPause() {
        if (mCameraController != null) {
            mCameraController.closeCamera();
        }
        stopBackgroundThread();
        super.onPause();
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Thread");
        mBackgroundThread.start();
        mCameraHandler = new Handler(mBackgroundThread.getLooper(), mHandlerCallback);
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mCameraHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(Size previewSize, int viewWidth, int viewHeight) {
        if (null == mTextureView || null == previewSize) {
            return;
        }
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / previewSize.getHeight(),
                    (float) viewWidth / previewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(this);
        AlertDialog dlg = dlgBuilder.setMessage(message).setPositiveButton(
            android.R.string.ok,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            }
        ).create();
        dlg.show();
    }

    // Returns true if the device supports the required hardware level, or better.
    private boolean isHardwareLevelSupported(CameraCharacteristics c, int requiredLevel) {
        final int[] sortedHwLevels = {
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY,
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL,
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED,
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL,
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3
        };
        Integer deviceLevel = c.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
        if (deviceLevel == null) {
            return false;
        }

        if (requiredLevel == deviceLevel) {
            return true;
        }

        for (int sortedLevel : sortedHwLevels) {
            if (sortedLevel == requiredLevel) {
                return true;
            } else if (sortedLevel == deviceLevel) {
                return false;
            }
        }
        return false; // Should never reach here
    }
}

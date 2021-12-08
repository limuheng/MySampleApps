package com.hank.camera2test.camera2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hank.camera2test.recording.IVideoRecorder;
import com.hank.camera2test.recording.VideoRecorderImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class CameraController implements ICameraController {
    private static final String TAG = CameraController.class.getSimpleName();

    public static final int MSG_OPEN_CAMERA = 0;
    public static final int MSG_CREATE_SESSION = 1;
    public static final int MSG_START_PREVIEW = 2;
    public static final int MSG_STOP_PREVIEW = 3;
    public static final int MSG_READY_TO_CAPTURE = 4;
    public static final int MSG_TAKE_PICTURE = 5;
    public static final int MSG_START_RECORDING = 6;
    public static final int MSG_STOP_RECORDING = 7;

    private Context mContext;
    private WindowManager mWindowManager;
    private CameraManager mCameraService;

    private CameraCaptureSession mSession;
    private CameraDevice mCamera;

    private String mBackCamId;
    private String mFrontCamId;

    private Map<String, CameraCharacteristics> mCameraMaps = new HashMap<>();

    private String mCameraId;

    private Surface mPreviewSurface;
    private Surface mRecordSurface;
    private Size mPreviewSize;

    private File mSavingDir;
    private ImageReader mImgReader;

    private Handler mHandler;

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

    private Object mCaptureObj = new Object();

    private AtomicBoolean mIsRecording = new AtomicBoolean(false);

    private IVideoRecorder mVideoRecorder = new VideoRecorderImpl();

    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.d(TAG, "Camera[" + camera.getId() + "] was opened");
            mCameraId = camera.getId();
            mCamera = camera;
            // Create a capture session to accept capture requests
            mHandler.sendEmptyMessage(MSG_CREATE_SESSION);
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.d(TAG, "Camera[" + camera.getId() + "] was disconnected");
            closeCamera();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.d(TAG, "Failed to open Camera[" + camera.getId() + "], ERROR NO.: " + error);
            closeCamera();
        }

        @Override
        public void onClosed(@NonNull CameraDevice camera) {
            Log.d(TAG, "Camera[" + camera.getId() + "] was closed");
            mCamera = null;
        }
    };

    private final CameraCaptureSession.StateCallback mSessionCallback
            = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            Log.i(TAG, "[CameraCaptureSession] onConfigured");
            if (null == mCamera) {
                Log.e(TAG, "[CameraCaptureSession] CameraDevice is null!");
                return;
            }
            mSession = session;
            // Start preview
            mHandler.sendEmptyMessage(MSG_START_PREVIEW);
            // Enable take picture button
            mHandler.sendEmptyMessage(MSG_READY_TO_CAPTURE);
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Log.e(TAG, "[CameraCaptureSession] onConfigureFailed");
            mSession = null;
        }
    };

    private final CameraCaptureSession.CaptureCallback mCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureStarted(
                @NonNull CameraCaptureSession session,@NonNull CaptureRequest request,
                long timestamp, long frameNumber) {
            //Log.d(TAG, "[CaptureCallback] onCaptureStarted");
        }

        @Override
        public void onCaptureProgressed(
                @NonNull CameraCaptureSession session, @NonNull CaptureRequest request,
                @NonNull CaptureResult partialResult) {
            //Log.d(TAG, "[CaptureCallback] onCaptureProgressed");
        }

        @Override
        public void onCaptureCompleted(
                @NonNull CameraCaptureSession session, @NonNull CaptureRequest request,
                @NonNull TotalCaptureResult result) {
            //Log.d(TAG, "[CaptureCallback] onCaptureCompleted");
            Object obj = request.getTag();
            if (obj == mCaptureObj) {
                // Start preview
                Log.d(TAG, "[CaptureCallback] onCaptureCompleted: restart preview");
                mHandler.sendEmptyMessage(MSG_START_PREVIEW);
            }
        }

        @Override
        public void onCaptureFailed(
                @NonNull CameraCaptureSession session, @NonNull CaptureRequest request,
                @NonNull CaptureFailure failure) {
            //Log.d(TAG, "[CaptureCallback] onCaptureFailed");
        }
    };

    private final ImageReader.OnImageAvailableListener mImgAvailableListener =
            new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            if (mSavingDir == null) {
                Log.e(TAG, "Unable to retrieve saving directory!");
                return;
            }
            File file = new File(mSavingDir, "Camera2_" + mDateFormat.format(new Date()) + ".JPG");
            Image image = reader.acquireLatestImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(file);
                output.write(bytes);
                Log.d(TAG, "Picture was saved to: " + file.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                image.close();
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    CameraController(@NonNull Context ctx, @NonNull WindowManager windowManager,
                     @NonNull Handler handler) throws NullPointerException {
        mContext = ctx;
        mWindowManager = windowManager;
        mHandler = handler;
        mCameraService = (CameraManager) ctx.getSystemService(Context.CAMERA_SERVICE);

        if (mCameraService == null) {
            throw new NullPointerException("Unable to get CameraManager Service...");
        }

        try {
            String[] cameraIds = mCameraService.getCameraIdList();
            for (String id : cameraIds) {
                CameraCharacteristics characteristics = mCameraService.getCameraCharacteristics(id);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                facing = (facing == null) ? -1 : facing;
                switch (facing) {
                    case CameraCharacteristics.LENS_FACING_BACK: {
                        Log.d(TAG, "Back Camera ID: " + id);
                        mBackCamId = id;
                        mCameraMaps.put(mBackCamId, characteristics);
                        Integer deviceLevel = characteristics.get(
                                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                        // RedMi Note 5: INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY
                        Log.d(TAG, "Supported Hardware Level: " + deviceLevel);
                        break;
                    }
                    case CameraCharacteristics.LENS_FACING_FRONT: {
                        Log.d(TAG, "Front Camera ID: " + id);
                        mFrontCamId = id;
                        mCameraMaps.put(mFrontCamId, characteristics);
                        Integer deviceLevel = characteristics.get(
                                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                        // RedMi Note 5: INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY
                        Log.d(TAG, "Supported Hardware Level: " + deviceLevel);
                        break;
                    }
                }
                if (mBackCamId != null && mFrontCamId != null) {
                    break;
                }
            }
        } catch (CameraAccessException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUpOutputDir(@NonNull File dir) {
        mSavingDir = dir;
    }

    @Override
    public String getFrontCamId() {
        return mFrontCamId;
    }

    @Override
    public String getBackCamId() {
        return mBackCamId;
    }

    @Override
    public void openCamera(String camId, int width, int height) {
        if (mContext.checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Failed to open camera becuase of permission not granted.");
            return ;
        }
        setUpOutputs(camId, width, height);
        try {
            mCameraService.openCamera(camId, mStateCallback, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * If we don't need preview, then surfaceTexture can be null.
     * */
    @Override
    public void createSession(SurfaceTexture surfaceTexture) {
        try {
            assert surfaceTexture != null;

            // We configure the size of default buffer to be the size of camera preview we want.
            surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            // This is the output Surface we need to start preview.
            mPreviewSurface = new Surface(surfaceTexture);
            Log.d(TAG, "Preview size: (" + mPreviewSize.getWidth() + ", " + mPreviewSize.getHeight());
            mVideoRecorder.setDirectory(mSavingDir);
            mVideoRecorder.setSize(new Size(1280, 960));
            mRecordSurface = mVideoRecorder.init();

            assert mCamera != null;

            mCamera.createCaptureSession(
                    Arrays.asList(mPreviewSurface, mImgReader.getSurface(), mRecordSurface),
                    mSessionCallback, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startPreview() {
        CaptureRequest.Builder previewBuilder;
        try {
            previewBuilder = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            return ;
        }
        previewBuilder.addTarget(mPreviewSurface);

        // Auto focus should be continuous for camera preview.
        previewBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

        //setAutoFlash(previewBuilder);

        try {
            mSession.setRepeatingRequest(previewBuilder.build(), mCaptureCallback, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopPreview() {
        try {
            mSession.stopRepeating();
            mSession.abortCaptures();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeCamera() {
        if (mVideoRecorder != null) {
            mVideoRecorder.release();
            mVideoRecorder = null;
        }
        if (mSession != null) {
            mSession.close();
            mSession = null;
        }
        if (mCamera != null) {
            mCamera.close();
            mCamera = null;
        }
        if (null != mImgReader) {
            mImgReader.close();
            mImgReader = null;
        }
    }

    @Override
    public Size getPreviewSize() {
        return mPreviewSize == null ? new Size(1, 1) : mPreviewSize;
    }

    @Override
    public void takePicture() {
        Log.d(TAG, "takePicture");
        try {
            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder =
                    mCamera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImgReader.getSurface());

            // Use the same AE and AF modes as the preview.
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            captureBuilder.setTag(mCaptureObj);

            mSession.capture(captureBuilder.build(), mCaptureCallback, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startRecording() {
        if (!mIsRecording.compareAndSet(false, true)) {
            return ;
        }
        Log.d(TAG, "startRecording");
        mVideoRecorder.start(mSavingDir);

        try {
            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder =
                    mCamera.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            captureBuilder.addTarget(mPreviewSurface);
            captureBuilder.addTarget(mRecordSurface);

            captureBuilder.set(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE, 1);

            mSession.setRepeatingRequest(captureBuilder.build(), mCaptureCallback, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopRecording() {
        if (!mIsRecording.compareAndSet(true, false)) {
            return ;
        }

        String outputFile = mVideoRecorder.stop();
        Log.d(TAG, "stopRecording: " + outputFile);

        try {
            mSession.stopRepeating();
            mSession.abortCaptures();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isRecording() {
        return mIsRecording.get();
    }

    private boolean needSwapDimension(@Nullable CameraCharacteristics camChars) {
        if (camChars == null) {
            return false;
        }

        boolean result = false;
        int displayRotation = mWindowManager.getDefaultDisplay().getRotation();
        Integer sensorOrientation = camChars.get(CameraCharacteristics.SENSOR_ORIENTATION);
        sensorOrientation = (sensorOrientation == null) ? 0 : sensorOrientation;

        switch (displayRotation) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180: {
                if (sensorOrientation == 90 || sensorOrientation == 270) {
                    result = true;
                }
                break;
            }
            case Surface.ROTATION_90:
            case Surface.ROTATION_270: {
                if (sensorOrientation == 0 || sensorOrientation == 180) {
                    result = true;
                }
                break;
            }
            default: {
                Log.e(TAG, "Display rotation is invalid: " + displayRotation);
            }
        }
        return result;
    }

    private void setUpOutputs(String camId, int width, int height) {
        CameraCharacteristics camChars = mCameraMaps.get(camId);

        if (camChars == null) {
            return;
        }

        if (needSwapDimension(camChars)) {
            mPreviewSize = new Size(height, width);
        } else {
            mPreviewSize = new Size(width, height);
        }

        StreamConfigurationMap map = camChars.get(
                CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        Size size;
        if (map != null) {
            size = Collections.max(
                    Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                    new CompareSizesByArea());
        } else {
            // If output size not available, use preview size by default
            size = mPreviewSize;
        }

        mImgReader = ImageReader.newInstance(size.getWidth(), size.getHeight(),
                ImageFormat.JPEG, /*maxImages*/2);
        mImgReader.setOnImageAvailableListener(
                mImgAvailableListener, mHandler);
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }
}

/**
 * @author Hank_Li@novatek
 * Off screen capture 20160607
 */

package com.nvt.fprlinker.OffScreenCapture;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.ImageReader;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;
import android.view.WindowManager;
import android.widget.Toast;

public class OffScreenCaptureService extends Service {
    private static final String TAG = OffScreenCaptureService.class.getSimpleName();

    private Semaphore mCameraOpenCloseLock = new Semaphore(1);;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCameraSession;
    private ImageReader mPreviewImageReader;
    private ImageReader mImageReader;

    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;

    private long mStartTime;
    private long mEndTime;

    private int mJpegOrientation = 0;
    public static final int ROTATE_BASE = 90;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mStartTime = System.currentTimeMillis();
        Log.d(TAG, "Service start time: 0");
        startBackgroundThread();
        openCamera();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mEndTime = System.currentTimeMillis();
        Log.d(TAG, "Elapse time: " + ((mEndTime - mStartTime)) + " msec.");
        closeCamera();
        stopBackgroundThread();
        super.onDestroy();
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        mEndTime = System.currentTimeMillis();
        //Log.d(TAG, "Open camera start time: " + ((mEndTime - mStartTime)) + " msec.");
        CameraUtils.findFacingBackCamera(this);
        CameraCharacteristics characteristics = CameraUtils.getCameraCharacteristics(this, CameraUtils.getdFacingBackCameraId());
        Size[] sizeList = CameraUtils.getConfigSize(characteristics, ImageFormat.JPEG);
        Size smallest = CameraUtils.getSmallestSize(sizeList, ImageFormat.JPEG);
        Size largest = CameraUtils.getLargestSize(sizeList, ImageFormat.JPEG);
        mPreviewImageReader = ImageReader.newInstance(smallest.getWidth(), smallest.getHeight(), ImageFormat.JPEG, CameraUtils.MAX_IMAGES);
        mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(), ImageFormat.JPEG, CameraUtils.MAX_IMAGES);
        mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);
        CameraUtils.openCamera(this, mCameraOpenCloseLock, CameraUtils.getdFacingBackCameraId(),
                mDeviceStateCallback, mBackgroundHandler);

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int displayRotation = wm.getDefaultDisplay().getRotation();
        int deviceOrientation = displayRotation * ROTATE_BASE;
        // Calculate how much degree the photo should be rotated
        mJpegOrientation = CameraUtils.getJpegOrientation(characteristics, deviceOrientation);
    }

    private void closeCameraDevice() {
        if (null != mCameraDevice) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    private void closeCameraSession() {
        if (null != mCameraSession) {
            mCameraSession.close();
            mCameraSession = null;
        }
    }

    private void closeImageReader() {
        if (mPreviewImageReader != null) {
            mPreviewImageReader.close();
            mPreviewImageReader = null;
        }

        if (mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }
    }

    private void closeCamera() {
        Log.d(TAG, "closeCamera");
        try {
            mCameraOpenCloseLock.acquire();
            closeCameraSession();
            closeCameraDevice();
            closeImageReader();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            try {
                mCameraOpenCloseLock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void createCameraSession() {
        mEndTime = System.currentTimeMillis();
        //Log.d(TAG, "Camera session create start time: " + ((mEndTime - mStartTime)) + " msec.");
        // We set up a CaptureRequest.Builder with the output Surface.
        try {
            // Qualcomm 810 does not support 2 ImageReader as output
            mCameraDevice.createCaptureSession(
                    Arrays.asList(/*mPreviewImageReader.getSurface(), */mImageReader.getSurface()),
                    mSessionStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Log.d(TAG, "onImageAvailable !!");
            mEndTime = System.currentTimeMillis();
            Log.d(TAG, "start saving img time: " + ((mEndTime - mStartTime)) + " msec.");
            try {
                // use start time to ensure there is only single image saved
                final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "PIC_" + /*System.currentTimeMillis()*/ mStartTime + ".jpg");
                new ImageSaver(reader.acquireLatestImage(), file, mJpegOrientation).run();  // acquireNextImage()
                //Toast.makeText(mContext, "Image saved path: " + mFile.getPath(), Toast.LENGTH_SHORT).show();
                mEndTime = System.currentTimeMillis();
                Log.d(TAG, "finish saving img time: " + ((mEndTime - mStartTime)) + " msec.");
                // Notify system to make media scan under the specific path
                MediaScannerConnection.scanFile(getApplicationContext(), new String[] { file.getPath() }, new String[] { "image/jpeg" }, null);
                Handler handler = new Handler(OffScreenCaptureService.this.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Image saved path: " + file.getPath(), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private final CameraDevice.StateCallback mDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            Log.d(TAG, "StateCallback onOpened");
            mEndTime = System.currentTimeMillis();
            //Log.d(TAG, "Open camera finish time: " + ((mEndTime - mStartTime)) + " msec.");
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            createCameraSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            Log.d(TAG, "StateCallback onDisconnected");
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            Log.e(TAG, "StateCallback onError");
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            stopSelf();
        }
    };

    private CameraCaptureSession.StateCallback mSessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
            Log.d(TAG, "mPreviewStateCallback onConfigured");
            mEndTime = System.currentTimeMillis();
            //Log.d(TAG, "Camera session create finish time: " + ((mEndTime - mStartTime)) + " msec.");
            if (mCameraDevice == null) {
                return ;
            }
            mCameraSession = cameraCaptureSession;

            takePicture();
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
            Log.e(TAG, "mPreviewStateCallback onConfigureFailed");
            stopSelf();
        }
    };

    private CameraCaptureSession.CaptureCallback mExposureCallback =
        new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureProgressed(CameraCaptureSession session,
                                        CaptureRequest request, CaptureResult result) {
            Log.d(TAG, "mExposureCallback onCaptureProgressed");
            Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
            if (aeState != null) {
                if (CaptureResult.CONTROL_AE_STATE_CONVERGED == aeState.intValue()) {
                    Log.d(TAG, "aeState: " + aeState.intValue() + " [CONVERGED: 2]");
                } else {
                    Log.d(TAG, "aeState: " + aeState.intValue());
                }
            } else {
                Log.d(TAG, "aeState is null");
            }
            lockFocus();
        }
    };

    private CameraCaptureSession.CaptureCallback mLockAFCallback =
        new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureProgressed(CameraCaptureSession session,
                                        CaptureRequest request, CaptureResult partialResult) {
            Log.d(TAG, "mLockAFCallback onCaptureProgressed");
            //process(partialResult);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session,
                                       CaptureRequest request, TotalCaptureResult result) {
            Log.d(TAG, "mLockAFCallback onCaptureCompleted");
            mEndTime = System.currentTimeMillis();
            Log.d(TAG, "lock af finish time: " + ((mEndTime - mStartTime)) + " msec.");
            process(result);
        }

        private void process(CaptureResult result) {
            Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
            if (afState == null) {
                Log.d(TAG, "afState is null");
            } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                       CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                Log.d(TAG, "afState: " + afState.intValue() + " [FOCUSED: 4, NOT_FOCUSED: 5]");
            } else {
                Log.d(TAG, "afState: " + afState.intValue() + " [INACTIVE: 0]");
            }
            captureStillPicture();
        }
    };

    private CameraCaptureSession.CaptureCallback mCaptureCallback =
            new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureProgressed(CameraCaptureSession session,
                                        CaptureRequest request, CaptureResult partialResult) {
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session,
                                       CaptureRequest request, TotalCaptureResult result) {
            Log.d(TAG, "mCaptureCallback onCaptureCompleted");
            mEndTime = System.currentTimeMillis();
            stopSelf();
        }
    };

    private void takePicture() {
        mEndTime = System.currentTimeMillis();
        //Log.d(TAG, "takePicture start time: " + ((mEndTime - mStartTime)) + " msec.");
        exposure();
    }

    private void exposure() {
        final CaptureRequest.Builder captureBuilder;
        try {
            captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureBuilder.addTarget(mImageReader.getSurface()/*mPreviewImageReader.getSurface()*/);

            captureBuilder.set(
                    CaptureRequest.CONTROL_MODE,
                    CaptureRequest.CONTROL_MODE_AUTO
                    );
            captureBuilder.set(
                    CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON // CONTROL_AE_MODE_ON_ALWAYS_FLASH, CONTROL_AE_MODE_ON_AUTO_FLASH
                    );
            captureBuilder.set(
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START
                    );

            CaptureRequest exposureRequest = captureBuilder.build();
            mCameraSession.capture(exposureRequest, mExposureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void lockFocus() {
        mEndTime = System.currentTimeMillis();
        Log.d(TAG, "lock af start time: " + ((mEndTime - mStartTime)) + " msec.");
        final CaptureRequest.Builder captureBuilder;
        try {
            captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            // Qualcomm 810 does not support 2 ImageReader as output, otherwise we could use PreviewImageReader for AF
            captureBuilder.addTarget(mImageReader.getSurface()/*mPreviewImageReader.getSurface()*/);

            captureBuilder.set(
                  CaptureRequest.CONTROL_AF_MODE,
                  CaptureRequest.CONTROL_AF_MODE_AUTO
                  );
            captureBuilder.set(
                    CaptureRequest.CONTROL_AF_TRIGGER,
                    CaptureRequest.CONTROL_AF_TRIGGER_START
                    );

            CaptureRequest lockAfRequest = captureBuilder.build();
            mCameraSession.capture(lockAfRequest, mLockAFCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void captureStillPicture() {
        if (null == mCameraDevice) {
            return;
        }
        // This is the CaptureRequest.Builder that we use to take a picture.
        final CaptureRequest.Builder captureBuilder;
        try {
            mEndTime = System.currentTimeMillis();
            Log.d(TAG, "captureStillPicture start time: " + ((mEndTime - mStartTime)) + " msec.");
            // Use TEMPLATE_STILL_CAPTURE will not get an appropriate exposure
            // So we use TEMPLATE_PREVIEW instead.
            captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());

            mCameraSession.capture(captureBuilder.build(), mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}

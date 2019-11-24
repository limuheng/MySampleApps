/**
 * @author Hank_Li@novatek
 * Off screen capture 20160607
 */

package com.nvt.fprlinker.OffScreenCapture;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ExifInterface;
import android.os.Handler;
import android.util.Log;
import android.util.Size;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.nvt.fprlinker.R;
import com.nvt.fprlinker.util.PreferencesUtils;

public class CameraUtils {
    private static final String TAG = CameraUtils.class.getSimpleName();

    public static final int MAX_IMAGES = 2;

    private static CameraUtils sInstance = null;

    public static final int REQ_CAMERA_PERMISSION = 110001;

    public static final int ROTATE_90 = 90;
    public static final int ROTATE_180 = 180;
    public static final int ROTATE_270 = 270;

    synchronized public static CameraUtils getInstance() {
        if (sInstance == null) {
            sInstance = new CameraUtils();
        }
        return sInstance;
    }

    private static CameraManager sCameraManager = null;

    synchronized public static CameraManager getManager(Context context) {
        if (sCameraManager == null) {
            try {
                sCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sCameraManager;
    }

    private static String[] sCameraIds = null;

    public static String[] getCameraIdList(Context context) {
        if (sCameraIds == null) {
            try {
                sCameraIds = getManager(context).getCameraIdList();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sCameraIds;
    }

    private static String sdFacingBackCameraId = null;

    public static void setdFacingBackCameraId(String cId) {
        sdFacingBackCameraId = cId;
    }

    public static String getdFacingBackCameraId() {
        return sdFacingBackCameraId;
    }

    public static String findFacingBackCamera(Context context) {
        try {
            String[] cameraIdList = getCameraIdList(context);
            for (String cameraId : cameraIdList) {
                CameraCharacteristics characteristics = getCameraCharacteristics(context, cameraId);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    setdFacingBackCameraId(cameraId);
                    return cameraId;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed go get facing back camera...");
            e.printStackTrace();
        }
        return null;
    }

    public static CameraCharacteristics getCameraCharacteristics(Context context, String cameraId) {
        CameraCharacteristics cameraCharacteristics = null;
        try {
            cameraCharacteristics = getManager(context).getCameraCharacteristics(cameraId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cameraCharacteristics;
    }

    public static Size[] getConfigSize(CameraCharacteristics characteristics, int format) {
        Size[] sizes = null;
        try {
            StreamConfigurationMap configs = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            sizes = configs.getOutputSizes(format/*ImageFormat.JPEG*/);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sizes;
    }

    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    public static Size getLargestSize(Size[] sizes, int format) {
        return Collections.max(Arrays.asList(sizes), new CompareSizesByArea());
    }

    public static Size getSmallestSize(Size[] sizes, int format) {
        return Collections.min(Arrays.asList(sizes), new CompareSizesByArea());
    }

    public static boolean openCamera(Context context, Semaphore openCloseLock, String cameraId,
                              CameraDevice.StateCallback callback, Handler handler) {
        boolean openSuccess = false;
        // BEGIN Hank_Li@novatek, permission check 20160623
        if (!PermissionUtils.getInstance().hasPermission(context, Manifest.permission.CAMERA)) {
            return openSuccess;
        }
        // END Hank_Li@novatek, permission check 20160623
        try {
            if (!openCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            CameraUtils.getManager(context).openCamera(cameraId, callback, handler);
            openSuccess = true;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
        return openSuccess;
    }

    public static int getJpegOrientation(CameraCharacteristics c, int deviceOrientation) {
        if (deviceOrientation == android.view.OrientationEventListener.ORIENTATION_UNKNOWN) return 0;
        int sensorOrientation = c.get(CameraCharacteristics.SENSOR_ORIENTATION);

        // Round device orientation to a multiple of 90
        deviceOrientation = (deviceOrientation + 45) / 90 * 90;

        // Reverse device orientation for front-facing cameras
        boolean facingFront = c.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT;
        if (facingFront) deviceOrientation = -deviceOrientation;

        // Calculate desired JPEG orientation relative to camera orientation to make
        // the image upright relative to the device orientation
        int jpegOrientation = (sensorOrientation - deviceOrientation + 360) % 360;

        return jpegOrientation;
    }

    public static boolean rotatePhoto(String fileName, int roation) {
        boolean result = false;
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(fileName);
            if (roation != 0) {
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                Matrix mtx = new Matrix();
                mtx.preRotate(roation);

               // Rotating Bitmap & convert to ARGB_8888, required by tess
               bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
               bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
               FileOutputStream out = null;
               try {
                   out = new FileOutputStream(fileName);
                   bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                   result = true;
               } catch (Exception e) {
                   e.printStackTrace();
               } finally {
                   try {
                       if (out != null) {
                           out.close();
                       }
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
            }
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "correctPhotoOrientation fail", e);
        }
        return result;
    }

    public static boolean setPhotoExifOrientation(String fileName, int mRotation) {
        boolean result = false;
        ExifInterface exif = null;
        try {
            String rotateValue = null;
            exif = new ExifInterface(fileName);
//            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//            Log.d(TAG, "exifOrientation: " + exifOrientation);
            Log.d(TAG, "mRotation: " + mRotation);
            switch (mRotation) {
                case ROTATE_90: {
                    rotateValue = String.valueOf(ExifInterface.ORIENTATION_ROTATE_90);
                    break;
                }
                case ROTATE_180: {
                    rotateValue = String.valueOf(ExifInterface.ORIENTATION_ROTATE_180);
                    break;
                }
                case ROTATE_270: {
                    rotateValue = String.valueOf(ExifInterface.ORIENTATION_ROTATE_270);
                    break;
                }
            }
            if (rotateValue != null) {
                exif.setAttribute(ExifInterface.TAG_ORIENTATION, rotateValue);
                exif.saveAttributes();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean hasCamera(Context context) {
        String[] cameraIdList = getCameraIdList(context);
        if (cameraIdList != null && cameraIdList.length > 0) {
            return true;
        }
        return false;
    }

    // BEGIN hank_li@novatek, add capture by default camera app 20160630
    private int mCaptureMode = CaptureMode.BY_DEFAULT_CAMERA_APP;

    public interface CaptureMode {
        public static final int BY_DEFAULT_CAMERA_APP = 0;
        public static final int BY_CAMERA2_API = 1;
    }

    public void capture(Context context) {
        mCaptureMode = AdbCmdUtils.execGetPropIntCmd(AdbCmdUtils.PROP_CAPTURE_MODE);
        if (mCaptureMode == -1) {
            mCaptureMode = CaptureMode.BY_DEFAULT_CAMERA_APP;
        }
        switch (mCaptureMode) {
            case CaptureMode.BY_DEFAULT_CAMERA_APP: {
                captureByDefaultCameraApp(context);
                break;
            }
            case CaptureMode.BY_CAMERA2_API: {
                try {
                    Intent intent = new Intent(context, OffScreenCaptureService.class);
                    context.startService(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to start OffScreenCaptureService...");
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private static final String sCameraPackageName = "com.android.camera2"; // "org.codeaurora.snapcam"
    private static final String sCameraActivityName = "com.android.camera.CameraLauncher"; // "com.android.camera.CameraLauncher"

    public void captureByDefaultCameraApp(final Context context) {
        Intent launchAppInfo = new Intent();
        launchAppInfo.setComponent(new ComponentName(sCameraPackageName, sCameraActivityName));
        launchAppInfo.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(launchAppInfo);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(AdbCmdUtils.INT_EVENT_DELAY);
                        AdbCmdUtils.execCaptureCmd();
                        Thread.sleep(AdbCmdUtils.INT_EVENT_DELAY);
                        AdbCmdUtils.execBackCmd();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isCameraLaunedBefore(Context context) {
        return PreferencesUtils.getBoolPrefs(context, PreferencesUtils.KEY_CAMERA_LAUNCHED, false);
    }

    public boolean hintUserToFirstLaunchCamera(Context context) {
        if (isCaptureByDefaultCamera(context)) {
            if (!isCameraLaunedBefore(context)) {
                DialogClickListenr clickListener = new DialogClickListenr(context);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.hint_title_launch_camera);
                builder.setMessage(R.string.hint_message_launch_camera);
                builder.setPositiveButton(R.string.dialog_continue, clickListener);
                builder.setNegativeButton(R.string.dialog_cancel, clickListener);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        }
        return false;
    }

    public boolean isCaptureByDefaultCamera(Context context) {
        mCaptureMode = AdbCmdUtils.execGetPropIntCmd(AdbCmdUtils.PROP_CAPTURE_MODE);
        if (mCaptureMode == -1) {
            mCaptureMode = CaptureMode.BY_DEFAULT_CAMERA_APP;
        }
        return (mCaptureMode == CaptureMode.BY_DEFAULT_CAMERA_APP);
    }

    static class DialogClickListenr implements OnClickListener {
        Context _context;

        public DialogClickListenr(Context context) {
            _context = context;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE: {
                    Intent launchAppInfo = new Intent();
                    //launchAppInfo.setComponent(new ComponentName("org.codeaurora.snapcam", "com.android.camera.CameraLauncher"));
                    launchAppInfo.setComponent(new ComponentName("com.android.camera2", "com.android.camera.CameraLauncher"));
                    launchAppInfo.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    _context.startActivity(launchAppInfo);
                    PreferencesUtils.setBoolPrefs(_context, PreferencesUtils.KEY_CAMERA_LAUNCHED, true);
                    break;
                }
                case DialogInterface.BUTTON_NEGATIVE: {
                    // Do nothing
                    break;
                }
            }
        }
        
    }
    // END hank_li@novatek, add capture by default camera app 20160630
}

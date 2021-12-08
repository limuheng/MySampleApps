package com.hank.camera2test.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PermissionUtils {
    private static final String TAG = PermissionUtils.class.getSimpleName();

    public static final String[] sRequiredPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static PermissionUtils sInstance;

    public static PermissionUtils getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PermissionUtils(context);
        }
        return sInstance;
    }

    private WeakReference<Context> mContext;

    private PermissionUtils(Context ctx) {
        mContext = new WeakReference<Context> (ctx);
    }

    public boolean isPermissionGranted(String permission) {
        Context ctx = mContext.get();
        if (ctx == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionStatus = ctx.checkSelfPermission(permission);
            Log.d(TAG, "permission:$permission result: " + permissionStatus);
            return permissionStatus == PackageManager.PERMISSION_GRANTED;
        } else {
            //permission is always granted at installation time on device <23
            return true;
        }
    }

    public List<String> getMissedPermissions() {
        List<String> missedPermissions = new ArrayList<> ();
        for (String permission : sRequiredPermissions) {
            if (!isPermissionGranted(permission)) {
                missedPermissions.add(permission);
            }
        }
        return missedPermissions;
    }
}

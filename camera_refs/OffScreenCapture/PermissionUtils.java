/**
 * @author Hank_Li@novatek
 * permission check 20160623
 */
package com.nvt.fprlinker.OffScreenCapture;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class PermissionUtils {
    private static final String TAG = PermissionUtils.class.getSimpleName();

    private static PermissionUtils sInstance;

    private PermissionUtils() {}

    public static final int REQ_PERMISSION_LIST = 110000;
    public static final int REQ_PERMISSION_CAMERA = 110001;

    synchronized public static PermissionUtils getInstance() {
        if (sInstance == null) {
            sInstance = new PermissionUtils();
        }
        return sInstance;
    }

    public List<String> getNeedCheckList(Context context, String[] permissions) {
        List<String> list = new ArrayList<String> ();
        for (String permission : permissions) {
            if (!hasPermission(context, permission)) {
                list.add(permission);
                Log.d(TAG, "Need permission: " + permission);
            }
        }
        return list;
    }

    public boolean hasPermission(Context context, String permission) {
        return (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
    }

    public void performPermissionCheck(Activity activity, String[] permissionArray, int reqCode) {
        ActivityCompat.requestPermissions(activity, permissionArray, reqCode);
    }
}

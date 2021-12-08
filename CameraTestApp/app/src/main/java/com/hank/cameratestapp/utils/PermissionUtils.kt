package com.hank.cameratestapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log

interface PermissionUtils  {

    companion object {
        val TAG = PermissionUtils::class.java.simpleName

        private var instance: PermissionUtils? = null

        fun getInstance(context: Context): PermissionUtils {
            if (instance == null) {
                instance = PermissionUtilsImpl(context)
            }
            return instance as PermissionUtils
        }
    }

    fun getRequiredPermissions(): Array<String>
    fun isPermissionGranted(permission: String): Boolean
    /**
     * @return list of missing permissions
     */
    fun checkPermissions(requiredPermissions: Array<String>): List<String>

    private class PermissionUtilsImpl(private val context: Context): PermissionUtils {
        override fun getRequiredPermissions(): Array<String> {
            return arrayOf(Manifest.permission.CAMERA,
                          Manifest.permission.RECORD_AUDIO,
                          Manifest.permission.WRITE_EXTERNAL_STORAGE,
                          Manifest.permission.ACCESS_FINE_LOCATION)
        }

        override fun isPermissionGranted(permission: String): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val result = context.checkSelfPermission(permission)
                Log.d(TAG, "permission:$permission result:$result")
                result == PackageManager.PERMISSION_GRANTED
            } else {
                //permission is always granted at installation time on device <23
                true
            }
        }

        override fun checkPermissions(requiredPermissions: Array<String>): List<String> {
            val missedPermissions = ArrayList<String>()

            for (permission in requiredPermissions) {
                if (!isPermissionGranted(permission)) {
                    missedPermissions.add(permission)
                }
            }

            return missedPermissions
        }
    }

}
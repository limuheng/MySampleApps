package com.hank.cameratestapp.camera2

import android.content.Context
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.util.Log
import java.lang.Exception
import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.os.Build
import com.hank.cameratestapp.utils.PermissionUtils


class Camera2Manager(private val context: Context): ICamera2Manager {

    companion object {
        val TAG = Camera2Manager::class.java.simpleName
    }

    // The handler on which the camera callback should be invoked, or null to use the current thread's looper.
    private val handler = Handler()

    private var cameraService : CameraManager? = null

    init {
        try {
            cameraService = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get CameraService", e)
        }
    }

    override fun checkCameraHardware(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    override fun getNumberOfCameras(): Int {
        return cameraService?.cameraIdList?.size ?: 0
    }

    @SuppressLint("MissingPermission")
    override fun getCameraInstance(id: String, callback: CameraDevice.StateCallback): Boolean {
        try {
            if (!PermissionUtils.getInstance(context).isPermissionGranted(permission.CAMERA)) {
                Log.e(TAG, "Permission denied when trying to open camera")
                return false
            }
            cameraService?.openCamera(id, callback, handler)
            return true
        } catch (e: CameraAccessException) {
            Log.e(TAG, "CameraAccessException", e)
        }
        return false
    }

    override fun getFrontFacingCameraId(): String? {
        var frontFacingId: String? = null
        cameraService?.cameraIdList?.forEach {
            val characteristics = cameraService!!.getCameraCharacteristics(it)
            if (isFrontFacingCamera(characteristics)) {
                Log.d(TAG, "getFrontFacingCameraId : frontFacingId=$it")
                frontFacingId = it
            }
        }
        return frontFacingId
    }

    override fun getBackFacingCameraId(): String? {
        var backFacingId: String? = null
        cameraService?.cameraIdList?.forEach {
            val characteristics = cameraService!!.getCameraCharacteristics(it)
            if (isBackFacingCamera(characteristics)) {
                Log.d(TAG, "getBackFacingCameraId : backFacingId=$it")
                Log.d(TAG, "getBackFacingCameraId : device API = ${Build.VERSION.SDK_INT}")
                // Don't know why physicalIds is empty, so mark this snippet
                /*
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val physicalIds = characteristics.physicalCameraIds
                    Log.d(TAG, "isEmpty : ${physicalIds.isEmpty()}")
                    physicalIds.iterator().forEach { id ->
                        Log.d(TAG, "getBackFacingCameraId : physical id=$id")
                        backFacingId += "$id;"
                    }
                } else {
                    backFacingId = it
                }
                */
                backFacingId = it
            }
        }
        return backFacingId
    }

    private fun isFrontFacingCamera(characteristics: CameraCharacteristics): Boolean {
        return characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT
    }

    private fun isBackFacingCamera(characteristics: CameraCharacteristics): Boolean {
        return characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
    }

    override fun getCameraCharacteristics(id: String): CameraCharacteristics? {
        return cameraService?.getCameraCharacteristics(id)
    }
}
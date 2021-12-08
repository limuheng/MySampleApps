@file:Suppress("DEPRECATION")

package com.hank.cameratestapp.camera1

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.util.Log
import android.view.Surface
import android.view.WindowManager

@Suppress("DEPRECATION")
class CameraManager(private val context: Context): ICameraManager {

    companion object {
        val TAG = CameraManager::class.java.simpleName
    }

    private var windowManager: WindowManager? = null

    private var numberOfCameras: Int = -1
    private var frontFacingCameraId: Int = -1
    private var backFacingCameraId: Int = -1

    init {
        try {
            windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get WindowManager", e)
        }
    }

    override fun checkCameraHardware(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    override fun getNumberOfCameras(): Int {
        if (numberOfCameras < 0) {
            numberOfCameras = Camera.getNumberOfCameras()
        }
        return numberOfCameras
    }

    override fun getCameraInstance(id: Int?): Camera? {
        return try {
            // attempt to get a Camera instance
            id?.let {
                Camera.open(it)
            } ?: run {
                Camera.open()
            }
        } catch (e: Exception) {
            // Camera is not available (in use or does not exist)
            Log.e(TAG, "getCameraInstance($id) : ${e.message}", e)
            null // returns null if camera is unavailable
        }
    }

    override fun getFrontFacingCameraId(): Int {
        if (frontFacingCameraId >= 0) {
            return frontFacingCameraId
        }
        val cameraCount = getNumberOfCameras()
        for (i in 0 until cameraCount) {
            val cameraInfo = CameraInfo()
            Camera.getCameraInfo(i, cameraInfo)
            Log.d(TAG, "getFrontFacingCameraId : camera$i, facing=${cameraInfo.facing}, orientation=${cameraInfo.orientation}")
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
                frontFacingCameraId = i
                break
            }
        }
        return frontFacingCameraId
    }

    override fun getBackFacingCameraId(): Int {
        if (backFacingCameraId >= 0) {
            return backFacingCameraId
        }
        val cameraCount = getNumberOfCameras()
        for (i in 0 until cameraCount) {
            val cameraInfo = CameraInfo()
            Camera.getCameraInfo(i, cameraInfo)
            Log.d(TAG, "getFrontFacingCameraId : camera$i, facing=${cameraInfo.facing}, orientation=${cameraInfo.orientation}")
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                backFacingCameraId = i
                break
            }
        }
        return backFacingCameraId
    }

    override fun updateCameraOrientation(id: Int, camera: Camera) {
        val cameraInfo = CameraInfo()
        Camera.getCameraInfo(id, cameraInfo)

        val degrees = when (windowManager?.defaultDisplay?.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> 0
        }

        val result: Int = if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
            val rotate = (cameraInfo.orientation + degrees) % 360
            (360 - rotate) % 360 // compensate the mirror
        } else {
            (cameraInfo.orientation - degrees + 360) % 360
        }

        camera.setDisplayOrientation(result)
    }
}
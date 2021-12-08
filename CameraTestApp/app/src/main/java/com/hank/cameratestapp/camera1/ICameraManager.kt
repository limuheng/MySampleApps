@file:Suppress("DEPRECATION")

package com.hank.cameratestapp.camera1

import android.hardware.Camera

@Suppress("DEPRECATION")
interface ICameraManager {
    fun checkCameraHardware(): Boolean
    fun getNumberOfCameras(): Int
    fun getCameraInstance(id: Int? = null): Camera?
    fun getFrontFacingCameraId(): Int
    fun getBackFacingCameraId(): Int
    fun updateCameraOrientation(id: Int, camera: Camera)
}
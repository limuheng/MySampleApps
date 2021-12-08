package com.hank.cameratestapp.camera2

import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice.StateCallback

interface ICamera2Manager {
    fun checkCameraHardware(): Boolean
    fun getNumberOfCameras(): Int
    fun getCameraInstance(id: String, callback: StateCallback): Boolean
    fun getFrontFacingCameraId(): String?
    fun getBackFacingCameraId(): String?
    fun getCameraCharacteristics(id: String): CameraCharacteristics?
}
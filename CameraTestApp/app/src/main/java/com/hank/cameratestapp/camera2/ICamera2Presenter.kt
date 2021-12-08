package com.hank.cameratestapp.camera2

import android.graphics.Point
import android.graphics.Rect
import android.hardware.camera2.CameraDevice
import android.util.Size
import android.view.Surface
import com.hank.cameratestapp.utils.Callback
import com.hank.cameratestapp.utils.CameraIndicatorView

interface ICamera2Presenter {
    fun isReady(): Boolean
    fun openCamera(callback: Callback<CameraDevice>)
    fun closeCamera()
    fun getOutputSize(format: Int): Array<Size>
    fun setPreviewSize(size: Size?)
    fun setPreviewSurface(surface: Surface?)
    fun startCaptureSession()
    fun setCameraIndicatorView(view: CameraIndicatorView?)
    fun getCameraIndicatorView(): CameraIndicatorView?
    fun focusOnTouch(point: Point, parent: Rect)
}
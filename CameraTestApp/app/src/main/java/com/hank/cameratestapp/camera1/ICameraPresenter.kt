@file:Suppress("DEPRECATION")

package com.hank.cameratestapp.camera1

import android.graphics.Rect
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.hank.cameratestapp.utils.Callback
import com.hank.cameratestapp.utils.CameraIndicatorView

interface ICameraPresenter {
    fun isReady(): Boolean
    fun openCamera(callback: Callback<Camera>)
    fun closeCamera()
    fun stopPreview(): Boolean
    fun setAndStartPreview(surfaceHolder: SurfaceHolder): Boolean
    fun stopPreviewAndFreeCamera(): Boolean
    fun takePicture()
    fun setExposureCompensation(compensation: Int)
    fun getMaxEv(): Int
    fun getMinEv(): Int
    fun getEvSteps(): Int
    fun setSurfaceView(view: SurfaceView)
    fun getSurfaceView(): SurfaceView?
    fun focusOnTouch(touchArea: Rect, parent: Rect)
    fun setCameraIndicatorView(view: CameraIndicatorView?)
    fun getCameraIndicatorView(): CameraIndicatorView?
}
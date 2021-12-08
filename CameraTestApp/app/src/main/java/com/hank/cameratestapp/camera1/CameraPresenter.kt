@file:Suppress("DEPRECATION")

package com.hank.cameratestapp.camera1

import android.graphics.ImageFormat
import android.hardware.Camera
import android.util.Log
import android.view.SurfaceHolder
import java.io.IOException
import android.graphics.Rect
import android.view.SurfaceView
import java.lang.ref.WeakReference
import android.hardware.Camera.Parameters.FOCUS_MODE_AUTO
import android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
import android.os.Handler
import com.hank.cameratestapp.utils.Callback
import com.hank.cameratestapp.utils.CameraIndicatorView

const val CAM_AREA_LEFT = -1000
const val CAM_AREA_TOP = -1000
const val CAM_AREA_RIGHT = 1000
const val CAM_AREA_BOTTOM = 1000
const val CAM_AREA_WIDTH = CAM_AREA_RIGHT - CAM_AREA_LEFT
const val CAM_AREA_HEIGHT = CAM_AREA_BOTTOM - CAM_AREA_TOP
const val CAM_POS_X_SHIFT = 1000
const val CAM_POS_Y_SHIFT = 1000

class CameraPresenter(private val cameraManager: ICameraManager): ICameraPresenter, SurfaceHolder.Callback {

    companion object {
        /**
         * Convert touch position x:y to [Camera.Area] position -1000:-1000 to 1000:1000.
         */
        private fun calculateFocusArea(touchArea: Rect, parent: Rect): Rect {
            var left = touchArea.left * CAM_AREA_WIDTH / parent.width() - CAM_POS_X_SHIFT
            var top = touchArea.top * CAM_AREA_HEIGHT / parent.height() - CAM_POS_Y_SHIFT
            var right = touchArea.right * CAM_AREA_WIDTH / parent.width() - CAM_POS_X_SHIFT
            var bottom = touchArea.bottom * CAM_AREA_HEIGHT / parent.height() - CAM_POS_Y_SHIFT
            return Rect(
                if (left >= CAM_AREA_LEFT) left else CAM_AREA_LEFT,
                if (top >= CAM_AREA_TOP) top else CAM_AREA_TOP,
                if (right <= CAM_AREA_RIGHT) right else CAM_AREA_RIGHT,
                if (bottom <= CAM_AREA_BOTTOM) bottom else CAM_AREA_BOTTOM)
        }
    }

    val TAG = CameraPresenter::class.java.simpleName

    private val callback = object : Callback<Boolean> {
        override fun invoke(data: Boolean?, throwable: Throwable?) {
            if (data == true) {
                camera?.startPreview()
            }
        }
    }

    private var camera: Camera? = null
    private val photoHandler: PhotoHandler = PhotoHandler(callback)

    private var maxEv: Int = 0
    private var minEv: Int = 0
    private var evSteps: Int = 0

    private var isMeteringAreaSupported = false

    private var wrSurfaceView: WeakReference<SurfaceView>? = null
    private var cameraIndicatorView: CameraIndicatorView? = null

    private val handler = Handler()

    private val audoFocusCallback = Camera.AutoFocusCallback { success, _ ->
        Log.d(TAG, "audoFocusCallback : success=$success")
        if (success) {
            camera?.cancelAutoFocus()
        }
        setContinuousFocus()
    }

    override fun isReady(): Boolean {
        return (camera != null)
    }

    override fun openCamera(callback: Callback<Camera>) {
        val cameraId = cameraManager.getBackFacingCameraId()
        camera = cameraManager.getCameraInstance(cameraId)
        camera?.let {
            cameraManager.updateCameraOrientation(cameraId, it)

            setContinuousFocus()

            val parameters = camera?.parameters

            val previewSizeList = parameters?.supportedPreviewSizes
            if (previewSizeList?.isNotEmpty() == true) {
                val previewSize = previewSizeList[0]
                Log.d(TAG, "previewSize : w=${previewSize.width}, h=${previewSize.height}")
                parameters.setPreviewSize(previewSize.width, previewSize.height)
            }

            val picFormatList = parameters?.supportedPictureFormats
            if (picFormatList?.contains(ImageFormat.JPEG) == true) {
                parameters.pictureFormat = ImageFormat.JPEG
            }

            val picSizeList = parameters?.supportedPictureSizes
            if (picSizeList?.isNotEmpty() == true) {
                //picSizeList.forEach { Log.d(TAG, "supported size : w=${it.width}, h=${it.height}") }
                val toSize = picSizeList[0/*picSizeList.size / 2*/]
                // TODO rotate the image by device orientation
                Log.d(TAG, "toSize : w=${toSize.width}, h=${toSize.height}")
                parameters.setPictureSize(toSize.width, toSize.height)
            }

            isMeteringAreaSupported = parameters?.maxNumMeteringAreas?.let { n -> n > 0 } ?: false
            Log.d(TAG, "openCamera : isMeteringAreaSupported=$isMeteringAreaSupported")

            camera?.parameters = parameters

            maxEv = parameters?.maxExposureCompensation ?: 0
            minEv = parameters?.minExposureCompensation ?: 0
            evSteps = maxEv - minEv
            Log.d(TAG, "maxEv=$maxEv, minEv=$minEv, evSteps=$evSteps")

            callback(it, null)
        } ?: run {
            callback(null, Exception("Failed to open camera"))
        }
    }

    private fun setContinuousFocus() {
        val parameters = camera?.parameters
        val focusModeList = parameters?.supportedFocusModes
        if (focusModeList?.contains(FOCUS_MODE_CONTINUOUS_PICTURE) == true) {
            parameters.focusMode = FOCUS_MODE_CONTINUOUS_PICTURE
        }
        camera?.parameters = parameters
    }

    override fun closeCamera() {
        camera?.stopPreview()
        camera?.release()
        camera = null
    }

    override fun setAndStartPreview(surfaceHolder: SurfaceHolder): Boolean {
        camera?.apply {
            try {
                this.setPreviewDisplay(surfaceHolder)
                this.startPreview()
                return true
            } catch (e: Exception) {
                Log.w(TAG, "setAndStartPpreview : Error starting camera preview: ${e.message}")
            }
        }
        return false
    }

    override fun stopPreview(): Boolean {
        camera?.let {
            try {
                it.stopPreview()
                return true
            } catch (e: Exception) {
                Log.w(TAG, "stopPreview : Error stopping camera preview: ${e.message}")
            }
        }
        return false
    }

    override fun stopPreviewAndFreeCamera(): Boolean {
        camera?.let {
            try {
                it.stopPreview()
                it.release()
            } catch (e: Exception) {
                Log.w(TAG, "stopPreviewAndFreeCamera : Error stopping camera preview: ${e.message}")
            }
        }
        return false
    }

    override fun takePicture() {
        camera?.takePicture(Camera.ShutterCallback {
            Log.d(TAG, "takePicture : ShutterCallback")
        }, null, photoHandler)
    }

    override fun setExposureCompensation(compensation: Int) {
        val parameters = camera?.parameters
        if (parameters?.isAutoExposureLockSupported == true) {
            Log.d(TAG, "setExposureCompensation : old exposure=${parameters.exposureCompensation}")
            takeIf { compensation in minEv..maxEv }?.let {
                parameters.exposureCompensation = compensation
                Log.d(TAG, "setExposureCompensation : new exposure=${parameters.exposureCompensation}")
                camera?.parameters = parameters
            }
        }
    }

    override fun getMaxEv(): Int {
        return maxEv
    }

    override fun getMinEv(): Int {
        return minEv
    }

    override fun getEvSteps(): Int {
        return evSteps
    }

    override fun setSurfaceView(view: SurfaceView) {
        wrSurfaceView?.let { it.clear() }
        wrSurfaceView = WeakReference(view)
    }

    override fun getSurfaceView(): SurfaceView? {
        return wrSurfaceView?.get()
    }

    override fun focusOnTouch(touchArea: Rect, parent: Rect) {
        camera?.let {
            try {
                it.cancelAutoFocus()

                val parameters = it.parameters
                parameters.focusMode = FOCUS_MODE_AUTO
                val focusArea = calculateFocusArea(touchArea, parent)
                parameters.focusAreas = listOf(Camera.Area(focusArea, 1000))

                if (isMeteringAreaSupported) {
                    parameters.meteringAreas = parameters.focusAreas
                    //listOf(Camera.Area(focusArea, 1000))
                }

                it.parameters = parameters
                it.autoFocus(audoFocusCallback)

                cameraIndicatorView?.setHaveTouch(true, touchArea)
                cameraIndicatorView?.invalidate()

                handler.postDelayed({
                    cameraIndicatorView?.setHaveTouch(false, null)
                    cameraIndicatorView?.invalidate()
                }, 1000)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun setCameraIndicatorView(view: CameraIndicatorView?) {
        cameraIndicatorView = view
    }

    override fun getCameraIndicatorView(): CameraIndicatorView? {
        return cameraIndicatorView
    }

    /*
     * Overridden methods of SurfaceHolder.Callback
     */
    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        Log.d(TAG, "surfaceChanged : holder=$holder")
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        if (holder?.surface == null) {
            // preview surface does not exist
            return
        }

        // stop preview before making changes
        try {
            camera?.stopPreview()
        } catch (e: Exception) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        camera?.apply {
            try {
                setPreviewDisplay(holder)
                startPreview()
            } catch (e: Exception) {
                Log.w(TAG, "surfaceChanged : Error starting camera preview: ${e.message}")
            }
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        // empty. Take care of releasing the Camera preview in your activity.
        camera?.apply {
            stopPreview()
            release()
        }
        camera = null
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        Log.d(TAG, "surfaceCreated : holder=$holder")
        holder?.let {
            // The Surface has been created, now tell the camera where to draw the preview.
            camera?.apply {
                try {
                    setPreviewDisplay(holder)
                    startPreview()
                } catch (e: IOException) {
                    Log.w(TAG, "surfaceCreated : Error setting camera preview: ${e.message}")
                }
            }
        }
    }
}
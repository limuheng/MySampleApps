package com.hank.cameratestapp.camera2

import android.graphics.Point
import android.graphics.Rect
import android.hardware.camera2.*
import android.hardware.camera2.params.MeteringRectangle
import android.os.Handler
import android.util.Log
import android.util.Size
import android.view.Surface
import com.hank.cameratestapp.utils.*
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean

class Camera2Presenter(private val cameraManager: ICamera2Manager): ICamera2Presenter {

    companion object {
        val TAG = Camera2Presenter::class.java.simpleName

        /**
         * Convert touch position x:y to [Camera2 Coordinates system].
         */
        private fun calculateFocusArea(touchPoint: Point, parent: Rect, cameraArea: Rect): Rect {
            // Here we do a coordinates transformation
            // Please refer to the link for further information of the coordinates system in Camera2
            var transformedPoint = Point(touchPoint.y, parent.width() - touchPoint.x)

            // Then we do the scale transformation from preview coordinates to Camera2 Area coordinates
            transformedPoint.x = transformedPoint.x * cameraArea.width() / parent.height()
            transformedPoint.y = transformedPoint.y * cameraArea.height() / parent.width()

            val focusArea = calculateTapArea(transformedPoint.x, transformedPoint.y)
            Log.d(TAG, "focusArea = (${focusArea.left}, ${focusArea.top}, ${focusArea.right}, ${focusArea.bottom})")
            Log.d(TAG, "cameraArea = (${cameraArea.left}, ${cameraArea.top}, ${cameraArea.right}, ${cameraArea.bottom})")
            // Checking the bounds
            val res = Rect(if (focusArea.left >= cameraArea.left) focusArea.left else cameraArea.left,
                if (focusArea.top >= cameraArea.top) focusArea.top else cameraArea.top,
                if (focusArea.right <= cameraArea.right) focusArea.right else cameraArea.right,
                if (focusArea.bottom <= cameraArea.bottom) focusArea.bottom else cameraArea.bottom)
            Log.d("Camera2Presenter", "res = (${res.left}, ${res.top}, ${res.right}, ${res.bottom})")
            return res
        }

        private fun calculateTapArea(x: Int, y: Int): Rect {
            return Rect((x - 100), (y - 100), (x + 100), (y + 100))
        }
    }

    private var cameraDevice: CameraDevice? = null

    private val isApiTimeout = AtomicBoolean(false)

    private var previewSize: Size? = null
    private var previewSurface: Surface? = null

    // TODO bind handler to a background thread instead of main thread
    private val handler = Handler()

    private var previewReqBuilder: CaptureRequest.Builder? = null
    private var captureSession: CameraCaptureSession? = null

    private var cameraIndicatorView: CameraIndicatorView? = null

    private var camera1CoordinatesArea: Rect? = null

    private val cameraDeviceCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice?) {
            Log.d(TAG, "cameraDeviceCallback : onOpened")
            if (!isApiTimeout.get()) {
                cameraDevice = camera
            } else {
                camera?.close()
                cameraDevice = null
            }
        }

        override fun onDisconnected(camera: CameraDevice?) {
            Log.d(TAG, "cameraDeviceCallback : onDisconnected")
            cameraDevice = null
        }

        override fun onError(camera: CameraDevice?, error: Int) {
            Log.d(TAG, "cameraDeviceCallback : onError")
            cameraDevice = null
        }
    }

    private val sessionStateCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession?) {
            captureSession = null
        }

        override fun onConfigured(cameraCaptureSession: CameraCaptureSession?) {
            Log.d(TAG, "sessionStateCallback : onConfigured")
            captureSession = cameraCaptureSession

            previewReqBuilder?.set(CaptureRequest.CONTROL_AF_MODE,
                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)

            try {
                captureSession?.setRepeatingRequest(previewReqBuilder?.build(), /*object : CameraCaptureSession.CaptureCallback() {
                    override fun onCaptureCompleted(session: CameraCaptureSession?,
                                                    request: CaptureRequest?, result: TotalCaptureResult?) {
                        //Log.d(TAG, "123 : onCaptureCompleted")
                    }
                }*/ null, handler)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }

    private val captureSessionCallback = object : CameraCaptureSession.CaptureCallback() {
        override fun onCaptureCompleted(session: CameraCaptureSession?,
                                        request: CaptureRequest?, result: TotalCaptureResult?) {
            Log.d(TAG, "captureSessionCallback : onCaptureCompleted")
        }

        override fun onCaptureFailed(session: CameraCaptureSession?,
                                     request: CaptureRequest?, failure: CaptureFailure?) {
            Log.d(TAG, "captureSessionCallback : onCaptureFailed")
        }

        override fun onCaptureProgressed(session: CameraCaptureSession?,
                                         request: CaptureRequest?, partialResult: CaptureResult?) {
            Log.d(TAG, "captureSessionCallback : onCaptureProgressed")
        }
    }

    override fun isReady(): Boolean {
        return (cameraDevice != null)
    }

    override fun openCamera(callback: Callback<CameraDevice>) {
        CoroutineScope(Dispatchers.Default).launch {
            val cameraId = cameraManager.getBackFacingCameraId()
            if (cameraId != null) {
                isApiTimeout.set(false)
                cameraManager.getCameraInstance(cameraId, cameraDeviceCallback)
                withTimeout(API_TIMEOUT) {
                    while (!isReady()) {
                        delay(API_POLLING_DURATION)
                    }
                }
                cameraDevice?.let {
                    val characteristics = cameraManager.getCameraCharacteristics(it.id)
                    camera1CoordinatesArea = characteristics?.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)
                    callback(it, null)
                } ?: run {
                    isApiTimeout.set(true)
                    callback(null, Exception("Timeout exception of opening camera($cameraId)"))
                }
            } else {
                callback(null, Exception("Can not find camera id of back facing camera."))
            }
        }
    }

    override fun closeCamera() {
        previewReqBuilder = null
        cameraDevice?.close()
        cameraDevice = null
    }

    override fun getOutputSize(format: Int): Array<Size> {
        val characteristics = cameraDevice?.let {
            cameraManager.getCameraCharacteristics(it.id)
        } ?: run { null }

        val streamConfigMap = characteristics?.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        return streamConfigMap?.getOutputSizes(format) ?: arrayOf()
    }

    override fun setPreviewSize(size: Size?) {
        previewSize = size
    }

    override fun setPreviewSurface(surface: Surface?) {
        Log.d(TAG, "setPreviewSurface : surface = $surface")
        previewSurface = surface
    }

    override fun setCameraIndicatorView(view: CameraIndicatorView?) {
        cameraIndicatorView = view
    }

    override fun getCameraIndicatorView(): CameraIndicatorView? {
        return cameraIndicatorView
    }

    override fun startCaptureSession() {
        Log.d(TAG, "startCaptureSession")
        if (!isReady()) {
            Log.e(TAG, "createPreviewSession : camera is not READY!")
            return
        }

        if (previewSurface == null) {
            Log.e(TAG, "createPreviewSession : surface is not SET!")
            return
        }

        if (previewReqBuilder == null) {
            previewReqBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        }
        previewReqBuilder?.addTarget(previewSurface)

        try {
            cameraDevice?.createCaptureSession(listOf(previewSurface), sessionStateCallback, handler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    override fun focusOnTouch(point: Point, parent: Rect) {
        camera1CoordinatesArea?.let {
            val touchArea = calculateTapArea(point.x, point.y)
            autoFocus(calculateFocusArea(point, parent, it))

            cameraIndicatorView?.setHaveTouch(true, touchArea)
            cameraIndicatorView?.invalidate()

            handler.postDelayed({
                cameraIndicatorView?.setHaveTouch(false, null)
                cameraIndicatorView?.invalidate()
            }, 1000)
        }

    }

    private fun autoFocus(focusArea: Rect) {
        Log.d(TAG, "autoFocus")
        val focusRect = arrayOf(MeteringRectangle(focusArea, 1))
        previewReqBuilder?.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO)
        previewReqBuilder?.set(CaptureRequest.CONTROL_AF_REGIONS, focusRect)
        previewReqBuilder?.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START)

        try {
            captureSession?.capture(previewReqBuilder?.build(), captureSessionCallback, handler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }
}
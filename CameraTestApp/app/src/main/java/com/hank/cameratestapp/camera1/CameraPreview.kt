@file:Suppress("DEPRECATION")

package com.hank.cameratestapp.camera1

import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView


class CameraPreview(context: Context, private val camera: ICameraPresenter) :SurfaceView(context),
    SurfaceHolder.Callback {

    companion object {
        val TAG = CameraPreview::class.java.simpleName
    }

    init {
        holder?.apply {
            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            addCallback(this@CameraPreview)
            // deprecated setting, but required on Android versions prior to 3.0
            setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        } ?: run {
            Log.w(TAG, "init : holder is null")
        }
        camera.setSurfaceView(this)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        Log.d(TAG, "surfaceChanged : holder=$holder")
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        if (holder?.surface == null) {
            // preview surface does not exist
            Log.w(TAG, "surfaceChanged : holder is null")
            return
        }

        // stop preview before making changes
        camera.stopPreview()

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        camera.setAndStartPreview(holder)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        Log.d(TAG, "surfaceDestroyed : holder=$holder")
        camera.stopPreviewAndFreeCamera()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        Log.d(TAG, "surfaceCreated : holder=$holder")
        // The Surface has been created, now tell the camera where to draw the preview.
        holder?.let {
            camera.setAndStartPreview(it)
        } ?: run {
            Log.w(TAG, "surfaceCreated : holder is null")
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    val touchArea = calculateTapArea(event.x, event.y)
                    touchArea?.let { camera.focusOnTouch(it, Rect(left, top, right, bottom)) }
                }
                else -> { /* Do nothing */ }
            }
        } ?: run {
            Log.w(TAG, "onTouchEvent : event is null")
        }
        return true
    }

    private fun calculateTapArea(x: Float, y: Float): Rect? {
        val intX = x.toInt()
        val intY = y.toInt()
        return Rect((intX - 100), (intY - 100), (intX + 100), (intY + 100))
    }


}
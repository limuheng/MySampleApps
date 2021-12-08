@file:Suppress("DEPRECATION")

package com.hank.cameratestapp.camera2

import android.content.Context
import android.graphics.Rect
import android.hardware.Camera
import android.util.Log
import android.view.MotionEvent
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView

const val CAM_AREA_LEFT = -1000
const val CAM_AREA_TOP = -1000
const val CAM_AREA_RIGHT = 1000
const val CAM_AREA_BOTTOM = 1000
const val CAM_AREA_WIDTH = CAM_AREA_RIGHT - CAM_AREA_LEFT
const val CAM_AREA_HEIGHT = CAM_AREA_BOTTOM - CAM_AREA_TOP
const val CAM_POS_X_SHIFT = 1000
const val CAM_POS_Y_SHIFT = 1000

class Camera2Preview(context: Context) : SurfaceView(context) {

    companion object {
        val TAG = Camera2Preview::class.java.simpleName
    }

    interface TouchListener {
        fun onTouch(touchArea: Rect, focusArea: Rect)
    }

    private var touchListener: TouchListener? = null

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    val touchArea = calculateTapArea(event.x, event.y)
                    val focusArea = touchArea?.let {
                        calculateFocusArea(it)
                    }
                    focusArea?.let {
                        Log.d(TAG, "onTouchEvent : action=${event.action}, event [${event.x}, ${event.y}]")
                        Log.d(TAG, "onTouchEvent : CameraPreview [$left, $top, $right, $bottom]")
                        Log.d(TAG, "onTouchEvent : TouchArea [${touchArea.left}, ${touchArea.top}, ${touchArea.right}, ${touchArea.bottom}]")
                        Log.d(TAG, "onTouchEvent : FocusArea [${focusArea.left}, ${focusArea.top}, ${focusArea.right}, ${focusArea.bottom}]")
                        touchListener?.onTouch(touchArea, it)
                    }
                }
                else -> { /* Do nothing */ }
            }
        } ?: run {
            Log.w(TAG, "onTouchEvent : event is null")
        }
        return true
    }

    fun getSurface(): Surface? {
        return holder?.surface
    }

    fun setTouchListener(listener: TouchListener) {
        touchListener = listener
    }

    /**
     * Convert touch position x:y to [Camera.Area] position -1000:-1000 to 1000:1000.
     */
    private fun calculateTapArea(x: Float, y: Float): Rect? {
        val intX = x.toInt()
        val intY = y.toInt()
        return Rect((intX - 100), (intY - 100), (intX + 100), (intY + 100))
    }

    private fun calculateFocusArea(touchArea: Rect): Rect? {
        var left = touchArea.left * CAM_AREA_WIDTH / width - CAM_POS_X_SHIFT
        var top = touchArea.top * CAM_AREA_HEIGHT / height - CAM_POS_Y_SHIFT
        var right = touchArea.right * CAM_AREA_WIDTH / width - CAM_POS_X_SHIFT
        var bottom = touchArea.bottom * CAM_AREA_HEIGHT / height - CAM_POS_Y_SHIFT
        return Rect(
            if (left >= CAM_AREA_LEFT) left else CAM_AREA_LEFT,
            if (top >= CAM_AREA_TOP) top else CAM_AREA_TOP,
            if (right <= CAM_AREA_RIGHT) right else CAM_AREA_RIGHT,
            if (bottom <= CAM_AREA_BOTTOM) bottom else CAM_AREA_BOTTOM)
    }
}
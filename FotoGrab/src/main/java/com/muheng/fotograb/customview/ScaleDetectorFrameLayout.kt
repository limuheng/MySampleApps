package com.muheng.fotograb.customview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.FrameLayout
import com.muheng.rxjava.Event
import com.muheng.rxjava.RxEventBus

/**
 *  The ScaleGestureDetactor only act when the child view sets onTouchListener and return true
 */

class ScaleDetectorFrameLayout : FrameLayout {

    companion object {
        const val MIN_SCALE = 1.0f
        const val MAX_SCALE = 3.0f
    }

    private var mGestureDetector: ScaleGestureDetector? = null

    constructor(context: Context) : super(context) {
        mGestureDetector = ScaleGestureDetector(context, mScaleGestureListener)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mGestureDetector = ScaleGestureDetector(context, mScaleGestureListener)
    }

    private var mScaleFactor = MIN_SCALE
    private var mScaleGestureListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector?): Boolean {

            mScaleFactor = mScaleFactor.times(detector?.scaleFactor ?: 1.0f)
            mScaleFactor = Math.max(MIN_SCALE, Math.min(mScaleFactor, MAX_SCALE))

            RxEventBus.get()?.send(Event.ScaleEvent(mScaleFactor))

            return true
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        mGestureDetector?.onTouchEvent(ev)
        return super.onInterceptTouchEvent(ev)
    }
}
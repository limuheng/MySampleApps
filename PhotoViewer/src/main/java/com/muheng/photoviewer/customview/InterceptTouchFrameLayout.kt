package com.muheng.photoviewer.customview

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.FrameLayout

class InterceptTouchFrameLayout : FrameLayout {
    var mGestureDetector: GestureDetector? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        mGestureDetector?.onTouchEvent(ev);
        return false;
    }
}
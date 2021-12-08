package com.hank.cameratestapp.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.hank.cameratestapp.R
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class CameraIndicatorView : View {

    private var touchArea: Rect? = null
    private var touchAreaPaint: Paint = Paint()
    private var haveTouch: Boolean = false

    private val locker = ReentrantLock()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        setupTouchAreaPaint()
    }

    private fun setupTouchAreaPaint() {
        touchAreaPaint.color = ContextCompat.getColor(context, R.color.ivory)
        touchAreaPaint.style = Paint.Style.STROKE
        touchAreaPaint.strokeWidth = 2.0f
    }

    override fun onDraw(canvas: Canvas?) {
        if (haveTouch) {
            canvas?.drawRect(touchArea, touchAreaPaint)
        }
    }

    fun setHaveTouch(haveTouch: Boolean, area: Rect?) {
        locker.withLock {
            this.haveTouch = haveTouch
            touchArea = area
        }
    }
}
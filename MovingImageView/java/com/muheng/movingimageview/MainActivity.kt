package com.muheng.movingimageview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView

    private var touchListener = object : View.OnTouchListener {
        private var lastX = 0f
        private var lastY = 0f
        private var draggingId = MotionEvent.INVALID_POINTER_ID
        override fun onTouch(view: View?, event: MotionEvent?): Boolean {
            when (event?.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if (event.pointerCount == 1) {
                        draggingId = event.actionIndex
                        lastX = event.getX(event.actionIndex)
                        lastY = event.getY(event.actionIndex)
                        Log.d("11111", "ACTION_DOWN, draggingId:$draggingId, lastX:$lastX, lastY:$lastY")
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (draggingId == event.actionIndex) {
                        var dx = (event.getX(event.actionIndex) - lastX)
                        var dy = (event.getY(event.actionIndex) - lastY)

                        imageView.x = imageView.x.plus(dx)
                        imageView.y = imageView.y.plus(dy)
                        Log.d("11111", "ACTION_MOVE, draggingId:$draggingId, lastX:$lastX, lastY:$lastY, dx:$dx, dy:$dy")

                        // Do not remember new x/y to lastX/Y
                        //lastX = event.getX(event.actionIndex)
                        //lastY = event.getY(event.actionIndex)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (draggingId == event.getPointerId(event.actionIndex)) {
                        draggingId = MotionEvent.INVALID_POINTER_ID
                        Log.d("11111", "ACTION_UP, draggingId:$draggingId")
                    }
                }
            }
            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.image)
        imageView.setOnTouchListener(touchListener)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        //Log.d("11111", "LM:${layoutParams.leftMargin}, TM:${layoutParams.topMargin}, RM:${layoutParams.rightMargin}, BM:${layoutParams.bottomMargin}")
        //Log.d("11111", "imageView: (${imageView.x}, ${imageView.y})")
        //Log.d("11111", "imageView size: (${imageView.width}, ${imageView.height})")
        //Log.d("11111", "window size: (${window.decorView.width}, ${window.decorView.height})")
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                imageView.y = imageView.y.minus(3)
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                imageView.y = imageView.y.plus(3)
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                imageView.x = imageView.x.minus(3)
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                imageView.x = imageView.x.plus(3)
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}

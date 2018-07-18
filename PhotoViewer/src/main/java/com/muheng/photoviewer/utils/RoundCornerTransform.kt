package com.muheng.photoviewer.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import android.graphics.RectF
import android.graphics.BitmapShader
import android.graphics.Bitmap


/**
 * Refs: https://github.com/wildma/GlideRoundImage/blob/master/GlideRoundImage.java
 */
class RoundCornerTransform : BitmapTransformation {

    companion object {
        fun roundCornerCrop(pool: BitmapPool?, source: Bitmap?, radius: Float): Bitmap? {
            if (source == null) return null

            var squared = Bitmap.createBitmap(source, 0, 0, source.width, source.height)
            var result = pool?.get(source.width, source.height, Bitmap.Config.ARGB_8888)

            if (result == null) {
                result = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
            }

            val canvas = Canvas(result)
            val paint = Paint()
            paint.shader = BitmapShader(squared, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            paint.isAntiAlias = true

            // There's problem to render round corner rect
            val rectF = RectF(0f, 0f, source.width as Float, source.height as Float)
            canvas.drawRoundRect(rectF, radius, radius, paint)

            //val rect = Rect(0, 0, source.width, source.height)
            //canvas.drawRect(rect, paint)

            return result
        }
    }

    private var mRadius: Float = 0.0f

    constructor(context: Context?) : super(context) {
        RoundCornerTransform(context, 6);
    }

    constructor(context: Context?, dp: Int) : super(context) {
        mRadius = Resources.getSystem().displayMetrics.density * dp;
    }

    override fun getId(): String {
        return javaClass.name
    }

    override fun transform(pool: BitmapPool?, toTransform: Bitmap?, outWidth: Int, outHeight: Int): Bitmap? {
        return roundCornerCrop(pool, toTransform, mRadius)
        // Below is for Glide 4.0.0
        //Bitmap bitmap = TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight);
        //return roundCrop(pool, bitmap);
    }
}
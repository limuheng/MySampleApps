package com.muheng.photoviewer.utils

import android.content.Context
import android.graphics.*
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation

/**
 * Ref: http://www.gadgetsaint.com/android/circular-images-glide-library-android/#.WwPOYkiFOUk
 */
class CircleTransform(context: Context?) : BitmapTransformation(context) {

    companion object {
        fun circleCrop(pool: BitmapPool?, source: Bitmap?): Bitmap? {
            if (source == null) return null

            var size = Math.min(source.width, source.height)
            var x = (source.width - size) / 2
            var y = (source.height - size) / 2

            var squared = Bitmap.createBitmap(source, x, y, size, size)
            var result = pool?.get(size, size, Bitmap.Config.ARGB_8888)

            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            }

            var canvas = Canvas(result)
            var paint = Paint()

            paint.shader = BitmapShader(squared, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            paint.isAntiAlias = true

            val radius = size / 2f
            canvas.drawCircle(radius, radius, radius, paint)

            return result
        }
    }

    override fun getId(): String {
        return javaClass.name
    }

    override fun transform(pool: BitmapPool?, toTransform: Bitmap?, outWidth: Int, outHeight: Int): Bitmap? {
        return circleCrop(pool, toTransform)
    }

}
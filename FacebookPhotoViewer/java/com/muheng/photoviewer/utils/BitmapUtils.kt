package com.muheng.photoviewer.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File

class BitmapUtils {
    companion object {
        val TAG = "BitmapUtils"

        @Throws(Exception::class)
        fun decodeImage(uri : Uri, smapleSize : Int) : Bitmap {
            var options = BitmapFactory.Options()
            options.inSampleSize = smapleSize
            return BitmapFactory.decodeFile(File(uri.toString()).path, options)
        }
    }
}
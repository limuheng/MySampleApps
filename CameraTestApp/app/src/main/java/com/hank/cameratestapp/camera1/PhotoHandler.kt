@file:Suppress("DEPRECATION")

package com.hank.cameratestapp.camera1

import android.hardware.Camera
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import com.hank.cameratestapp.utils.Callback
import java.io.File
import java.io.FileOutputStream

class PhotoHandler(private val callback: Callback<Boolean>): Camera.PictureCallback {

    companion object {
        val TAG = PhotoHandler::class.java.simpleName
    }

    override fun onPictureTaken(data: ByteArray?, camera: Camera?) {
        try {
            PictureTakenAsyncTask(data, callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    internal inner class PictureTakenAsyncTask(private val data: ByteArray?, private val callback: Callback<Boolean>)
        : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void): Boolean? {
            if (data == null) {
                return false
            }

            val pictureDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "HankCamera")

            if (!pictureDir.exists() && !pictureDir.mkdir()) {
                Log.e(TAG, "PictureTakenAsyncTask : Error while creating picture directory")
                return false
            }

            val pictureFile = File(pictureDir, System.currentTimeMillis().toString() + ".jpg")

            try {
                if (pictureFile.exists()) {
                    pictureFile.delete()
                }
                val fos = FileOutputStream(pictureFile)
                fos.write(data)
                fos.close()
                Log.d(TAG, "New Image saved:${pictureFile.name}, image size = ${data.size / 1024} KB")
            } catch (error: Exception) {
                Log.d(TAG, "Image could not be saved.")
                return false
            }

            //correctPhotoOrientation(fileFullPath)
            return true
        }

        override fun onPostExecute(result: Boolean?) {
            result?.takeIf { true }?.run {
                callback(result, null)
            }
        }
    }
}
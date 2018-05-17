package com.muheng.photoviewer

import com.hank.hellonews.okhttp.OkHttpUtils
import java.io.File
import java.io.FileOutputStream

class DownloadUtils {
    companion object {
        const val PHOTO_CACHE_DIR = "photos"
        var sFilesDir: File? = null

        // Synchronous call
        fun downloadPhoto(fileName : String?, url: String?) : String {
            var fullFileName : String = ""
            if (url == null || url.isEmpty()) {
                return fullFileName
            }
            var response = OkHttpUtils.requestSyncGet(url)
            if (response.isSuccessful) {
                var photoCacheDir = File(sFilesDir, PHOTO_CACHE_DIR)
                if (!photoCacheDir.exists()) {
                    if (photoCacheDir.mkdir()) {
                        return fullFileName
                    }
                }
                //Log.d("DownloadUtils", "photoCacheDir: " + photoCacheDir.path)
                var file = File(photoCacheDir, fileName)
                fullFileName = file.absolutePath
                //Log.d("DownloadUtils", "photo absolutePath: " + fullFileName)
                var fos: FileOutputStream? = null
                try {
                    //if (!file.exists()) {
                        fos = FileOutputStream(file)
                        fos.write(response.body()?.bytes());
                        //Log.d("DownloadUtils", "photo saved successfully!")
                    //}
                } catch (e: Exception) {
                    fullFileName = ""
                    e.printStackTrace()
                } finally {
                    try {
                        fos?.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            return fullFileName
        }
    }
}
package com.muheng.fotograb

import android.util.Log
import com.hank.hellonews.okhttp.OkHttpUtils
import com.muheng.rxjava.Event
import com.muheng.rxjava.RxEventBus
import java.io.File
import java.io.FileOutputStream

class DownloadUtils {
    companion object {
        const val TAG = "DownloadUtils"
        const val PHOTO_DIR = "photos"

        var sCachedFilesDir: File? = null

        const val PHOTO_VIEWER_DIR = "PhotoViewerDownload"
        var sDownloadFilesDir: File? = null

        fun extractPhotoName(url: String?): String? {
            var result: String? = url?.substring(0, url.indexOf("?"))
            result = result?.substring(result.lastIndexOf('/') + 1)
            Log.d(TAG, "Photo file Name: $result")
            return result
        }

        fun downloadPhoto(fileName: String?, url: String?): String? {
            var fullFileName: String? = null
            if (url == null || url.isEmpty()) {
                return fullFileName
            }
            var response = OkHttpUtils.requestSyncGet(url)
            if (response.isSuccessful) {
                var photoCacheDir = File(sDownloadFilesDir, PHOTO_VIEWER_DIR)
                if (!photoCacheDir.exists()) {
                    if (photoCacheDir.mkdir()) {
                        return fullFileName
                    }
                }
                //Log.d("DownloadUtils", "photoCacheDir: " + photoCacheDir.path)
                var file = File(photoCacheDir, fileName)
                //Log.d("DownloadUtils", "photo absolutePath: " + fullFileName)
                var fos: FileOutputStream? = null
                try {
                    //if (!file.exists()) {
                    fos = FileOutputStream(file)
                    fos.write(response.body()?.bytes());
                    fullFileName = file.absolutePath
                    //Log.d("DownloadUtils", "photo saved successfully!")
                    //}
                    // Download completed
                    RxEventBus.get()?.send(Event.DownloadCompleted(fullFileName))
                } catch (e: Exception) {
                    RxEventBus.get()?.send(Event.DownloadFailed(e.message))
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
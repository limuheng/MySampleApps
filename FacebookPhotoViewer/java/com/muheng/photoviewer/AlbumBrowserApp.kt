package com.muheng.photoviewer

import android.app.Application

class AlbumBrowserApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DownloadUtils.sFilesDir = this@AlbumBrowserApp.filesDir
    }
}
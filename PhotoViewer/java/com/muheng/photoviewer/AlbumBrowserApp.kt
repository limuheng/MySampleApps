package com.muheng.photoviewer

import android.app.Application
import com.muheng.photoviewer.utils.Constants
import com.muheng.photoviewer.utils.DimenUtils
import com.muheng.photoviewer.utils.RuntimeUtils

class AlbumBrowserApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DownloadUtils.sFilesDir = this@AlbumBrowserApp.filesDir
    }

    fun getStatusBarHeightDp() : Int {
        var  resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android")
        var statusBarHeight : Int = 0
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        }
        return DimenUtils.pxToDp(statusBarHeight)
    }

    fun getToolBarHeightDp() : Int {
        val styledAttributes = theme.obtainStyledAttributes(
                intArrayOf(android.R.attr.actionBarSize)
        )
        var actionBarHeight = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()
        return DimenUtils.pxToDp(actionBarHeight)
    }
}
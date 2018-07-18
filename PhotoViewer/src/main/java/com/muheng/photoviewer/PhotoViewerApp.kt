package com.muheng.photoviewer

import android.app.Application
import android.os.Environment
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.muheng.jni.JniAdMob
import com.muheng.photoviewer.utils.Constants
import com.muheng.photoviewer.utils.DimenUtils

class PhotoViewerApp : Application() {

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("photoviewer_lib")
        }
    }

    private lateinit var mJniAdMob: JniAdMob

    override fun onCreate() {
        super.onCreate()

        // Initialize download path & directory
        DownloadUtils.sCachedFilesDir = this@PhotoViewerApp.filesDir
        DownloadUtils.sDownloadFilesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        // Initialize google admob
        mJniAdMob = JniAdMob()
        MobileAds.initialize(this, mJniAdMob.getAppId()) // resources.getString(R.string.test_admob_app_id
    }

    fun getJniAdMob(): JniAdMob {
        if (mJniAdMob == null) {
            mJniAdMob = JniAdMob()
        }
        return mJniAdMob
    }

    fun createAdView(unitId: String): AdView {
        var adView = AdView(this)
        adView.adSize = AdSize.SMART_BANNER
        adView.adUnitId = unitId //resources.getString(R.string.test_ad_unit_id)

        val adRequest = AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build()
        adView.loadAd(adRequest)

        return adView
    }

//    fun getStatusBarHeightDp(): Int {
//        var resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android")
//        var statusBarHeight: Int = 0
//        if (resourceId > 0) {
//            statusBarHeight = resources.getDimensionPixelSize(resourceId)
//        }
//        return DimenUtils.pxToDp(statusBarHeight)
//    }
//
//    fun getToolBarHeightDp(): Int {
//        val styledAttributes = theme.obtainStyledAttributes(
//                intArrayOf(android.R.attr.actionBarSize)
//        )
//        var actionBarHeight = styledAttributes.getDimension(0, 0f).toInt()
//        styledAttributes.recycle()
//        return DimenUtils.pxToDp(actionBarHeight)
//    }
}
package com.muheng.jni

import android.util.Log

/**
 * Created by Muheng Li on 2018/7/17.
 */
class JniAdMob {

    private external fun createNativeObject(): Long
    private external fun getAdmobAppId(address: Long): String
    private external fun getTestAdId(address: Long): String

    var mNativeAdMob: Long = 0L

    init {
        mNativeAdMob = createNativeObject()
    }

    fun getAppId(): String {
        return getAdmobAppId(mNativeAdMob)
    }

    fun getTestAdId(): String {
        return getTestAdId(mNativeAdMob)
    }
}
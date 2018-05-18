package com.muheng.facebook

import android.net.Uri

class MeProfile {
    var mId : String = ""
    var mName : String = ""
    var mLink : String = ""
    var mCoverUrl: String = ""
    var mCoverUri : Uri? = null
    var mPictureUrl: String = ""
    var mPictureUri : Uri? = null

    fun logout() {
        mId = ""
        mName = ""
        mLink = ""
        mCoverUrl = ""
        mCoverUri = null
        mPictureUrl = ""
        mPictureUri = null
    }
}
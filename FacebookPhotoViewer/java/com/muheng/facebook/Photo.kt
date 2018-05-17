package com.muheng.facebook

import android.net.Uri

class Photo {
    var mId : String? = null
    var mName : String? = null
    var mLink : String? = null   // Facebook link
    var mPicture : String? = null // Small preview photo
    var mCreatedTime : String? = null
    var mWidth : Int = 0
    var mHeight : Int = 0
    var mSource : String? = null // Photo url

    var mPictureCache : Uri? = null
    var mSourceCache: Uri? = null
}
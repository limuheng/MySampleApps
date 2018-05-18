package com.muheng.photoviewer.manager

import android.os.Bundle
import android.os.Handler
import com.facebook.AccessToken
import com.facebook.AccessTokenTracker
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.muheng.facebook.MeProfile
import com.muheng.photoviewer.utils.Constants
import org.json.JSONObject

class FacebookManager private constructor() {

    companion object {
        private var sInstanct : FacebookManager? = null

        fun getInstance() : FacebookManager? {
            if (sInstanct == null) {
                sInstanct = FacebookManager()
            }
            return sInstanct
        }

        fun createGraphRequest(bundle : Bundle,
                               callback : GraphRequest.GraphJSONObjectCallback) : GraphRequest {
            var request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), callback)
            request.parameters = bundle
            return request
        }

        fun createGraphPathRequest(bundle : Bundle, path : String,
                               callback : GraphRequest.Callback) : GraphRequest {
            var request = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), path, callback)
            request.parameters = bundle
            return request
        }

        fun extractFBPhotoName(url : String?) : String? {
            var result : String? = url?.substring(0, url.indexOf(".jpg") + 4)
            result = result?.substring(result.lastIndexOf('/'))
            return result
        }
    }

    var mHandler : Handler? = null
    var mMeProfile : MeProfile = MeProfile()
    private var mAccessTokenTracker : AccessTokenTracker

    init {
        mAccessTokenTracker = object : AccessTokenTracker() {
            override fun onCurrentAccessTokenChanged(oldAccessToken: AccessToken?, currentAccessToken: AccessToken?) {
                // User logged out
                if (currentAccessToken == null) {
                    mMeProfile.logout()
                    // Notify UI to change
                    mHandler?.sendEmptyMessage(Constants.MSG_UPDATE_UI)
                }
            }
        }
    }

    fun loginSuccess(obj: JSONObject?) {
        if (obj?.has(Constants.ID) == true) {
            mMeProfile.mId = obj.getString(Constants.ID) ?: ""
        }
        if (obj?.has(Constants.NAME) == true) {
            mMeProfile.mName = obj.getString(Constants.NAME) ?: ""
        }
        if (obj?.has(Constants.LINK) == true) {
            mMeProfile.mLink = obj.getString(Constants.LINK) ?: ""
        }

        if (obj?.has(Constants.COVER) == true) {
            var cover = obj.getJSONObject(Constants.COVER)
            if (obj.has(Constants.SOURCE)) {
                mMeProfile.mCoverUrl = cover?.getString(Constants.SOURCE) ?: ""
            }
        }

        if (obj?.has(Constants.PICTURE) == true) {
            var picture = obj.getJSONObject(Constants.PICTURE)
            if (obj.has(Constants.DATA)) {
                var data = picture?.getJSONObject(Constants.DATA)
                mMeProfile.mPictureUrl = data?.getString(Constants.URL) ?: ""
            }
        }

//        var thread = Thread(object: Runnable {
//            override fun run() {
//                var coverFile = DownloadUtils.downloadPhoto("me_cover.jpg", mMeProfile.mCoverUrl)
//                mMeProfile.mCoverUri = Uri.parse(coverFile)
//                //Log.d("123", "Download: " + coverFile)
//
//                var pictureFile = DownloadUtils.downloadPhoto("me_picture.jpg", mMeProfile.mPictureUrl)
//                mMeProfile.mPictureUri = Uri.parse(pictureFile)
//                //Log.d("123", "Download: " + pictureFile.toString())
//
//                mHandler?.sendEmptyMessage(Constants.MSG_UPDATE_UI)
//            }
//        })
//        thread.start()
    }

    fun isLoggedIn() : Boolean {
        var accessToken : AccessToken? = AccessToken.getCurrentAccessToken()
        return !(accessToken == null || accessToken.permissions.isEmpty())
    }

    fun logout() {
        LoginManager.getInstance().logOut()
    }
}
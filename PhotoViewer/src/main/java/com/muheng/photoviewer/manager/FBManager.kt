package com.muheng.photoviewer.manager

import com.facebook.AccessToken
import com.muheng.facebook.MeProfile

class FBManager private constructor() {

    companion object {
        private var sInstance: FBManager? = null

        fun get(): FBManager? {
            if (sInstance == null) {
                sInstance = FBManager()
            }
            return sInstance
        }

        fun isLoggedIn(): Boolean {
            var accessToken: AccessToken? = AccessToken.getCurrentAccessToken()
            return !(accessToken == null || accessToken.permissions.isEmpty())
        }
    }

    var mUser: MeProfile? = null
}
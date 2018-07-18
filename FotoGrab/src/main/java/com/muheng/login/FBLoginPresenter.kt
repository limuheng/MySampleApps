package com.muheng.login

import com.muheng.facebook.FBManager
import java.lang.ref.WeakReference

/**
 * Created by  Muheng Li on 2018/7/7.
 */
class FBLoginPresenter(loginView: IFBLoginView) {

    private val mLoginView = WeakReference<IFBLoginView>(loginView)

    fun performLoginCheck() {
        mLoginView.get()?.onLoginCheck(FBManager.isLoggedIn())
    }

}
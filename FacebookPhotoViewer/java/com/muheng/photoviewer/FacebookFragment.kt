package com.muheng.photoviewer

import android.support.v4.app.Fragment
import com.muheng.photoviewer.utils.FacebookManager
import com.muheng.photoviewer.utils.UIHandler

abstract class FacebookFragment : Fragment(), UIHandler.Companion.IUIHandler {
    protected fun isLoggedIn() : Boolean {
        return FacebookManager.getInstance()?.isLoggedIn() == true
    }
    abstract protected fun showProgress(idx : Int = 0)
    abstract protected fun hideProgress(idx : Int = 0)
    abstract protected fun updateUI()
    abstract fun executeRequest()
    abstract fun onBackPressed() : Boolean
    abstract fun onVisible()
}
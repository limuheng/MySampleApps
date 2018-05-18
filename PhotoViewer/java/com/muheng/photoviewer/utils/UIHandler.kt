package com.muheng.photoviewer.utils

import android.os.Handler
import android.os.Message
import java.lang.ref.WeakReference

class UIHandler(iUIHandler : IUIHandler) : Handler() {
    companion object {
        interface IUIHandler {
            fun handleMessage(msg : Message?)
        }
    }

    val mIUIHandler : WeakReference<IUIHandler>

    init {
        mIUIHandler = WeakReference<IUIHandler> (iUIHandler)
    }

    override fun handleMessage(msg: Message?) {
        var uiHandler = mIUIHandler.get()
        uiHandler?.handleMessage(msg)
    }
}
package com.muheng.photoviewer.utils

import android.content.Context
import android.net.ConnectivityManager



class NetworkCheckUtils {
    companion object {
        fun isConnected(context : Context?) : Boolean {
            val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }
    }
}
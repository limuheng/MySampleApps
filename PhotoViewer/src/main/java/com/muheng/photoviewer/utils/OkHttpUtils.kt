package com.hank.hellonews.okhttp

import android.util.Log
import okhttp3.*
import java.io.IOException

class OkHttpUtils {
    companion object {
        const val TAG: String = "OkHttpUtils"

        private var sOkClient = OkHttpClient()

//        fun requestGet(urlStr: String?, callback : Callback) {
//            if (urlStr != null) {
//                val request = Request.Builder().url(urlStr).build()
//
//                val call = sOkClient.newCall(request)
//                call.enqueue(callback)
//            }
//        }

        // Synchronized http call
        fun requestSyncGet(urlStr: String): Response {
            val request = Request.Builder().url(urlStr).build()
            return sOkClient.newCall(request).execute()
        }

//        fun requestPut(urlStr: String, reqBody: RequestBody) {
//            val request = Request.Builder().url(urlStr).put(reqBody).build()
//
//            val call = sOkClient.newCall(request)
//            call.enqueue(sCallback)
//        }
    }
}
package com.muheng.photoviewer.manager

import android.os.Handler
import com.hank.hellonews.okhttp.OkHttpUtils
import com.muheng.photoviewer.utils.Constants
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.net.URLDecoder

abstract class PageManager<T> : PhotoManager<T>() {

    // TODO implement preloading prev & next data
    //var mCachedDataPrev = ArrayList<T> ()
    //var mCachedDataNext = ArrayList<T> ()

    override fun loadNext(callback: ICallback<T>?, handler : Handler?) {
        if (hasNext() && !mLoadingUrl.equals(mNext)) {
            // To disable item clicker listener
            mLoading = true
            handler?.sendEmptyMessage(Constants.MSG_LOADING)
            mLoadingUrl = mNext

            var httpCallback : Callback = object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    var decodedResponse = URLDecoder.decode(response.body()?.string(), "UTF-8");
                    var data = JSONObject(decodedResponse)
                    var list = parsingData(data, handler)
                    mCachedData.clear()
                    mCachedData.addAll(list)
                    // To enable item clicker listener
                    mLoading = false
                    callback?.onSuccess(true, list)
                }
            }

            OkHttpUtils.requestGet(mLoadingUrl, httpCallback)
        }
    }

    override fun loadPrev(callback: ICallback<T>?, handler : Handler?) {
        if (hasPrev() && !mLoadingUrl.equals(mPrev)) {
            // To disable item clicker listener
            mLoading = true
            handler?.sendEmptyMessage(Constants.MSG_LOADING)
            mLoadingUrl = mPrev

            var httpCallback : Callback = object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    var decodedResponse = URLDecoder.decode(response.body()?.string(), "UTF-8");
                    var data = JSONObject(decodedResponse)
                    var list = parsingData(data, handler)
                    mCachedData.clear()
                    mCachedData.addAll(list)
                    // To enable item clicker listener
                    mLoading = false
                    callback?.onSuccess(true, list)
                }
            }

            OkHttpUtils.requestGet(mLoadingUrl, httpCallback)
        }
    }
}
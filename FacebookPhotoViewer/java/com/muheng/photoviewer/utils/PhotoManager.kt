package com.muheng.photoviewer.utils

import android.os.Handler
import android.util.Log
import com.hank.hellonews.okhttp.OkHttpUtils
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.net.URLDecoder

abstract class PhotoManager<T> {

    interface ICallback<T> {
        fun onSuccess(isNext : Boolean, list : List<T>)
        fun onFailed(isNext : Boolean, error : String)
    }

    var mCurrDataIdx : Int = -1
    var mCachedData = ArrayList<T> ()

    var mLoadingUrl : String = ""
    var mPrev : String = ""
    var mNext : String = ""

    var mLoading : Boolean = false

    fun getItem(pos: Int, columnCount : Int = 1) : List<T> {
        var items = ArrayList<T>()
        for (i in 0..columnCount.minus(1)) {
            var idx = (pos * columnCount + i)
            if (idx >= 0 && idx < mCachedData.size) {
                items.add(mCachedData[idx])
            }
        }
        return items
    }

    fun appendData(listToAdd : ArrayList<T>?) {
        mCachedData.addAll(listToAdd as ArrayList<T>)
    }

    protected fun hasNext() : Boolean {
        return (!mNext.isEmpty())
    }

    fun loadNext(callback: ICallback<T>?, handler : Handler?) {
        //Log.d("PhotoManager", "loadNext, mNext: " + mNext)
        //Log.d("PhotoManager", "loadNext, mLoadingUrl: " + mLoadingUrl)
        if (hasNext() && !mLoadingUrl.equals(mNext)) {
            // To disable item clicker listener
            mLoading = true
            handler?.sendEmptyMessage(Constants.MSG_LOAD_NEXT)
            mLoadingUrl = mNext

            var httpCallback : Callback = object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    var decodedResponse = URLDecoder.decode(response.body()?.string(), "UTF-8");
                    var data = JSONObject(decodedResponse)
                    var list = parsingData(data, handler)
                    mCachedData.addAll(list)
                    // To enable item clicker listener
                    mLoading = false
                    callback?.onSuccess(true, list)
                }
            }

            OkHttpUtils.requestGet(mLoadingUrl, httpCallback)
        }
    }

    abstract fun parsingData(jsonObj: JSONObject?, handler : Handler?) : ArrayList<T>

    abstract fun getData(id : String) : T?

    abstract fun calibrateCurrIdx(id : String?)
    abstract fun getNextData() : T?
    abstract fun getPrevData() : T?
    abstract fun goNextData() : T?
    abstract fun goPrevData() : T?
}
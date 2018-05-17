package com.muheng.photoviewer.utils

import android.os.Handler
import android.util.Log
import com.muheng.facebook.Photo
import org.json.JSONObject

class PhotosManager : PhotoManager<Photo>() {

    companion object {
        val TAG : String = "PhotosManager"

        private var sInstanct: PhotosManager? = null

        fun getInstance(): PhotosManager? {
            if (sInstanct == null) {
                sInstanct = PhotosManager()
            }
            return sInstanct
        }

        fun release() {
            sInstanct = null
        }
    }

    override fun parsingData(jsonObj: JSONObject?, handler : Handler?) : ArrayList<Photo> {
        var list = ArrayList<Photo> ()

        //Log.d(TAG, jsonObj?.toString())
        var photosArray = jsonObj?.getJSONArray(Constants.DATA)
        if (photosArray != null) {
            list.clear()
            for (i in 0..photosArray.length().minus(1)) {
                try {
                    var photo = Photo()
                    var photoJson = photosArray.getJSONObject(i)
                    if (photoJson.has(Constants.ID)) {
                        photo.mId = photoJson.getString(Constants.ID)
                    }
                    if (photoJson.has(Constants.NAME)) {
                        photo.mName = photoJson.getString(Constants.NAME)
                    }
                    if (photoJson.has(Constants.PICTURE)) {
                        photo.mPicture = photoJson.getString(Constants.PICTURE)
                    }
                    if (photoJson.has(Constants.WEB_IMAGES)) {
                        var webImages = photoJson.getJSONArray(Constants.WEB_IMAGES)
                        var firstImg = webImages.getJSONObject(0)
                        if (firstImg.has(Constants.SOURCE)) {
                            photo.mSource = firstImg.getString(Constants.SOURCE)
                        }
                    }

                    list.add(photo)
                } catch (e : Exception) {
                    e.printStackTrace()
                }
            }
        }
        var paging = jsonObj?.getJSONObject(Constants.PAGING)
        if (paging != null) {
            try {
                sInstanct?.mPrev = paging.getString(Constants.PREVIOUS)
            } catch (e : Exception) {}
            try {
                sInstanct?.mNext = paging.getString(Constants.NEXT)
            } catch (e : Exception) {}
        }
//
//        var picThread = Thread(object: Runnable {
//            override fun run() {
//                var size = list.size
//                for (i in 0..size.minus(1)) {
//                    var photo = list.get(i)
//
//                    var pictureUrl = photo.mPicture
//                    var pictureFile = DownloadUtils.downloadPhoto(FacebookManager.extractFBPhotoName(pictureUrl), pictureUrl)
//                    photo.mPictureCache = Uri.parse(pictureFile)
//
//                    handler?.sendEmptyMessage(Constants.MSG_UPDATE_UI)
//                }
//            }
//        })
//        picThread.start()

//        var srcThread = Thread(object: Runnable {
//            override fun run() {
//                var size = list.size
//                for (i in 0..size.minus(1)) {
//                    var photo = list.get(i)
//
//                    var sourceUrl = photo.mSource
//                    var sourceFile = DownloadUtils.downloadPhoto(FacebookManager.extractFBPhotoName(sourceUrl), sourceUrl)
//                    photo?.mSourceCache = Uri.parse(sourceFile)
//
//                    handler?.sendEmptyMessage(Constants.MSG_UPDATE_UI)
//                }
//            }
//        })
//        srcThread.start()
        return list
    }

    override fun getData(id : String) : Photo? {
        if (mCurrDataIdx == -1) {
            calibrateCurrIdx(id)
        }
        return mCachedData[mCurrDataIdx]
    }

    override fun calibrateCurrIdx(id: String?) {
        if (id != null) {
            for (i in 0..mCachedData.size.minus(1)) {
                if (id == mCachedData[i].mId) {
                    mCurrDataIdx = i
                    return
                }
            }
            Log.e(TAG, "Unable to match the id passed in: " + id)
        }
        mCurrDataIdx = 0
    }

    override fun getNextData(): Photo? {
        var nextIdx = mCurrDataIdx + 1
        if (nextIdx < mCachedData.size) {
            return mCachedData[nextIdx]
        }
        return null
    }

    override fun getPrevData(): Photo? {
        var prevIdx = mCurrDataIdx - 1
        if (prevIdx >= 0) {
            return mCachedData[prevIdx]
        }
        return null
    }

    override fun goNextData(): Photo? {
        var photo = getNextData()
        if (photo != null) {
            mCurrDataIdx++
        }
        return photo
    }

    override fun goPrevData(): Photo? {
        var photo = getPrevData()
        if (photo != null) {
            mCurrDataIdx--
        }
        return photo
    }
}
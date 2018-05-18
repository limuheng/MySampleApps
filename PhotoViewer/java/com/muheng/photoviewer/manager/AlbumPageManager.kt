package com.muheng.photoviewer.manager

import android.os.Handler
import android.util.Log
import com.muheng.facebook.Album
import com.muheng.facebook.Photo
import com.muheng.photoviewer.utils.Constants
import org.json.JSONObject

class AlbumPageManager : PageManager<Album>() {
    companion object {
        val TAG : String = "AlbumPageManager"

        private var sInstanct: AlbumPageManager? = null

        fun getInstance(): AlbumPageManager? {
            if (sInstanct == null) {
                sInstanct = AlbumPageManager()
            }
            return sInstanct
        }

        fun release() {
            sInstanct = null
        }
    }

    override fun parsingData(jsonObj: JSONObject?, handler: Handler?): ArrayList<Album> {
        var list = ArrayList<Album> ()

        Log.d(TAG, jsonObj?.toString())
        var albumsArray = jsonObj?.getJSONArray(Constants.DATA)
        if (albumsArray != null) {
            list.clear()
            for (i in 0..albumsArray.length().minus(1)) {
                var album : Album = Album()
                var albumJson = albumsArray.getJSONObject(i)
                if (albumJson.has(Constants.ID)) {
                    album.mId = albumJson.getString(Constants.ID)
                }
                if (albumJson.has(Constants.NAME)) {
                    album.mName = albumJson.getString(Constants.NAME)
                }
                if (albumJson.has(Constants.COVER_PHOTO)) {
                    var coverPhoto = albumJson.getJSONObject(Constants.COVER_PHOTO)
                    album.mCoverPhoto = Photo()
                    if (coverPhoto.has(Constants.ID)) {
                        album.mCoverPhoto?.mId = coverPhoto.getString(Constants.ID)
                    }
                    if (coverPhoto.has(Constants.NAME)) {
                        album.mCoverPhoto?.mName = coverPhoto.getString(Constants.NAME)
                    }
                    if (coverPhoto.has(Constants.PICTURE)) {
                        album.mCoverPhoto?.mPicture = coverPhoto.getString(Constants.PICTURE)
                    }
                    if (coverPhoto.has(Constants.SOURCE)) {
                        album.mCoverPhoto?.mSource = coverPhoto.getString(Constants.SOURCE)
                    }
                }
                list.add(album)
            }
        }
        var paging = jsonObj?.getJSONObject(Constants.PAGING)
        if (paging != null) {
            if (paging.has(Constants.PREVIOUS)) {
                sInstanct?.mPrev = paging.getString(Constants.PREVIOUS)
            }

            if (paging.has(Constants.NEXT)) {
                sInstanct?.mNext = paging.getString(Constants.NEXT)
            }
        }
        return list
    }

    override fun getData(id: String): Album? {
        for (album in mCachedData) {
            if (id == album.mId) {
                return album
            }
        }
        return null
    }
}
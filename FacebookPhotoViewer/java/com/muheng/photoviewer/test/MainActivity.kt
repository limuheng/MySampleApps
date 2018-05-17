package com.muheng.photoviewer.test

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.facebook.GraphRequest.GraphJSONObjectCallback
import com.muheng.facebook.Album
import com.muheng.facebook.Photo
import com.muheng.photoviewer.utils.Constants
import com.muheng.photoviewer.DownloadUtils
import com.muheng.facebook.MeProfile
import com.muheng.photoviewer.R
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {
    private val PUBLIC_PROFILE = "public_profile"
    private val USER_PHOTOS = "user_photos"
    private val MSG_UPDATE_UI = 1
    private val MSG_UPDATE_ALBUM = 2

    var mMeProfile : MeProfile? = null
    var mCallbackManager : CallbackManager
    var mFacebookCallback : FacebookCallback<LoginResult>

    var mMeProfileRequest: GraphRequest? = null
    var mMeProfileCallback: GraphJSONObjectCallback = object : GraphJSONObjectCallback {
        override fun onCompleted(obj: JSONObject?, response: GraphResponse?) {
            Log.d("123", obj?.toString())
            mMeProfile = MeProfile()
            mMeProfile?.mId = obj?.getString(Constants.ID) ?: ""
            mMeProfile?.mName = obj?.getString(Constants.NAME) ?: ""
            mMeProfile?.mLink = obj?.getString(Constants.LINK) ?: ""

            var cover = obj?.getJSONObject(Constants.COVER)
            mMeProfile?.mCoverUrl = cover?.getString(Constants.SOURCE) ?: ""

            var picture = obj?.getJSONObject(Constants.PICTURE)
            var data = picture?.getJSONObject(Constants.DATA)
            mMeProfile?.mPictureUrl = data?.getString(Constants.URL) ?: ""

            var thread = Thread(object: Runnable {
                override fun run() {
                    var coverFile = DownloadUtils.downloadPhoto("me_cover.jpg", mMeProfile?.mCoverUrl)
                    mMeProfile?.mCoverUri = Uri.parse(coverFile)
                    Log.d("123", "Download: " + coverFile.toString())

                    var pictureFile = DownloadUtils.downloadPhoto("me_picture.jpg", mMeProfile?.mPictureUrl)
                    mMeProfile?.mPictureUri = Uri.parse(pictureFile)
                    Log.d("123", "Download: " + pictureFile.toString())

                    mHandler.sendEmptyMessage(MSG_UPDATE_UI)
                }
            })
            thread.start()
        }
    }

    var mAlbumRequest: GraphRequest? = null
    var mAlbumCallback: GraphJSONObjectCallback = object : GraphJSONObjectCallback {
        override fun onCompleted(obj: JSONObject?, response: GraphResponse?) {
            Log.d("123", obj?.toString())
            var albums = obj?.getJSONObject(Constants.ALBUMS)
            var albumsArray = albums?.getJSONArray(Constants.DATA)
            if (albumsArray != null) {
                mAdapter?.mData?.clear()
                for (i in 0..albumsArray.length().minus(1)) {
                    var album : Album = Album()
                    album.mId = albumsArray.getJSONObject(i).getString(Constants.ID)
                    album.mName = albumsArray.getJSONObject(i).getString(Constants.NAME)
                    var coverPhoto = albumsArray.getJSONObject(i).getJSONObject(Constants.COVER_PHOTO)
                    album.mCoverPhoto = Photo()
                    album.mCoverPhoto?.mId = coverPhoto.getString(Constants.ID)
                    try {
                        album.mCoverPhoto?.mName = coverPhoto.getString(Constants.NAME)
                    } catch (e : Exception) {
                        //e.printStackTrace()
                    }
                    album.mCoverPhoto?.mPicture = coverPhoto.getString(Constants.PICTURE)
                    album.mCoverPhoto?.mSource = coverPhoto.getString(Constants.SOURCE)
                    mAdapter?.mData?.add(album)
                }
                mAdapter?.notifyDataSetChanged()
            }
            var thread = Thread(object: Runnable {
                override fun run() {
                    if (mAdapter?.mData != null) {
                        var size = mAdapter?.mData?.size ?: 0
                        for (i in 0..size.minus(1)) {
                            var album = mAdapter?.mData?.get(i) as Album
                            var pictureUrl = album.mCoverPhoto?.mPicture
                            var pictureFile = DownloadUtils.downloadPhoto(extractFBPhotoName(pictureUrl), pictureUrl)
                            album.mCoverPhoto?.mPictureCache = Uri.parse(pictureFile)
                            Log.d("123", "Albun: " + album.mName + ",  cover uri: " + pictureFile.toString())
                            mHandler.sendEmptyMessage(MSG_UPDATE_ALBUM)
                        }
                    }
                }
            })
            thread.start()
        }
    }

    init {
        mCallbackManager = CallbackManager.Factory.create()
        mFacebookCallback = object : FacebookCallback<LoginResult> {
            override fun onCancel() {
                Toast.makeText(
                        this@MainActivity,
                        R.string.cancel,
                        Toast.LENGTH_SHORT).show();
            }

            override fun onSuccess(result: LoginResult?) {
                Toast.makeText(
                        this@MainActivity,
                        R.string.success,
                        Toast.LENGTH_SHORT).show();
            }

            override fun onError(error: FacebookException?) {
                Toast.makeText(
                        this@MainActivity,
                        R.string.error,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    var mHandler : Handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                MSG_UPDATE_UI -> {
                    updateMeUi()
                }
                MSG_UPDATE_ALBUM -> {
                    updateAlbums()
                }
            }
        }
    }

    var mMePhoto : ImageView? = null
    var mMeCover : ImageView? = null
    var mName : TextView? = null
    var mLoginButton : LoginButton? = null
    var mListView : ListView? = null

    var mAdapter : AlbumListTestAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_activity_main)

        DownloadUtils.sFilesDir = this@MainActivity.filesDir

        mMePhoto = findViewById(R.id.me_photo)
        mMeCover = findViewById(R.id.me_cover)
        mName = findViewById(R.id.name)
        mLoginButton = findViewById(R.id.login_button)
        mLoginButton?.setReadPermissions(Arrays.asList(PUBLIC_PROFILE, USER_PHOTOS))
        mLoginButton?.registerCallback(mCallbackManager, mFacebookCallback)

        mListView = findViewById(R.id.album_list)
        var layoutinflater : LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mAdapter = AlbumListTestAdapter(layoutinflater)
        mListView?.adapter = mAdapter
    }

    override fun onResume() {
        super.onResume()
        if (isLoggedIn()) {
            // Start to request user profile
            val parameters = Bundle()
            parameters.putString("fields", "id, name, link, cover, picture")
            // Request me profile
            mMeProfileRequest = createGraphRequest(AccessToken.getCurrentAccessToken(), parameters, mMeProfileCallback)
            mMeProfileRequest?.executeAsync()

            // request album list
            val parameters2 = Bundle()
            parameters2.putString("fields", "albums.limit(6){name,cover_photo{name,picture,source}}")
            mAlbumRequest = createGraphRequest(AccessToken.getCurrentAccessToken(), parameters2, mAlbumCallback)
            mAlbumRequest?.executeAsync()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mCallbackManager.onActivityResult(requestCode, resultCode, data)
    }

    fun isLoggedIn() : Boolean {
        var accessToken : AccessToken? = AccessToken.getCurrentAccessToken()
        return !(accessToken == null || accessToken.getPermissions().isEmpty())
    }

    fun createGraphRequest(accessToken : AccessToken, bundle : Bundle,
                           callback : GraphJSONObjectCallback) : GraphRequest {
        var request = GraphRequest.newMeRequest(accessToken, callback)
        request.setParameters(bundle);
        return request
    }

    fun updateMeUi() {
        mMePhoto?.setImageURI(mMeProfile?.mPictureUri)
        mMeCover?.setImageURI(mMeProfile?.mCoverUri)
        mName?.setText(mMeProfile?.mName)
    }

    fun updateAlbums() {
        mAdapter?.notifyDataSetChanged()
    }

    fun extractFBPhotoName(url : String?) : String? {
        var result : String?
        result = url?.substring(0, url.indexOf(".jpg") + 4)
        result = result?.substring(result.lastIndexOf('/'))
        return result
    }
}

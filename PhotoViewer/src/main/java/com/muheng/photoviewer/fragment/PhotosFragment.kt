package com.muheng.photoviewer.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.muheng.facebook.Photo
import com.muheng.photoviewer.PhotoViewActivity
import com.muheng.photoviewer.adapter.PhotosAdapter
import com.muheng.photoviewer.presenter.AlbumPhotosPresenter
import com.muheng.photoviewer.presenter.IPageView
import com.muheng.photoviewer.utils.Constants

/**
 * Created by Muheng Li on 2018/7/9.
 */
class PhotosFragment : PageFragment<Photo>() {

    companion object {
        const val TAG = "PhotosFragment"
    }

    private lateinit var mAlbumId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            mAlbumId = savedInstanceState.getString(Constants.EXTRA_ID, "")
        } else {
            mAlbumId = activity?.intent?.getStringExtra(Constants.EXTRA_ID) ?: ""
        }

        mPresenter = AlbumPhotosPresenter(this)
        mPresenter.init()
        mPresenter.registerRxBus()

        mAdapter = PhotosAdapter()

        if (mAlbumId.isNotBlank()) {
            mPresenter.loadStartPage(mAlbumId)
        } else {
            onNoData()
        }
    }

    override fun onItemClicked(item: Photo) {
        //Toast.makeText(context, "Photo $id was clicked!", Toast.LENGTH_SHORT).show()
        var intent = Intent(context, PhotoViewActivity::class.java)
        intent.putExtra(Constants.EXTRA_PHOTO, item)
        startActivity(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(Constants.EXTRA_ID, mAlbumId)
    }
}
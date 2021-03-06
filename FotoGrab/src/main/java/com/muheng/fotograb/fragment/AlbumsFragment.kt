package com.muheng.fotograb.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.muheng.facebook.Album
import com.muheng.fotograb.PhotosActivity
import com.muheng.fotograb.adapter.AlbumsAdapter
import com.muheng.fotograb.presenter.AlbumsPresenter
import com.muheng.fotograb.presenter.IPageView
import com.muheng.fotograb.utils.Constants

/**
 * Created by Muheng Li on 2018/7/9.
 */
class AlbumsFragment : PageFragment<Album>() {

    companion object {
        const val TAG = "AlbumsFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPresenter = AlbumsPresenter(this)
        mPresenter.init()
        mPresenter.registerRxBus()
        mPresenter.loadStartPage()

        mAdapter = AlbumsAdapter()
    }

    override fun onItemClicked(item: Album) {
        //Toast.makeText(context, "Album$id was clicked!", Toast.LENGTH_SHORT).show()
        var intent = Intent(context, PhotosActivity::class.java)
        intent.putExtra(Constants.EXTRA_ID, item.id)
        intent.putExtra(Constants.EXTRA_NAME, "${item.name}(${item.photo_count})")
        startActivity(intent)
    }

    override fun onError(error: Throwable) {
        Log.d(TAG, error.message)
        Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
    }

}
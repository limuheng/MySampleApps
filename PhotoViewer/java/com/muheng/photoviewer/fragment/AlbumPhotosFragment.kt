package com.muheng.photoviewer.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.muheng.facebook.Photo
import com.muheng.photoviewer.R
import com.muheng.photoviewer.adapter.PhotosAdapter
import com.muheng.photoviewer.utils.Constants
import com.muheng.photoviewer.manager.FacebookManager
import com.muheng.photoviewer.manager.PhotosManager
import com.muheng.photoviewer.manager.PhotoManager.ICallback
import com.muheng.photoviewer.utils.UIHandler

class AlbumPhotosFragment : FacebookFragment(), ICallback<Photo> {
    companion object {
        val TAG: String = "AlbumPhotosFragment"
    }

    private var mPhotosRequest: GraphRequest? = null
    private var mPhotosCallback = object : GraphRequest.Callback {
        override fun onCompleted(response: GraphResponse?) {
            Log.d("mPhotosCallback", "response: " + response?.jsonObject?.toString())
            if (response?.jsonObject?.has(Constants.NAME) == true) {
                PhotosManager.getInstance()?.mAlbumName = response.jsonObject?.getString(Constants.NAME)
                updateTitle()
            }
            if (response?.jsonObject?.has(Constants.PHOTOS) == true) {
                var photos = response.jsonObject?.getJSONObject(Constants.PHOTOS)
                var list = PhotosManager.getInstance()?.parsingData(photos, mHandler)
                PhotosManager.getInstance()?.appendData(list)
            }
            mHandler?.sendEmptyMessage(Constants.MSG_LOADING_DONE)
            mHandler?.sendEmptyMessage(Constants.MSG_UPDATE_UI)
        }
    }

    private var mHandler : UIHandler? = null

    private var mProgressBar : ProgressBar? = null
    private var mList: RecyclerView? = null
    private var mAdapter : PhotosAdapter? = null

    var mAlbumId : String = ""

    private var mScrollListener : RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            var spanCount = (recyclerView?.layoutManager as StaggeredGridLayoutManager).spanCount
            //Log.d(TAG, "spanCount: " + spanCount)
            var lastVisibleItemPos = IntArray(spanCount)
            (recyclerView.layoutManager as StaggeredGridLayoutManager).findLastCompletelyVisibleItemPositions(lastVisibleItemPos)
            var lastVisibleItem = Math.max(lastVisibleItemPos[spanCount - 2], lastVisibleItemPos[spanCount - 1]);//findMax(lastPositions);
            //Log.d(TAG, "lastVisibleItem: " + lastVisibleItem + ", mAdapter.itemCount: " + mAdapter?.itemCount)
            if (lastVisibleItem == mAdapter?.itemCount?.minus(1) && mAdapter?.itemCount != 0) {
                PhotosManager.getInstance()?.loadNext(this@AlbumPhotosFragment, mHandler)
            }
        }
    }

    private var mPhotoClickListener = object : PhotosAdapter.OnItemClickListener {
        override fun onItemClick(id: String?) {
            var intent = Intent(Constants.ACT_VIEW_PHOTO)
            intent.putExtra(Constants.EXTRA_PHOTO_ID, id)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            try {
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHandler = UIHandler(this)

        if (savedInstanceState != null) {
            mAlbumId = savedInstanceState.getString(Constants.EXTRA_ALBUM_ID)
        } else {
            var intent = activity?.intent
            mAlbumId = intent?.getStringExtra(Constants.EXTRA_ALBUM_ID) ?: ""
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        var rootView = inflater.inflate(R.layout.fragment_album_photos, container, false)
        mProgressBar = rootView?.findViewById(R.id.loading)
        mList = rootView?.findViewById(R.id.list)
        mAdapter = PhotosAdapter()
        mAdapter?.mItemClickListener = mPhotoClickListener
        mList?.adapter = mAdapter
        mList?.layoutManager = StaggeredGridLayoutManager(Constants.SPAN_COUNT_NORMAL, StaggeredGridLayoutManager.VERTICAL)
        mList?.addOnScrollListener(mScrollListener)
        return rootView ?: super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(Constants.EXTRA_ALBUM_ID, mAlbumId)
        super.onSaveInstanceState(outState)
    }

    override fun handleMessage(msg: Message?) {
        when (msg?.what) {
            Constants.MSG_UPDATE_UI -> {
                updateUI()
            }
            Constants.MSG_LOADING -> {
                showProgress()
            }
            Constants.MSG_LOADING_DONE -> {
                hideProgress()
            }
        }
    }

    override fun onSuccess(isNext: Boolean, list: List<Photo>) {
        mHandler?.sendEmptyMessage(Constants.MSG_LOADING_DONE)
        mHandler?.sendEmptyMessage(Constants.MSG_UPDATE_UI)
    }

    override fun onFailed(isNext: Boolean, error: String) {
        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
    }

    override fun updateUI() {
        mAdapter?.notifyDataSetChanged()
    }

    override fun showProgress(idx : Int) {
        mProgressBar?.visibility = View.VISIBLE
    }

    override fun hideProgress(idx : Int) {
        mProgressBar?.visibility = View.GONE
    }

    override fun executeRequest() {
        Log.d(TAG, "mAlbumId: " + mAlbumId)
        if (mAlbumId.isNotEmpty()) {
            mHandler?.sendEmptyMessage(Constants.MSG_LOADING)
            PhotosManager.getInstance()?.mCachedData?.clear()
            // request album list
            val parameters = Bundle()
            parameters.putString("fields", "photos.limit(" + Constants.PHOTOS_PER_PAGE + "){name,link,id,picture,created_time,webp_images},name")
            mPhotosRequest = FacebookManager.createGraphPathRequest(parameters, mAlbumId, mPhotosCallback)
            mPhotosRequest?.executeAsync()
        }
    }

    override fun onBackPressed() : Boolean {
        return true
    }

    override fun onVisible() {
        if (isLoggedIn()) {
            var intent = activity?.intent
            //Log.d(TAG, "[onVisible] intent: " + intent?.action)
            mAlbumId = intent?.getStringExtra(Constants.EXTRA_ALBUM_ID) ?: ""
            executeRequest()
        }
    }

    override fun updateTitle() {
        (activity as AppCompatActivity).supportActionBar?.title = PhotosManager.getInstance()?.mAlbumName
    }
}
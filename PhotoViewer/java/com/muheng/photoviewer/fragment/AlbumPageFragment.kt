package com.muheng.photoviewer.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.support.v7.widget.GridLayoutManager
import com.muheng.facebook.Album
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.muheng.photoviewer.R
import com.muheng.photoviewer.adapter.AlbumsPageAdapter
import com.muheng.photoviewer.customview.InterceptTouchFrameLayout
import com.muheng.photoviewer.manager.AlbumPageManager
import com.muheng.photoviewer.manager.FacebookManager
import com.muheng.photoviewer.manager.PhotoManager
import com.muheng.photoviewer.utils.*
import org.json.JSONObject


class AlbumPageFragment : FacebookFragment(), PhotoManager.ICallback<Album> {
    companion object {
        val TAG : String = "AlbumPageFragment"
    }

    private var mAlbumRequest: GraphRequest? = null
    private var mAlbumCallback = object : GraphRequest.GraphJSONObjectCallback  {
        override fun onCompleted(jsonObj: JSONObject?, response: GraphResponse?) {
            if (jsonObj?.has(Constants.ALBUMS) == true) {
                var albums = jsonObj.getJSONObject(Constants.ALBUMS)
                var albumList = AlbumPageManager.getInstance()?.parsingData(albums, mHandler)
                AlbumPageManager.getInstance()?.appendData(albumList)
                mAdapter?.notifyDataSetChanged()
            } else {
                mNoData?.visibility = View.VISIBLE
            }
        }
    }

    private var mHandler : UIHandler? = null

    private var mListContainer : InterceptTouchFrameLayout? = null
    private var mList: RecyclerView? = null
    private var mNoData : TextView? = null
    private var mAdapter : AlbumsPageAdapter? = null

    private var mGestureDetector : GestureDetector? = null
    private var mLastStartX : Float = Constants.ZERO_F

    private var mGestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            var moveDistance = e2?.x?.minus(e1?.x ?: Constants.ZERO_F) ?: Constants.ZERO_F
            if (moveDistance > Constants.SWIPE_THRESHOLD) {
                if (mLastStartX == e1?.x) {
                    return true;
                }
                mLastStartX = e1?.x ?: Constants.ZERO_F
                // load prvious album
                AlbumPageManager.getInstance()?.loadPrev(this@AlbumPageFragment, mHandler)
            } else if (moveDistance < -Constants.SWIPE_THRESHOLD) {
                if (mLastStartX == e1?.x) {
                    return true;
                }
                mLastStartX = e1?.x ?: Constants.ZERO_F
                // load next photo
                AlbumPageManager.getInstance()?.loadNext(this@AlbumPageFragment, mHandler)
            }
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }
    }

    private var mAlbumClickListener = object : AlbumsPageAdapter.OnItemClickListener {
        override fun onItemClick(id: String?) {
            //Toast.makeText(context, "Album ID: " + id, Toast.LENGTH_SHORT).show()
            if (AlbumPageManager.getInstance()?.mLoading != true) {
                var intent = Intent(Constants.ACT_VIEW_ALBUM)
                intent.putExtra(Constants.EXTRA_ALBUM_ID, id)
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                Log.d(TAG, "[mAlbumClickListener] >>> Diable click due to loading")
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHandler = UIHandler(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater.inflate(R.layout.fragment_album_page, container, false)
        mListContainer = rootView?.findViewById(R.id.list_container)
        mList = rootView?.findViewById(R.id.list)
        mNoData = rootView?.findViewById(R.id.no_data)

        mAdapter = AlbumsPageAdapter()
        mAdapter?.mItemHeight = RuntimeUtils.sAlbumHeightDp
        mAdapter?.mItemClickListener = mAlbumClickListener

        mList?.adapter = mAdapter
        mList?.layoutManager = GridLayoutManager(activity, RuntimeUtils.sSpanCount)

        mGestureDetector = GestureDetector(activity, mGestureListener)
        mListContainer?.mGestureDetector = mGestureDetector
        return rootView ?: super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (isLoggedIn()) {
            executeRequest()
        }
    }

    override fun handleMessage(msg: Message?) {
        when (msg?.what) {
            Constants.MSG_UPDATE_UI -> {
                updateUI()
            }
        }
    }

    override fun onSuccess(isNext: Boolean, list: List<Album>) {
        mHandler?.sendEmptyMessage(Constants.MSG_LOADING_DONE)
        mHandler?.sendEmptyMessage(Constants.MSG_UPDATE_UI)
    }

    override fun showProgress(idx: Int) {

    }

    override fun onFailed(isNext: Boolean, error: String) {
        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
    }

    override fun hideProgress(idx: Int) {

    }

    override fun updateUI() {
        mAdapter?.notifyDataSetChanged()
    }

    override fun executeRequest() {
        AlbumPageManager.getInstance()?.mCachedData?.clear()
        // request album list
        val parameters = Bundle()
        parameters.putString(
                "fields", "albums.limit(" +
                RuntimeUtils.sSpanCount * RuntimeUtils.sAlbumRowPerPage +
                "){name,cover_photo{name,picture,source}}")
        mAlbumRequest = FacebookManager.createGraphRequest(parameters, mAlbumCallback)
        mAlbumRequest?.executeAsync()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onVisible() {

    }

    override fun updateTitle() { }
}
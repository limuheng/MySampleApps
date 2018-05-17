package com.muheng.photoviewer

import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.OnScrollListener
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.muheng.facebook.Album
import com.muheng.photoviewer.utils.FacebookManager
import com.muheng.photoviewer.utils.AlbumManager
import com.muheng.photoviewer.utils.Constants
import com.muheng.photoviewer.utils.UIHandler
import com.muheng.photoviewer.utils.PhotoManager.ICallback
import org.json.JSONObject

class AlbumListFragment : FacebookFragment(), ICallback<Album> {
    companion object {
        val TAG : String = "AlbumListFragment"
        val LOAD_PREV : Int = 0
        val LOAD_NEXT : Int = 1
    }

    private var mAlbumRequest: GraphRequest? = null
    private var mAlbumCallback = object : GraphRequest.GraphJSONObjectCallback  {
        override fun onCompleted(jsonObj: JSONObject?, response: GraphResponse?) {
            if (jsonObj?.has(Constants.ALBUMS) == true) {
                var albums = jsonObj.getJSONObject(Constants.ALBUMS)
                var albumList = AlbumManager.getInstance()?.parsingData(albums, mHandler)
                AlbumManager.getInstance()?.appendData(albumList)
                mAdapter?.notifyDataSetChanged()
            } else {
                mNoData?.visibility = View.VISIBLE
            }
        }
    }

    private var mHandler : UIHandler? = null

    private var mProgressBar : Array<ProgressBar?> = arrayOfNulls<ProgressBar?>(2)
    private var mList: RecyclerView? = null
    private var mNoData : TextView? = null
    private var mAdapter : AlbumsAdapter? = null

    private var mScrollListener : OnScrollListener = object : OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            var spanCount = (recyclerView?.layoutManager as StaggeredGridLayoutManager).spanCount
            //Log.d(TAG, "spanCount: " + spanCount)
            var lastVisibleItemPos = IntArray(spanCount)
            (recyclerView.layoutManager as StaggeredGridLayoutManager).findLastCompletelyVisibleItemPositions(lastVisibleItemPos)
            var lastVisibleItem = Math.max(lastVisibleItemPos[spanCount - 2], lastVisibleItemPos[spanCount - 1]);
            //Log.d(TAG, "lastVisibleItem: " + lastVisibleItem + ", mAdapter.itemCount: " + mAdapter?.itemCount)

            if (lastVisibleItem == mAdapter?.itemCount?.minus(1) && mAdapter?.itemCount != 0) {
                AlbumManager.getInstance()?.loadNext(this@AlbumListFragment, mHandler)
            }
        }
    }

    private var mAlbumClickListener = object : AlbumsAdapter.OnItemClickListener {
        override fun onItemClick(id: String?) {
            //Toast.makeText(context, "Album ID: " + id, Toast.LENGTH_SHORT).show()
            if (AlbumManager.getInstance()?.mLoading != true) {
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
        super.onCreate(savedInstanceState)
        var rootView = inflater.inflate(R.layout.fragment_album_list, container, false)
        mProgressBar[LOAD_PREV] = rootView?.findViewById(R.id.load_prev)
        mProgressBar[LOAD_NEXT] = rootView?.findViewById(R.id.load_next)
        mList = rootView?.findViewById(R.id.list)
        mNoData = rootView?.findViewById(R.id.no_data)
        mAdapter = AlbumsAdapter()
        mAdapter?.mItemClickListener = mAlbumClickListener
        mList?.adapter = mAdapter
        mList?.layoutManager = StaggeredGridLayoutManager(Constants.SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL)
        mList?.addOnScrollListener(mScrollListener)
// Add horizontal divider between items
//        var itemDecorator = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
//        if (context != null) {
//            var drawable = ContextCompat.getDrawable(context as Context, R.drawable.recyclerview_divider)
//            if (drawable != null) {
//                itemDecorator.setDrawable(drawable);
//            }
//        }
//        mList?.addItemDecoration(itemDecorator)
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
            Constants.MSG_LOAD_NEXT -> {
                showProgress(LOAD_NEXT)
            }
            Constants.MSG_LOAD_NEXT_DONE -> {
                hideProgress(LOAD_NEXT)
            }
        }
    }

    override fun onSuccess(isNext : Boolean, list : List<Album>) {
        mHandler?.sendEmptyMessage(Constants.MSG_LOAD_NEXT_DONE)
        mHandler?.sendEmptyMessage(Constants.MSG_UPDATE_UI)
    }

    override fun onFailed(isNext : Boolean,  error : String) {
        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
    }

    override fun updateUI() {
        mAdapter?.notifyDataSetChanged()
    }

    override fun showProgress(idx : Int) {
        if (idx >= LOAD_PREV && idx <= LOAD_NEXT) {
            mProgressBar[idx]?.visibility = View.VISIBLE
        }
    }

    override fun hideProgress(idx : Int) {
        if (idx >= LOAD_PREV && idx <= LOAD_NEXT) {
            mProgressBar[idx]?.visibility = View.GONE
        }
    }

    override fun executeRequest() {
        AlbumManager.getInstance()?.mCachedData?.clear()
        // request album list
        val parameters = Bundle()
        parameters.putString("fields", "albums.limit(" + Constants.ALBUMS_PER_PAGE + "){name,cover_photo{name,picture,source}}")
        mAlbumRequest = FacebookManager.createGraphRequest(parameters, mAlbumCallback)
        mAlbumRequest?.executeAsync()
    }

    override fun onBackPressed() : Boolean {
        return false
    }

    override fun onVisible() {
        // Do nothing
    }
}
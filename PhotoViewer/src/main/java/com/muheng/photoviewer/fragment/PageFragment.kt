package com.muheng.photoviewer.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.muheng.facebook.Page
import com.muheng.photoviewer.R
import com.muheng.photoviewer.adapter.PageAdapter
import com.muheng.photoviewer.presenter.IConfigChanged
import com.muheng.photoviewer.presenter.IPageView
import com.muheng.photoviewer.presenter.PagePresenter
import com.muheng.photoviewer.utils.Constants
import com.muheng.photoviewer.utils.NetworkCheckUtils
import com.muheng.photoviewer.utils.SpanCountUtils

/**
 * Created by Muheng Li on 2018/7/13.
 */
abstract class PageFragment<T>() : Fragment(), IPageView<T>, IConfigChanged {

    companion object {
        const val TAG = "PageFragment"
    }

    protected open lateinit var mAdapter: PageAdapter<T>
    protected var mList: RecyclerView? = null
    protected var mNoData: TextView? = null

    protected open var mScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            var spanCount = (recyclerView?.layoutManager as StaggeredGridLayoutManager).spanCount
            // Check if scroll to the bottom of the list
            var lastVisiblePositions = IntArray(spanCount)
            (recyclerView?.layoutManager as StaggeredGridLayoutManager).findLastCompletelyVisibleItemPositions(lastVisiblePositions)
            var lastVisibleItemPos = Math.max(lastVisiblePositions[spanCount - 2], lastVisiblePositions[spanCount - 1])
            if (lastVisibleItemPos == mAdapter.itemCount.minus(1) && mAdapter.itemCount != 0) {
                if (NetworkCheckUtils.isConnected(activity)) {
                    var lastPageAlbums = mAdapter.pages[mAdapter.pages.size.minus(1)]
                    mPresenter.loadMore(lastPageAlbums.paging.next)
                } else {
                    Toast.makeText(context, "No network access!", Toast.LENGTH_SHORT).show()
                }
            }
            // Check if scroll to the top of list
            var firstVisiblePositions = IntArray(spanCount)
            (recyclerView.layoutManager as StaggeredGridLayoutManager).findFirstCompletelyVisibleItemPositions(firstVisiblePositions)
            var firstVisibleItemPos = Math.min(firstVisiblePositions[spanCount - 2], firstVisiblePositions[spanCount - 1])
            if (firstVisibleItemPos == 0 && mAdapter.itemCount != 0) {
                if (NetworkCheckUtils.isConnected(activity)) {
                    var firstPageAlbums = mAdapter.pages[0]
                    mPresenter.loadMore(firstPageAlbums.paging.previous, false)
                } else {
                    Toast.makeText(context, "No network access!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

// For GridLayoutManager
//    // Check if scroll to the bottom of the list
//    var lastVisibleItemPos = (recyclerView?.layoutManager as GridLayoutManager).findLastCompletelyVisibleItemPosition()
//    if (lastVisibleItemPos == mAdapter.itemCount.minus(1) && mAdapter.itemCount != 0) {
//        if (NetworkCheckUtils.isConnected(activity)) {
//            var lastPageAlbums = mAdapter.pages[mAdapter.pages.size.minus(1)]
//            mPresenter.loadMore(lastPageAlbums.paging.next)
//        } else {
//            Toast.makeText(context, "No network access!", Toast.LENGTH_SHORT).show()
//        }
//    }
//    // Check if scroll to the top of list
//    var firstVisibleItemPos = (recyclerView?.layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition()
//    if (firstVisibleItemPos == 0 && mAdapter.itemCount != 0) {
//        if (NetworkCheckUtils.isConnected(activity)) {
//            var firstPageAlbums = mAdapter.pages[0]
//            mPresenter.loadMore(firstPageAlbums.paging.previous, false)
//        } else {
//            Toast.makeText(context, "No network access!", Toast.LENGTH_SHORT).show()
//        }
//    }

    protected open lateinit var mPresenter: PagePresenter<T>

    override fun onNoData() {
        mNoData?.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        mPresenter.unregisterRxBus()
        mPresenter.dispose()
        super.onDestroy()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater.inflate(R.layout.fragment_list, container, false)

        mList = rootView?.findViewById(R.id.list)
        mNoData = rootView?.findViewById(R.id.no_data)

        mList?.adapter = mAdapter
        mList?.layoutManager = StaggeredGridLayoutManager(SpanCountUtils.getSpanCount(), StaggeredGridLayoutManager.VERTICAL)
        //GridLayoutManager(context, Constants.SPAN_COUNT_NORMAL)
        (mList?.layoutManager as StaggeredGridLayoutManager).gapStrategy = GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        mList?.addOnScrollListener(mScrollListener)

        return rootView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        onConfigChanged()
    }

    // Ref: https://stackoverflow.com/questions/34028492/finding-top-offset-of-first-visible-item-in-a-recyclerview
    override fun onPage(page: Page<T>, isNext: Boolean) {
        if (isNext) {
            appendPage(page)
        } else {
            insertPage(page)
        }
    }

    override fun onItemClicked(item: T) {
        Toast.makeText(context, "Item was clicked!", Toast.LENGTH_SHORT).show()
    }

    override fun onError(error: Throwable) {
        Log.d(TAG, error.message)
        Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
    }

    override fun appendPage(page: Page<T>) {
        var itemCountPerPage = PageAdapter.PAFE_SIZE
        // If page count is greater or equal than max count, remove oldest page to make space for new page
        if (mAdapter.getPageCount() > PageAdapter.MAX_PAGE_COUNT) {
            // Remove items in first page
            mAdapter.pages.removeAt(0)
            // Notify items removed
            mAdapter.notifyItemRangeRemoved(
                    itemCountPerPage, (mAdapter.itemCount - itemCountPerPage))

            // Calculate position of the visible item
            var spanCount = (mList?.layoutManager as StaggeredGridLayoutManager).spanCount
            var firstVisiblePositions = IntArray(spanCount)
            (mList?.layoutManager as StaggeredGridLayoutManager).findFirstVisibleItemPositions(firstVisiblePositions)
            var firstPos = Math.min(firstVisiblePositions[spanCount - 2], firstVisiblePositions[spanCount - 1])
            val v = (mList?.layoutManager as StaggeredGridLayoutManager).getChildAt(0)

            // Force the RecyclerView to keep at the same visible item relative to new position
            // Using substraction because we removed the first page
            (mList?.layoutManager as StaggeredGridLayoutManager).scrollToPositionWithOffset(
                    firstPos - itemCountPerPage, v?.top ?: 0)
        }

        // Add new items to last page
        mAdapter.pages.add(page)
        mAdapter.notifyDataSetChanged()
    }

    override fun insertPage(page: Page<T>) {
        var itemCountPerPage = PageAdapter.PAFE_SIZE
        // Always insert page at the beginning
        mAdapter.pages.add(0, page)
        // Remove last page
        mAdapter.pages.removeAt(mAdapter.pages.size.minus(1))

        mAdapter.notifyDataSetChanged()

        // Calculate position of the visible item
        var spanCount = (mList?.layoutManager as StaggeredGridLayoutManager).spanCount
        var firstVisiblePositions = IntArray(spanCount)
        (mList?.layoutManager as StaggeredGridLayoutManager).findFirstVisibleItemPositions(firstVisiblePositions)
        var firstPos = Math.min(firstVisiblePositions[spanCount - 2], firstVisiblePositions[spanCount - 1])
        val v = (mList?.layoutManager as StaggeredGridLayoutManager).getChildAt(0)

        // Force the RecyclerView to keep at the same visible item relative to new position
        // Using addition because we inserted a page at the beginning
        (mList?.layoutManager as StaggeredGridLayoutManager).scrollToPositionWithOffset(
                firstPos + itemCountPerPage, v?.top ?: 0)
    }

    override fun onConfigChanged() {
        SpanCountUtils.calculateSpanCount()
    }
}
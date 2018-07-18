package com.muheng.photoviewer.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.muheng.facebook.Page

/**
 * Created by Muheng Li on 2018/7/13.
 */
abstract class PageAdapter<T> : RecyclerView.Adapter<PageViewHolder>() {

    companion object {
        const val MAX_PAGE_COUNT = 3
        const val PAFE_SIZE = 20
    }

    var pages = ArrayList<Page<T>>()

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        // TODO override
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        // Return a new holder instance
        return PageViewHolder(null)
    }

    override fun getItemCount(): Int {
        var size = 0
        for (page in pages) {
            size += page.data.size
        }
        return size
    }

    fun getPageCount(): Int {
        return pages.size
    }

}
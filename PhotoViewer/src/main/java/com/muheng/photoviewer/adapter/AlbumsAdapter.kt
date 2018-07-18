package com.muheng.photoviewer.adapter

import android.view.ViewGroup
import android.view.LayoutInflater
import com.muheng.facebook.Album
import com.muheng.photoviewer.R


class AlbumsAdapter : PageAdapter<Album>() {

    companion object {
        const val MAX_PAGE_COUNT = 3
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        if (pages.size > 0) {
            var pageIdx = position / PAFE_SIZE
            var posInPage = position % PAFE_SIZE
            var album = pages[pageIdx].data[posInPage]
            (holder as AlbumViewHolder).bindView(album)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the custom layout
        val root = inflater.inflate(R.layout.layout_album, parent, false)

        // Return a new holder instance
        return AlbumViewHolder(root)
    }

}
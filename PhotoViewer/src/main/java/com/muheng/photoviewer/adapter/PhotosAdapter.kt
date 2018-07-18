package com.muheng.photoviewer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.muheng.facebook.Photo
import com.muheng.photoviewer.R

class PhotosAdapter : PageAdapter<Photo>() {

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        if (pages.size > 0) {
            var pageIdx = position / PAFE_SIZE
            var posInPage = position % PAFE_SIZE
            var photo = pages[pageIdx].data[posInPage]
            (holder as PhotoViewHolder).bindView(photo, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val context = parent.getContext()
        val inflater = LayoutInflater.from(context)

        // Inflate the custom layout
        val root = inflater.inflate(R.layout.layout_photo, parent, false)

        // Return a new holder instance
        return PhotoViewHolder(root)
    }

}
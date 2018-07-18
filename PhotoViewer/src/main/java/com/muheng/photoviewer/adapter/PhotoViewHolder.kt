package com.muheng.photoviewer.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.muheng.facebook.Photo
import com.muheng.photoviewer.R
import com.muheng.rxjava.Event
import com.muheng.rxjava.RxEventBus

/**
 * Created by Muheng Li on 2018/7/13.
 */
class PhotoViewHolder(itemView: View?) : PageViewHolder(itemView) {
    var container: ViewGroup? = null
    var imageView: ImageView? = null

    init {
        container = itemView?.findViewById(R.id.photo_container)
        imageView = itemView?.findViewById(R.id.photo)
    }

    fun bindView(photo: Photo, position: Int) {
        container?.setOnClickListener {
            RxEventBus.get()?.send(Event.PhotoClicked(photo))
        }

        try {
            //imageView?.layoutParams?.height = 300 + (position % 2) * 150
            var photoUrl: String
            if (photo.webp_images.isNotEmpty()) {
                var midIdx = photo.webp_images.size.minus(1) / 2
                photoUrl = photo.webp_images[midIdx].source
            } else {
                // Use default picture as album cover
                photoUrl = photo.picture
            }
            Glide.with(container?.context).load(photoUrl).centerCrop()
                    .placeholder(R.drawable.image_coming_soon).into(imageView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
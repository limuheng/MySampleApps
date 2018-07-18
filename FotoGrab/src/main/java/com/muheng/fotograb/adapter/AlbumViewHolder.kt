package com.muheng.fotograb.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.muheng.facebook.Album
import com.muheng.fotograb.R
import com.muheng.rxjava.Event
import com.muheng.rxjava.RxEventBus

/**
 * Created by Muheng Li on 2018/7/13.
 */
class AlbumViewHolder(itemView: View?) : PageViewHolder(itemView) {
    private var albumContainer: ViewGroup? = null
    private var albumName: TextView? = null
    private var albumCover: ImageView? = null

    init {
        albumContainer = itemView?.findViewById(R.id.album_container)
        albumName = itemView?.findViewById(R.id.album_name)
        albumCover = itemView?.findViewById(R.id.cover_photo)
    }

    fun bindView(album: Album) {
        albumContainer?.setOnClickListener {
            RxEventBus.get()?.send(Event.AlbumClicked(album))
        }

        albumName?.text = album.name

        try {
            var photoUrl: String
            // Get the first photo as album cover
            if (album.photos.data.isNotEmpty()) {
                var midIdx = album.photos.data[0].webp_images.size.minus(1) / 2
                photoUrl = album.photos.data[0].webp_images[midIdx].source
                Glide.with(albumContainer?.context).load(photoUrl).centerCrop()
                        .placeholder(R.drawable.image_coming_soon).into(albumCover)
            } else {
                // Use default picture as album cover
                photoUrl = album.picture.data.url
                Glide.with(albumContainer?.context).load(photoUrl).centerCrop()
                        .placeholder(R.drawable.image_coming_soon).into(albumCover)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
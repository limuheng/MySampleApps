package com.muheng.photoviewer.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.muheng.facebook.Album
import com.muheng.photoviewer.R
import com.muheng.photoviewer.manager.AlbumManager


class AlbumsAdapter : RecyclerView.Adapter<AlbumsAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(id : String?)
    }

    var mItemClickListener : OnItemClickListener? = null

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        var albums = AlbumManager.getInstance()?.getItem(position)
        holder?.bindView(albums, mItemClickListener)
    }

    override fun getItemCount(): Int {
        return (AlbumManager.getInstance()?.mCachedData?.size ?: 0)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val context = parent?.getContext()
        val inflater = LayoutInflater.from(context)

        // Inflate the custom layout
        val root = inflater.inflate(R.layout.layout_album, parent, false)

        // Return a new holder instance
        return ViewHolder(root)
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var _Album : ViewGroup? = null
        var _AlbumName : TextView? = null
        var _AlbumCover : ImageView? = null

        init {
            _Album = itemView?.findViewById(R.id.album_container)
            _AlbumName = itemView?.findViewById(R.id.album_name)
            _AlbumCover = itemView?.findViewById(R.id.cover_photo)
        }

        fun bindView(albums : List<Album>?, clickListener : OnItemClickListener?) {
            if (albums != null) {
                _AlbumName?.text = albums[0].mName
                _Album?.setOnClickListener { clickListener?.onItemClick(albums[0].mId) }
                try {
                    var photoUrl = albums[0].mCoverPhoto?.mSource ?: albums[0].mCoverPhoto?.mPicture
                    Glide.with(_Album?.context).load(photoUrl).centerCrop().
                          placeholder(R.drawable.image_coming_soon).into(_AlbumCover)
                } catch (e : Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
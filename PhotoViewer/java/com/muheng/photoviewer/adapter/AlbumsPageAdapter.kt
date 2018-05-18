package com.muheng.photoviewer.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.muheng.facebook.Album
import com.muheng.photoviewer.R
import com.muheng.photoviewer.manager.AlbumPageManager
import com.muheng.photoviewer.utils.DimenUtils

class AlbumsPageAdapter : RecyclerView.Adapter<AlbumsPageAdapter.ViewHolder>() {

    var mItemHeight : Int = 0

    interface OnItemClickListener {
        fun onItemClick(id : String?)
    }

    var mItemClickListener : OnItemClickListener? = null

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        var albums = AlbumPageManager.getInstance()?.getItem(position)
        holder?.bindView(albums, mItemClickListener)
    }

    override fun getItemCount(): Int {
        return (AlbumPageManager.getInstance()?.mCachedData?.size ?: 0)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val context = parent?.getContext()
        val inflater = LayoutInflater.from(context)

        // Inflate the custom layout
        val root = inflater.inflate(R.layout.layout_album, parent, false)

        // Return a new holder instance
        return ViewHolder(root, mItemHeight)
    }

    class ViewHolder(itemView: View?, height : Int) : RecyclerView.ViewHolder(itemView) {
        var _Album : ViewGroup? = null
        var _AlbumName : TextView? = null
        var _AlbumCover : ImageView? = null

        init {
            _Album = itemView?.findViewById(R.id.album_container)
            _AlbumCover = itemView?.findViewById(R.id.cover_photo)
            _AlbumName = itemView?.findViewById(R.id.album_name)
            if (height > 0) {
                _Album?.layoutParams?.height = DimenUtils.dpToPx(height)
                _AlbumCover?.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 2.0f)
                _AlbumName?.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f)
            }

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
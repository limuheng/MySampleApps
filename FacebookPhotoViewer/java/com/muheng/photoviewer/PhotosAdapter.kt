package com.muheng.photoviewer

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.muheng.facebook.Photo
import com.muheng.photoviewer.utils.PhotosManager

class PhotosAdapter : RecyclerView.Adapter<PhotosAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(id : String?)
    }

    var mItemClickListener : OnItemClickListener? = null

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        var photo = PhotosManager.getInstance()?.getItem(position)
        holder?.bindView(photo, position, mItemClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val context = parent?.getContext()
        val inflater = LayoutInflater.from(context)

        // Inflate the custom layout
        val root = inflater.inflate(R.layout.layout_photo, parent, false)

        // Return a new holder instance
        return ViewHolder(root)
    }

    override fun getItemCount(): Int {
        return (PhotosManager.getInstance()?.mCachedData?.size ?: 0)
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var mContainer : ViewGroup? = null
        var _ImageView : ImageView? = null

        init {
            mContainer = itemView?.findViewById(R.id.photo_container)
            _ImageView = itemView?.findViewById(R.id.photo)
        }

        fun bindView(photo : List<Photo>?, position : Int, clickListener : OnItemClickListener?) {
            if (photo != null) {
//                var photoUri = photo[0].mSourceCache ?: photo[0].mPictureCache
//                if (photoUri != null) {
//                    _ImageView?.setImageBitmap(BitmapUtils.decodeImage(photoUri, 4))
//                }
                try {
                    var photoUrl = photo[0].mSource ?: photo[0].mPicture
                    Glide.with(_ImageView?.context).load(photoUrl).centerCrop().
                          placeholder(R.drawable.image_coming_soon).into(_ImageView)
                } catch (e : Exception) {
                    e.printStackTrace()
                }
                _ImageView?.layoutParams?.height = 300 + (position % 2) * 150
                mContainer?.setOnClickListener { clickListener?.onItemClick(photo[0].mId) }
            }
        }
    }
}
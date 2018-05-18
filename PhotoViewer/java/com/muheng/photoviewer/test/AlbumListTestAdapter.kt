package com.muheng.photoviewer.test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.muheng.facebook.Album
import com.muheng.photoviewer.R

class AlbumListTestAdapter(inflater: LayoutInflater) : BaseAdapter() {

    var mInflater: LayoutInflater? = null
    var mData : ArrayList<Album> = ArrayList<Album> ()

    init {
        mInflater = inflater
    }

    override fun getView(pos: Int, convertView: View?, parent: ViewGroup?): View? {
        var albums = getItem(pos)
        var result: View? = null
        var viewHolder: ViewHolder? = null

        if (convertView != null) {
            var obj = convertView.getTag()
            if (obj != null && obj is ViewHolder) {
                viewHolder = obj
                result = convertView
            }
        }

        if (result == null) {
            result = mInflater?.inflate(R.layout.test_item_albums, parent, false)
            if (result != null) {
                viewHolder = ViewHolder(result)
                result.setTag(viewHolder);
            }
        }

        viewHolder?.bindView(albums)
        return result
    }

    override fun getItem(pos: Int): Array<Album?> {
        var albums : Array<Album?> = arrayOf(null, null)
        var first = pos * 2
        var second = pos * 2 + 1
        if (first >= 0 && first < mData.size) {
            albums[0] = mData.get(first)
        }
        if (second >= 0 && second < mData.size) {
            albums[1] = mData.get(second)
        }
        return albums
    }

    override fun getItemId(pos: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return mData.size / 2
    }

    public class ViewHolder(root: View) {
        var _Album1 : View? = null
        var _Album1Name : TextView? = null
        var _Album1Cover : ImageView? = null

        var _Album2 : View? = null
        var _Album2Name : TextView? = null
        var _Album2Cover : ImageView? = null

        init {
            _Album1 = root.findViewById(R.id.album1)
            _Album1Name = _Album1?.findViewById(R.id.album_name)
            _Album1Cover = _Album1?.findViewById(R.id.cover_photo)

            _Album2 = root.findViewById(R.id.album2)
            _Album2Name = _Album2?.findViewById(R.id.album_name)
            _Album2Cover = _Album2?.findViewById(R.id.cover_photo)
        }

        fun bindView(albums : Array<Album?>) {
            if (albums[0] != null) {
                _Album1?.visibility = View.VISIBLE
                _Album1Name?.setText(albums[0]?.mName)
                _Album1Cover?.setImageURI(albums[0]?.mCoverPhoto?.mPictureCache)
            } else {
                _Album1?.visibility = View.INVISIBLE
            }
            if (albums[1] != null) {
                _Album2?.visibility = View.VISIBLE
                _Album2Name?.setText(albums[1]?.mName)
                _Album2Cover?.setImageURI(albums[1]?.mCoverPhoto?.mPictureCache)
            } else {
                _Album2?.visibility = View.INVISIBLE
            }
        }
    }
}
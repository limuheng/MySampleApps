package com.muheng.simplegallery.pageview;

import java.util.ArrayList;
import java.util.List;

import com.muheng.simplegallery.R;
import com.muheng.simplegallery.imagemodel.ImageItem;
import com.muheng.simplegallery.utils.SimpleKeyCode;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class GalleryPageAdapter extends BaseAdapter {
    private final String TAG = GalleryPageAdapter.class.getSimpleName();

    protected Context mContext;
    protected LayoutInflater mInflater;

    private ArrayList<ImageItem> mData = new ArrayList<ImageItem> ();

    private int mSelectedIdx = 0;
    private int mNumColumns;
    private int mItemWidthDp;
    private int mItemHeightDp;

    public GalleryPageAdapter(Context context, LayoutInflater inflater) {
        mContext = context;
        mInflater = inflater;
    }

    @Override
    public int getCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if ((mData != null) && (position < mData.size())) {
            return mData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ImageItem item = (ImageItem) getItem(position);
        View result = null;
        GalleryItemViewHolder viewHolder = null;

        if (convertView != null) {
            Object cacheObj = convertView.getTag();
            if (cacheObj instanceof GalleryItemViewHolder) {
                viewHolder = (GalleryItemViewHolder) convertView.getTag();
                result = convertView;
            }
        }

        if (result == null) {
            result = mInflater.inflate(R.layout.device_image_item, parent, false);
            viewHolder = new GalleryItemViewHolder(result);
            viewHolder.adjustSize(mItemWidthDp, mItemHeightDp);
        }

        result.setTag(viewHolder);

        viewHolder.bindData(item);
        viewHolder.setSelected(mSelectedIdx == position);

        return result;
    }

    public List<ImageItem> getData() {
        return mData;
    }

    public void setNumColumns(int numCol) {
        mNumColumns = numCol;
    }

    public int getNumColumns() {
        return mNumColumns;
    }

    public void setSelectedIdx(int idx) {
        if (idx >= 0 && idx < getCount()) {
            mSelectedIdx = idx;
        } else {
            mSelectedIdx = -1;
        }
    }

    public int getSelectedIdx() {
        return mSelectedIdx;
    }

    public void setItemSize(int widthDp, int heightDp) {
        mItemWidthDp = widthDp;
        mItemHeightDp = heightDp;
    }

    public void moveSelected(int dir) {
        Log.d(TAG, "moveSelected direction: " + dir);
        switch (dir) {
            case SimpleKeyCode.LEFT: {
                if (mSelectedIdx > 0) {
                    mSelectedIdx -= 1;
                } else {
                    mSelectedIdx = 0;
                    //Log.e(TAG, mContext.getString(R.string.left_limit));
                    //ToastUtils.getInstance().showToast(mContext, mContext.getString(R.string.left_limit));
                }
                break;
            }
            case SimpleKeyCode.RIGHT: {
                if (mSelectedIdx < (getCount() - 1)) {
                    mSelectedIdx += 1;
                } else {
                    mSelectedIdx = getCount() - 1;
                    //Log.e(TAG, mContext.getString(R.string.right_limit));
                    //ToastUtils.getInstance().showToast(mContext, mContext.getString(R.string.right_limit));
                }
                break;
            }
            case SimpleKeyCode.UP: {
                //Log.d(TAG, "UP mSelectedIdx: " + mSelectedIdx + ", mNumColumns: " + mNumColumns);
                if ((mSelectedIdx - mNumColumns) >= 0) {
                    mSelectedIdx -= mNumColumns;
                } else {
                    //mSelectedIdx = 0;
                    Log.e(TAG, mContext.getString(R.string.top_limit));
                }
                break;
            }
            case SimpleKeyCode.DOWN: {
                //Log.d(TAG, "DOWN mSelectedIdx: " + mSelectedIdx + ", mNumColumns: " + mNumColumns);
                if ((mSelectedIdx + mNumColumns) < getCount()) {
                    mSelectedIdx += mNumColumns;
                } else {
                    //mSelectedIdx = getCursor().getCount() - 1;
                    Log.e(TAG, mContext.getString(R.string.bottom_limit));
                }
                break;
            }
        }
        notifyDataSetChanged();
    }
}

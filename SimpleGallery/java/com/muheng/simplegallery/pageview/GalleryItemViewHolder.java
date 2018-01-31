package com.muheng.simplegallery.pageview;

import com.muheng.simplegallery.R;
import com.muheng.simplegallery.imagedecoder.ImageDecoderManager;
import com.muheng.simplegallery.imagedecoder.ImageDecodingRequest;
import com.muheng.simplegallery.imagemodel.ImageItem;
import com.muheng.simplegallery.utils.DimenUtils;

import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;

public class GalleryItemViewHolder {

    protected ImageView mNormalView;

    private ImageItem mImageItem;
    protected String mPath;

    public GalleryItemViewHolder(View root) {
        mNormalView = (ImageView) root.findViewById(R.id.normal_preview);
    }

    public void bindData(Cursor cursor) {
        try {
            mImageItem.setData(cursor);
            if (mPath == null || !mPath.equals(mImageItem.getData(ImageItem.IMAGE_PATH_IDX))) {
                mPath = mImageItem.getData()[ImageItem.IMAGE_PATH_IDX];
                bindData(ImageItem.IMAGE_PATH_IDX, mPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bindData(ImageItem item) {
        mImageItem = item;
        if (mPath == null || !mPath.equals(mImageItem.getData(ImageItem.IMAGE_PATH_IDX))) {
            mPath = mImageItem.getData(ImageItem.IMAGE_PATH_IDX);
            bindData(ImageItem.IMAGE_PATH_IDX, mPath);
        }
    }

    public void bindData(int idx, String data) {
        if (idx >= 0 && idx < ImageItem.DATA_SIZE) {
            if (idx == ImageItem.IMAGE_PATH_IDX) {
                if (data != null) {
                    ImageDecoderManager.getInstance().sendRequest(
                            new ImageDecodingRequest(mNormalView, data));
                }
            }
        }
    }

    public String getData(int idx) {
        if (idx >= 0 && idx < ImageItem.DATA_SIZE) {
            return mImageItem.getData(idx);
        }
        return "N/A";
    }

    public String[] getData() {
        return mImageItem.getData();
    }

    public String getPath() {
        return mPath;
    }

    public void setSelected(boolean isSelected) {
        mNormalView.setSelected(isSelected);
        if (mNormalView.isSelected()) {
            mNormalView.setBackgroundResource(R.drawable.img_border_s);
        } else {
            mNormalView.setBackgroundResource(R.drawable.img_border_n);
        }
    }

    public void adjustSize(int widthDp, int heightDp) {
        boolean isChanged = false;
        if (widthDp > 0 && widthDp != mNormalView.getLayoutParams().width) {
            mNormalView.getLayoutParams().width = DimenUtils.dpToPx(widthDp);
            isChanged = true;
        }
        if (heightDp > 0 && heightDp != mNormalView.getLayoutParams().height) {
            mNormalView.getLayoutParams().height = DimenUtils.dpToPx(heightDp);
            isChanged = true;
        }
        if (isChanged) {
            mNormalView.invalidate();
        }
    }
}

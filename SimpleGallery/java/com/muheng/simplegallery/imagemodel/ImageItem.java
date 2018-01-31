package com.muheng.simplegallery.imagemodel;

import android.database.Cursor;
import android.provider.MediaStore.Images.Media;

public class ImageItem {

    public static final int DATA_SIZE = 3;
    public static final int BUCKET_NAME_IDX = 0;
    public static final int IMAGE_PATH_IDX = 1;
    public static final int DATE_TAKEN_IDX = 2;

    private String[] mImageData;

    public ImageItem() {
        mImageData = new String[DATA_SIZE];
    }

    public void setData(Cursor cursor) {
        try {
            mImageData[0] = cursor.getString(cursor.getColumnIndex(Media.BUCKET_DISPLAY_NAME));
            mImageData[1] = cursor.getString(cursor.getColumnIndex(Media.DATA));
            mImageData[2] = cursor.getString(cursor.getColumnIndex(Media.DATE_TAKEN));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getData(int idx) {
        if (mImageData != null && (idx >= 0 && idx < mImageData.length)) {
            return mImageData[idx];
        }
        return null;
    }

    public String[] getData() {
        return mImageData;
    }
}

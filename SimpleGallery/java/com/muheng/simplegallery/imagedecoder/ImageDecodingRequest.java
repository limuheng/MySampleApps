package com.muheng.simplegallery.imagedecoder;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

public class ImageDecodingRequest {

    protected ImageView mImgView;
    protected String mImgPath;

    public ImageDecodingRequest(ImageView view, String path) {
        mImgView = view;
        mImgPath = path;
    }

    public ImageView getImageView() {
        return mImgView;
    }

    public String getImagePath () {
        return mImgPath;
    }

    // This method should be called in UI thread
    // Recycle bitmap before setting a new one
    public void bindView(Bitmap bitmap) {
        if (bitmap != null) {
            BitmapDrawable bd = (BitmapDrawable) mImgView.getDrawable();
            if (bd != null) {
                bd.getBitmap().recycle();
                mImgView.setImageBitmap(null);
            }
            mImgView.setImageBitmap(bitmap);
        }
    }
}

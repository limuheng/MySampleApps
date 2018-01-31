package com.example.android.customviews.views;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.annotation.NonNull;

public class CustomBitmapDrawable extends CustomDrawable {
    private static final String TAG = CustomBitmapDrawable.class.getSimpleName();

    private Bitmap mBitmap;

    public void setBitmap(Bitmap bmp) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) mWidth / w);
        float scaleHeight = ((float) mHeight / h);
        matrix.postScale(scaleWidth, scaleHeight);
        mBitmap = Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0, 0, mPaint);
        }
    }
}

package com.muheng.simplegallery.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

public class BitmapUtils {
    public static void setImageViewBitmap(ImageView view, Bitmap bitmap) {
        if (view != null && bitmap != null) {
            BitmapDrawable bd = (BitmapDrawable) view.getDrawable();
            if (bd != null) {
                bd.getBitmap().recycle();
                view.setImageBitmap(null);
            }
            view.setImageBitmap(bitmap);
        }
    }
    public static Bitmap downSamplingBitmap(String filePath, int inSampleSize) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = inSampleSize;
            bitmap = BitmapFactory.decodeFile(filePath, options);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}

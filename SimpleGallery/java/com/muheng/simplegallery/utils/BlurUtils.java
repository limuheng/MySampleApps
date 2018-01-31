package com.muheng.simplegallery.utils;

/**
 * References: 
 * 1. http://stackoverflow.com/questions/6795483/create-blurry-transparent-background-effect
 * 2. http://stackoverflow.com/questions/23294489/android-convert-current-screen-to-bitmap
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

public class BlurUtils {
    //private static final String TAG = BlurUtils.class.getSimpleName();

    private static final float BITMAP_SCALE = 0.3f;
    private static final float BLUR_RADIUS = 15.0f;

    public static Bitmap blur(View v) {
        return blur(v.getContext(), getScreenshot(v));
    }

    public static Bitmap blur(Context ctx, Bitmap image) {
        int width = Math.round(image.getWidth() * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(ctx);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }

    public static Bitmap getScreenshot(View view) {
        // configuramos para que la view almacene la cache en una imagen
        View root = view.getRootView();
        root.setDrawingCacheEnabled(true);
        root.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        root.buildDrawingCache();

        if(root.getDrawingCache() == null) return null; // Verificamos antes de que no sea null

        // utilizamos esa cache, para crear el bitmap que tendra la imagen de la view actual
        Bitmap snapshot = Bitmap.createBitmap(root.getDrawingCache());
        root.setDrawingCacheEnabled(false);
        root.destroyDrawingCache();

        return snapshot;
    }
}

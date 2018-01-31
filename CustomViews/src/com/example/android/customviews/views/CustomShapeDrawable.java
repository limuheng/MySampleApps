package com.example.android.customviews.views;

import android.graphics.Canvas;
import android.support.annotation.NonNull;

public class CustomShapeDrawable extends CustomDrawable {
    @Override
    public void draw(@NonNull Canvas canvas) {
        try {
            canvas.drawOval(0, 0, mWidth, mHeight, mPaint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

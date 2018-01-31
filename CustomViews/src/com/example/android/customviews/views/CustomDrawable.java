package com.example.android.customviews.views;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public abstract class CustomDrawable extends Drawable {
    private static final String TAG = CustomDrawable.class.getSimpleName();

    protected Paint mPaint = new Paint();
    protected float mX;
    protected float mY;

    protected int mWidth = 200;
    protected int mHeight = 200;

    public CustomDrawable() {
        setBounds(0, 0, mWidth, mHeight);
    }

    @Override
    public void draw(@NonNull Canvas canvas) { }

    @Override
    public void setAlpha(int i) { }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) { }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    public Paint getPaint() {
        return mPaint;
    }

    public float getX() {
        return mX;
    }

    public float getY() {
        return mY;
    }

    public void setX(float x) {
        mX = x;
    }

    public void setY(float y) {
        mY = y;
    }

    public void setXY(float x, float y) {
        mX = x;
        mY = y;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

}

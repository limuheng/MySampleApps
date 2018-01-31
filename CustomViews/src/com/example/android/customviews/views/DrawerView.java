package com.example.android.customviews.views;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DrawerView extends View {

    ArrayList<CustomDrawable> mCustomDrawables = new ArrayList<CustomDrawable> ();

    private boolean mIsDragging = false;
    private WeakReference<CustomDrawable> mDraggingDrawable = null;

    public DrawerView(Context context) {
        super(context);
    }

    public DrawerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public DrawerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private int mLastX = 0;
    private int mLastY = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchX = (int) event.getX(); //(int)event.getRawX();
        int touchY = (int) event.getY(); //(int)event.getRawY();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                CustomDrawable customDrawable = getSelectedCustomDrawable(touchX, touchY);
                if (customDrawable != null) {
                    mIsDragging = true;
                    mDraggingDrawable = new WeakReference<CustomDrawable> (customDrawable);
                    mLastX = touchX;
                    mLastY = touchY;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                //Log.d("ShapeDrawer", "ACTION_MOVE");
                if (mIsDragging && mDraggingDrawable.get() != null) {
                    int dX = touchX - mLastX;
                    int dY = touchY - mLastY;
                    float x = mDraggingDrawable.get().getX() + dX;
                    float y = mDraggingDrawable.get().getY() + dY;
                    mDraggingDrawable.get().setXY(x, y);
                    mLastX = touchX;
                    mLastY = touchY;
                    //ShapeDrawer.this.invalidate();
                    DrawerView.this.invalidate(mDraggingDrawable.get().getBounds());
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                mIsDragging = false;
                mLastX = 0;
                mLastY = 0;
                break;
            }
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (CustomDrawable drawable : mCustomDrawables) {
            if (drawable != null) {
                float dx = drawable.getX();
                float dy = drawable.getY();
                canvas.save();
                canvas.translate(dx, dy);
                drawable.draw(canvas);
                canvas.restore();
            }
        }
    }

    public void addCustomDrawable(CustomDrawable customDrawable) {
        float dx = (float) (Math.random() * (getWidth() + 1));
        float dy = (float) (Math.random() * (getHeight() + 1));
        //Log.d("ShapeDrawer", "width: " + getWidth() + ", height: " + getHeight());
        //Log.d("ShapeDrawer", "dx: " + dx + ", dy: " + dy);
        if ((dx + customDrawable.getWidth()) > getWidth()) {
            dx = getWidth() - customDrawable.getWidth();
        }
        if ((dy + customDrawable.getHeight()) > getHeight()) {
            dy = getHeight() - customDrawable.getHeight();
        }
        //Log.d("ShapeDrawer", "1 dx: " + dx + ", 2 dy: " + dy);
        customDrawable.setX(dx);
        customDrawable.setY(dy);
        mCustomDrawables.add(customDrawable);
        invalidate();
    }

    public void clearAll() {
        mCustomDrawables.clear();
        invalidate();
    }

    private CustomDrawable getSelectedCustomDrawable(int touchX, int touchY) {
        int size = mCustomDrawables.size();
        CustomDrawable customDrawable = null;
        for (int i = size - 1; i >= 0; i--) {
            customDrawable = mCustomDrawables.get(i);
            Rect rect = new Rect((int)customDrawable.getX(), (int)customDrawable.getY(),
                                  (int)(customDrawable.getX() + customDrawable.getWidth()),
                                  (int)(customDrawable.getY() + customDrawable.getHeight()));
            if (rect.contains(touchX, touchY)) {
                Log.d("ShapeDrawer", "selected index: " + i);
                return customDrawable;
            }
        }
        return null;
    }
}

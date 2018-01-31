package com.muheng.simplegallery.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ToastUtils {
    private final String TAG = ToastUtils.class.getSimpleName();

    public static final int TOAST_DELAY = 1000;

    private static ToastUtils sInstance;

    public static ToastUtils getInstance() {
        if (sInstance == null) {
            sInstance = new ToastUtils();
        }
        return sInstance;
    }

    private Toast mToast;
    private String mMessage;
    private Handler mHandler;

    private ToastUtils() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void showToast(Context context, String msg) {
        if (msg == null || msg.isEmpty()) {
            return ;
        }
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else if (!msg.equals(mMessage)) {
            mToast.setText(msg);
            mMessage = msg;
        }
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mToast != null) {
                    mToast.cancel();
                    mToast = null;
                }
            }
        }, TOAST_DELAY);
    }
}

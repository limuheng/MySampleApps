package com.hank.nativelibtest;

import android.util.Log;

public class NativeLib {
    static {
        System.loadLibrary("NativeLibTest");
    }

    public static final String TAG = "NativeLib";

    private native String nativeSayHello();

    public void sayHello() {
        Log.d(TAG, nativeSayHello());
    }
}

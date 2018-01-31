package com.muheng.simplegallery.imagedecoder;

import android.os.Handler;

public class ImageDecoderManager {
    private final String TAG = ImageDecoderManager.class.getSimpleName();

    private static final int MAX_THREAD_COUNT = 2;
    private static ImageDecoderManager sInstance;

    public static ImageDecoderManager getInstance() {
        if (sInstance == null) {
            sInstance = new ImageDecoderManager();
        }
        return sInstance;
    }

    public static void releaseInstance() {
        if (sInstance != null) {
            sInstance.release();
            sInstance = null;
        }
    }

    private Handler mHandler;
    private ImageDecodingRequestQueue mQueue;
    private ImageDecodingThread[] mThreads;

    private ImageDecoderManager() {
        mHandler = new Handler();
        mQueue = ImageDecodingRequestQueue.getInstance();
        mThreads = new ImageDecodingThread[MAX_THREAD_COUNT];
        for (int i = 0; i < mThreads.length; i++) {
            mThreads[i] = new ImageDecodingThread(mHandler);
            mThreads[i].start();
        }
    }

    public synchronized void sendRequest(ImageDecodingRequest request) {
        try {
            mQueue.enqueue(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void release() {
        if (mThreads != null) {
            for (int i = 0; i < mThreads.length; i++) {
                mThreads[i].interrupt();
                mThreads[i] = null;
            }
            mThreads = null;
        }
        mQueue = null;
    }
}

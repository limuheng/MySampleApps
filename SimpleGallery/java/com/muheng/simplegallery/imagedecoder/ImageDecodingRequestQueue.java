package com.muheng.simplegallery.imagedecoder;

import java.util.ArrayList;
import java.util.List;

public class ImageDecodingRequestQueue {

    public static final int DEFAULT_MAX_SIZE = 15;
    public static final int LIMIT_MAX_SIZE = 100;

    private static int sMaxQueueSize = DEFAULT_MAX_SIZE;

    private static ImageDecodingRequestQueue sInstance;

    public synchronized static ImageDecodingRequestQueue getInstance() {
        if (sInstance == null) {
            sInstance = new ImageDecodingRequestQueue();
        }
        return sInstance;
    }

    public synchronized static void release() {
        sInstance = null;
    }

    public synchronized static void setMaxQueueSize(int size) {
        if (size >= 0 && size <= LIMIT_MAX_SIZE) {
            sMaxQueueSize = size;
        } else if (size > LIMIT_MAX_SIZE) {
            sMaxQueueSize = LIMIT_MAX_SIZE;
        }
    }

    private List<ImageDecodingRequest> mQueue = new ArrayList<ImageDecodingRequest> ();

    public synchronized void enqueue(ImageDecodingRequest request) {
        if (mQueue.size() >= sMaxQueueSize) {
            mQueue.remove(0);
        }
        mQueue.add(request);
        notify();
    }

    public synchronized ImageDecodingRequest dequeue() {
        while (mQueue.size() <= 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return mQueue.remove(mQueue.size() - 1);
    }
}

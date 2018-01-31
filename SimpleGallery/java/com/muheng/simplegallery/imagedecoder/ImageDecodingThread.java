package com.muheng.simplegallery.imagedecoder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

public class ImageDecodingThread extends Thread {
    private final String TAG = ImageDecodingThread.class.getSimpleName();

    private Handler mHandler;
    private ImageDecodingRequestQueue mQueue;


    // Fix UI scrolling lag issue, this method is supposed to be called in a background thread
    private static Bitmap decodeImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 16;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    public ImageDecodingThread(Handler handler) {
        mHandler = handler;
        mQueue = ImageDecodingRequestQueue.getInstance();
    }

    @Override
    public void run() {
        while (!isInterrupted() && mQueue != null) {
            final ImageDecodingRequest request = mQueue.dequeue();
            if (request.getImageView() == null) {
                break;
            } else {
                final Bitmap bmp = decodeImage(request.getImagePath());
                if (bmp != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            request.bindView(bmp);
                        }
                    });
                }
            }
        }
        mHandler = null;
        mQueue = null;
    }

}

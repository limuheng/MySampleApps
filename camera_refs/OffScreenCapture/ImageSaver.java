/**
 * Created by NVT03407
 * Off screen capture 20160607
 */

package com.nvt.fprlinker.OffScreenCapture;

import android.media.Image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

class ImageSaver implements Runnable {

    /**
     * The JPEG image
     */
    private final Image mImage;
    /**
     * The file we save the image into.
     */
    private final File mFile;

    private int mRotation;

    public ImageSaver(Image image, File file, int rotation) {
        mImage = image;
        mFile = file;
        mRotation = rotation;
    }

    @Override
    public void run() {
        ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(mFile);
            output.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mImage.close();
            if (null != output) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // The camera on the development board has no sensor so that the rotation can't be calculated correctly.
        // Here we just hard-code the 270 (-90) to make the user take the camera in portrait
        //boolean rotateResult = CameraUtils.rotatePicture(mFile.getPath(), /*mRotation*/-90);
        boolean rotateResult = CameraUtils.setPhotoExifOrientation(mFile.getPath(), /*mRotation*/270);
    }
}
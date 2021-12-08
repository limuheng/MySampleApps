package com.hank.camera2test.camera2;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.util.Size;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

public interface ICameraController {
    class Factory {
        public static ICameraController createCameraController(
                @NonNull Context ctx,
                @NonNull WindowManager windowManager,
                @NonNull Handler handler) {
            return new CameraController(ctx, windowManager, handler);
        }
    }

    void setUpOutputDir(@NonNull File dir);
    @Nullable
    String getFrontCamId();
    @Nullable
    String getBackCamId();
    void openCamera(String camId, int width, int height);
    void createSession(SurfaceTexture surfaceTexture);
    void startPreview();
    void stopPreview();
    void closeCamera();
    Size getPreviewSize();
    void takePicture();
    void startRecording();
    void stopRecording();
    boolean isRecording();
}

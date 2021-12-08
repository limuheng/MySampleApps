package com.hank.camera2test.recording;

import android.util.Size;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

public interface IVideoRecorder {
    void setDirectory(@NonNull File dir);
    @Nullable File getDirectory();
    void setSize(@NonNull Size size);
    @Nullable Size getSize();
    Surface init() throws IllegalArgumentException, IllegalStateException;
    void configure() throws IllegalStateException;
    void start(@NonNull File targetDir) throws IllegalStateException;
    @NonNull String stop() throws IllegalStateException;
    void release() throws IllegalStateException;
}

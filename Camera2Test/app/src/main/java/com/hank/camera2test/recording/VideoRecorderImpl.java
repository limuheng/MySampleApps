package com.hank.camera2test.recording;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class VideoRecorderImpl implements IVideoRecorder {
    private static final String TAG = "VideoRecorder";

    private File mDirectory;
    private Size mSize;

    private State mState = State.UNINITIALIZED;

    private MediaCodec mMediaEncoder;
    private MediaFormat mFormat;

    private Surface mInputSurface;

    private String mTempFilePath;
    private String mOutputFilePath;

    private AtomicBoolean mStopRecording = new AtomicBoolean(true);

    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

    @Override
    public void setDirectory(@NonNull File dir) {
        mDirectory = dir;
    }

    @Nullable
    @Override
    public File getDirectory() {
        return mDirectory;
    }

    @Override
    public void setSize(@NonNull Size size) {
        mSize = size;
    }

    @Nullable
    @Override
    public Size getSize() {
        return mSize;
    }

    @Override
    public Surface init() throws IllegalStateException {
        if (mState != State.UNINITIALIZED) {
            throw new IllegalStateException("Incorrect state: " + mState.name() + " at init()");
        }

        if (mDirectory == null) {
            throw new IllegalArgumentException("Directory is not set!");
        }

        if (mSize == null) {
            throw new IllegalArgumentException("Size is not set!");
        }

        mInputSurface = MediaCodec.createPersistentInputSurface();

        mFormat = MediaFormat.createVideoFormat("video/avc", mSize.getWidth(), mSize.getHeight());
        mFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        mFormat.setInteger(MediaFormat.KEY_BIT_RATE, 3 * mSize.getWidth() * mSize.getHeight());
        mFormat.setFloat(MediaFormat.KEY_FRAME_RATE, 30.0f);
        mFormat.setFloat(MediaFormat.KEY_I_FRAME_INTERVAL, 1.0f);

        try {
            mMediaEncoder = MediaCodec.createEncoderByType("video/avc");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //mTempFilePath = mDirectory.getPath() + File.pathSeparatorChar + "tmp.mp4";

        mState = State.INITIALIZED;

        // Pre-configure at initialization
        mMediaEncoder.configure(mFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mMediaEncoder.setInputSurface(mInputSurface);

        mState = State.CONFIGURED;

        return mInputSurface;
    }

    @Override
    public void configure() throws IllegalStateException {
        if (mState != State.INITIALIZED && mState != State.STOPPED) {
            throw new IllegalStateException("Incorrect state: " + mState.name() + " at configure()");
        }
        mMediaEncoder.configure(mFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mMediaEncoder.setInputSurface(mInputSurface);
        mState = State.CONFIGURED;
    }

    @Override
    public void start(@NonNull final File targetDir) throws IllegalStateException {
        if (mState != State.CONFIGURED) {
            throw new IllegalStateException("Incorrect state: " + mState.name() + " at start()");
        }
        mState = State.STARTED;
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                // Do recording work
                recording(targetDir);
                // pre-configure for next recording
                configure();
            }
        });
    }

    @Override
    public String stop() throws IllegalStateException {
        if (mState != State.STARTED) {
            throw new IllegalStateException("Incorrect state: " + mState.name() + " at stop()");
        }
        mMediaEncoder.signalEndOfInputStream();
        mStopRecording.compareAndSet(false, true);
        mState = State.STOPPED;
        return mOutputFilePath;
    }

    @Override
    public void release() throws IllegalStateException {
        if (mState != State.CONFIGURED && mState != State.STOPPED) {
            throw new IllegalStateException("Incorrect state: " + mState.name() + " at release()");
        }
        if (mMediaEncoder != null) {
            mMediaEncoder.release();
            mMediaEncoder = null;
        }
        if (mExecutor != null) {
            mExecutor.shutdown();
            mExecutor = null;
        }
        mState = State.RELEASED;
    }

    private void recording(File outputDir) {
        if (mState != State.STARTED) {
            throw new IllegalStateException("Incorrect state: " + mState.name() + " at recording()");
        }
        // Create MdieaMuxer to write encoded data to file
        MediaMuxer mediaMuxer;
        try {
            File file = new File(outputDir, "Camera2_" + mDateFormat.format(new Date()) + ".mp4");
            mOutputFilePath = file.getPath();
            Log.d(TAG, "recording: " + mOutputFilePath);
            mediaMuxer = new MediaMuxer(file.getPath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            Log.d(TAG, "Failed to create MediaMuxer, stop recording...");
            e.printStackTrace();
            return ;
        }

        Log.d(TAG, "Start recording...");
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int videoTrackIdx = -1;
        boolean isMuxerStarted = false;

        mStopRecording.compareAndSet(true, false);
        mMediaEncoder.start();
        mMediaEncoder.flush();

        while (!mStopRecording.get()) {
            int outputBufferIdx = mMediaEncoder.dequeueOutputBuffer(bufferInfo, -1);
            if (outputBufferIdx >= 0) {
                ByteBuffer outputBuffer = mMediaEncoder.getOutputBuffer(outputBufferIdx);
                if (outputBuffer != null) {
                    mediaMuxer.writeSampleData(videoTrackIdx, outputBuffer, bufferInfo);
                } else {
                    Log.e(TAG, "outputBuffer is null while encoding");
                    mStopRecording.compareAndSet(false, true);
                }
                // outputBuffer is ready to be processed or rendered.
                mMediaEncoder.releaseOutputBuffer(outputBufferIdx, false);
            } else if (outputBufferIdx == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                Log.v(TAG, "Output format change. API Level > 21 can ignore this.");
                // Subsequent data will conform to new format.
                // Can ignore if using getOutputFormat(outputBufferId)
                MediaFormat format = mMediaEncoder.getOutputFormat();
                Log.v(TAG, "Adding video track " + format);
                videoTrackIdx = mediaMuxer.addTrack(format);
                Log.v(TAG, "MediaMuxer start");
                mediaMuxer.start();
                isMuxerStarted = true;
            } else if (outputBufferIdx == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                Log.v(TAG, "Output buffers changed. API Level > 21 can ignore this.");
            } else {
                Log.e(TAG, "Un-defined dequeueOutputBuffer() error: " + outputBufferIdx);
            }
        }

        if (isMuxerStarted) {
            mediaMuxer.stop();
            mediaMuxer.release();
        }

        mMediaEncoder.flush();
        mMediaEncoder.stop();
        mMediaEncoder.reset();
        Log.d(TAG, "Stop recording...");

//        Log.d(TAG, "Rename recording file to " + outputFile);
//        if (!new File(mTempFilePath).renameTo(new File(outputFile))) {
//            Log.e(TAG, "Rename recording file failed: " + outputFile);
//        }
    }

    private enum State {
        UNINITIALIZED,
        INITIALIZED,
        CONFIGURED,
        STARTED,
        STOPPED,
        RELEASED
    }
}

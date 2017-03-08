package com.m3b.rbrecoderlib;

import android.annotation.TargetApi;
import android.opengl.EGL14;

import com.m3b.rbrecoderlib.encoder.EglCore;
import com.m3b.rbrecoderlib.encoder.MediaAudioEncoder;
import com.m3b.rbrecoderlib.encoder.MediaEncoder;
import com.m3b.rbrecoderlib.encoder.MediaMuxerWrapper;
import com.m3b.rbrecoderlib.encoder.MediaVideoEncoder;
import com.m3b.rbrecoderlib.encoder.WindowSurface;

import java.io.IOException;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

@TargetApi(18)
public class GPUImageMovieWriter extends GPUImageFilter {
    /**
     * callback methods from encoder
     */
    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
        }

        @Override
        public void onMuxerStopped() {
        }
    };
    private MediaMuxerWrapper mMuxer;
    private MediaVideoEncoder mVideoEncoder;
    private MediaAudioEncoder mAudioEncoder;
    private WindowSurface mCodecInput;
    private EGLSurface mEGLScreenSurface;
    private EGL10 mEGL;
    private EGLDisplay mEGLDisplay;
    private EGLContext mEGLContext;
    private EglCore mEGLCore;
    private boolean mIsRecording = false;

    @Override
    public void onInit() {
        super.onInit();
        mEGL = (EGL10) EGLContext.getEGL();
        mEGLDisplay = mEGL.eglGetCurrentDisplay();
        mEGLContext = mEGL.eglGetCurrentContext();
        mEGLScreenSurface = mEGL.eglGetCurrentSurface(EGL10.EGL_DRAW);
    }

    @Override
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        // Draw on screen surface
        super.onDraw(textureId, cubeBuffer, textureBuffer);

        if (mIsRecording) {
            // create encoder surface
            if (mCodecInput == null) {
                mEGLCore = new EglCore(EGL14.eglGetCurrentContext(), EglCore.FLAG_RECORDABLE);
                mCodecInput = new WindowSurface(mEGLCore, mVideoEncoder.getSurface(), false);
            }

            // Draw on encoder surface
            mCodecInput.makeCurrent();
            super.onDraw(textureId, cubeBuffer, textureBuffer);
            if (mCodecInput != null) {
                mCodecInput.swapBuffers();
            }
            if (mVideoEncoder != null) {
                mVideoEncoder.frameAvailableSoon();
            }
        }

        // Make screen surface be current surface
        mEGL.eglMakeCurrent(mEGLDisplay, mEGLScreenSurface, mEGLScreenSurface, mEGLContext);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseEncodeSurface();
    }

    public void startRecording(final String outputPath, final String level, final int rotationRecord) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                if (mIsRecording) {
                    return;
                }

                int width = 0;
                int height = 0;

                /*
                * low 480 640
                * meduim 600 800
                * high 960 1280
                * veryhigh 1080 1920
                 * */

                switch (level) {
                    case "low":
                        width = 480;
                        height = 640;
                        break;

                    case "meduim":
                        width = 600;
                        height = 800;
                        break;

                    case "high":
                        width = 960;
                        height = 1280;
                        break;

                    case "veryhigh":
                        width = 1080;
                        height = 1920;
                        break;

                    default:
                        break;
                }

                try {
                    mMuxer = new MediaMuxerWrapper(outputPath, rotationRecord);

                    // for video capturing
                    mVideoEncoder = new MediaVideoEncoder(mMuxer, mMediaEncoderListener, width, height);
                    // for audio capturing
                    mAudioEncoder = new MediaAudioEncoder(mMuxer, mMediaEncoderListener);

                    mMuxer.prepare();
                    mMuxer.startRecording();

                    mIsRecording = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void stopRecording() {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                if (!mIsRecording) {
                    return;
                }

                mMuxer.stopRecording();
                mIsRecording = false;
                releaseEncodeSurface();
            }
        });
    }

    private void releaseEncodeSurface() {
        if (mEGLCore != null) {
            mEGLCore.makeNothingCurrent();
            mEGLCore.release();
            mEGLCore = null;
        }

        if (mCodecInput != null) {
            mCodecInput.release();
            mCodecInput = null;
        }
    }
}

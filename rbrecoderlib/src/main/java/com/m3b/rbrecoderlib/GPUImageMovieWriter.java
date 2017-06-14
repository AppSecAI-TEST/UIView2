package com.m3b.rbrecoderlib;

import android.annotation.TargetApi;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.util.Log;


import java.io.IOException;
import java.nio.FloatBuffer;


import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import com.m3b.rbrecoderlib.encoder.*;

@TargetApi(18)
public class GPUImageMovieWriter extends GPUImageFilter {
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
        GLES20.glViewport(0, 0,  getOutputWidth(),  getOutputHeight());//TODO FIX MEIZU BUG to fix previewscreen size
        super.onDraw(textureId, cubeBuffer, textureBuffer);

        if (mIsRecording) {

            // create encoder surface
            if (mCodecInput == null) {
                mEGLCore = new EglCore(EGL14.eglGetCurrentContext(), EglCore.FLAG_RECORDABLE);
                mCodecInput = new WindowSurface(mEGLCore, mVideoEncoder.getSurface(), true);
            }


                // Draw on encoder surface
                mCodecInput.makeCurrent();
                GLES20.glViewport(0, 0,  mCodecInput.getWidth(), mCodecInput.getHeight());//TODO FIX MEIZU BUG to fix recordvideo size
                super.onDraw(textureId, cubeBuffer, textureBuffer);

            if(mCodecInput != null) {//FIXME somedevice make crash maybe mCodecInput has been release too soon
                mEGLCore.logCurrent("+++");
                mCodecInput.swapBuffers();
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


    public void startRecording(final String outputPath, final Level level, final int rotationRecord) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                if (mIsRecording) {
                    return;
                }


                try {
                    mMuxer = new MediaMuxerWrapper(outputPath, rotationRecord);

                    // for video capturing
                    mVideoEncoder = new MediaVideoEncoder(mMuxer, mMediaEncoderListener, level.getWidth(), level.getHeight());
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


    public enum Level {
        LOW(360, 640), MEDIUM(540, 960), HIGH(720, 1280), VERYHIGH(1080, 1920),LOW_CROP(480, 480), MEDIUM_CROP(600, 600), HIGH_CROP(720, 720), VERYHIGH_CROP(1080, 1080);

        private int width;
        private int height;

        Level(int _width, int _height) {
            this.width = _width;
            this.height = _height;
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }
    }

}

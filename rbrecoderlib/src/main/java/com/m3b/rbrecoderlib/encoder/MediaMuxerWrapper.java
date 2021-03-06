package com.m3b.rbrecoderlib.encoder;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

@TargetApi(18)
public class MediaMuxerWrapper {
    private static final boolean DEBUG = false;
    private static final String TAG = "MediaMuxerWrapper";

    private String mOutputPath;
    private final MediaMuxer mMediaMuxer;    // API >= 18
    private int mEncoderCount, mStatredCount;
    private boolean mIsStarted;
    private MediaEncoder mVideoEncoder, mAudioEncoder;



    public  MediaMuxerWrapper(String outputPath, int rotationRecord) throws IOException {
        mOutputPath = outputPath;
        mMediaMuxer = new MediaMuxer(mOutputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

        mMediaMuxer.setOrientationHint(rotationRecord);//TODO orientationhint
        Log.d("++++","rotation: "+rotationRecord);

        mEncoderCount = mStatredCount = 0;
        mIsStarted = false;
    }

    public String getOutputPath() {
        return mOutputPath;
    }

    public void prepare() throws IOException {
        if (mVideoEncoder != null)
            mVideoEncoder.prepare();
        if (mAudioEncoder != null)
            mAudioEncoder.prepare();
    }

    public void startRecording() {
        if (mVideoEncoder != null)
            mVideoEncoder.startRecording();
        if (mAudioEncoder != null)
            mAudioEncoder.startRecording();
    }

    public void stopRecording() {
        if (mVideoEncoder != null)
            mVideoEncoder.stopRecording();
        mVideoEncoder = null;
        if (mAudioEncoder != null)
            mAudioEncoder.stopRecording();
        mAudioEncoder = null;
    }

    public synchronized boolean isStarted() {
        return mIsStarted;
    }

//**********************************************************************
//**********************************************************************
    /**
     * assign encoder to this calss. this is called from encoder.
     * @param encoder instance of MediaVideoEncoder or MediaAudioEncoder
     */
    /*package*/ void addEncoder(final MediaEncoder encoder) {
        if (encoder instanceof MediaVideoEncoder) {
            if (mVideoEncoder != null)
                throw new IllegalArgumentException("Video encoder already added.");
            mVideoEncoder = encoder;
        } else if (encoder instanceof MediaAudioEncoder) {
            if (mAudioEncoder != null)
                throw new IllegalArgumentException("Video encoder already added.");
            mAudioEncoder = encoder;
        } else
            throw new IllegalArgumentException("unsupported encoder");
        mEncoderCount = (mVideoEncoder != null ? 1 : 0) + (mAudioEncoder != null ? 1 : 0);
    }

    /**
     * request start recording from encoder
     * @return true when muxer is ready to write
     */
    /*package*/ synchronized boolean start() {
        if (DEBUG) Log.v(TAG,  "start:");
        mStatredCount++;
        if ((mEncoderCount > 0) && (mStatredCount == mEncoderCount)) {
            mMediaMuxer.start();
            mIsStarted = true;
            notifyAll();
            if (DEBUG) Log.v(TAG,  "MediaMuxer started:");
        }
        return mIsStarted;
    }

    /**
     * request stop recording from encoder when encoder received EOS
    */
    /*package*/ synchronized boolean stop() {
        if (DEBUG) Log.v(TAG,  "stop:mStatredCount=" + mStatredCount);
        mStatredCount--;
        if ((mEncoderCount > 0) && (mStatredCount <= 0) && mMediaMuxer!= null) {
            mMediaMuxer.stop();
            mMediaMuxer.release();
            mIsStarted = false;
            if (DEBUG) Log.v(TAG,  "MediaMuxer stopped:");
            return true;
        }
        return false;
    }

    /**
     * assign encoder to muxer
     * @param format
     * @return minus value indicate error
     */
    /*package*/ synchronized int addTrack(final MediaFormat format) {
        if (mIsStarted)
            throw new IllegalStateException("muxer already started");
        final int trackIx = mMediaMuxer.addTrack(format);
        if (DEBUG) Log.i(TAG, "addTrack:trackNum=" + mEncoderCount + ",trackIx=" + trackIx + ",format=" + format);
        return trackIx;
    }

    /**
     * write encoded data to muxer
     * @param trackIndex
     * @param byteBuf
     * @param bufferInfo
     */
    /*package*/ synchronized void writeSampleData(final int trackIndex, final ByteBuffer byteBuf, final MediaCodec.BufferInfo bufferInfo) {
        if (mStatredCount > 0)
            mMediaMuxer.writeSampleData(trackIndex, byteBuf, bufferInfo);
    }

    synchronized void removeFailEncoder() {
        mEncoderCount--;

        if ((mEncoderCount > 0) && (mStatredCount == mEncoderCount)) {
            mMediaMuxer.start();
            mIsStarted = true;
            notifyAll();
            if (DEBUG) Log.v(TAG,  "MediaMuxer force start");
        }
    }

}

package com.m3b.rbvideolib.FFmpeg;

public interface FFmpegLoadBinaryResponseHandler extends ResponseHandler {

    /**
     * on Fail
     */
    public void onFailure();

    /**
     * on Success
     */
    public void onSuccess();

}

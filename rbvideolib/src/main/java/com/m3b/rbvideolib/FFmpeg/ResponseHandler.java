package com.m3b.rbvideolib.FFmpeg;

abstract interface ResponseHandler {

    /**
     * on Start
     */
    public void onStart();

    /**
     * on Finish
     */
    public void onFinish();

}

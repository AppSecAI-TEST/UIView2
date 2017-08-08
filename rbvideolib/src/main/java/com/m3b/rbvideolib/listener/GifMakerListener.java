package com.m3b.rbvideolib.listener;

/**
 * Created by m3b on 17/8/4.
 */

public interface GifMakerListener {
    public void onExecStart();
    public void onExecSuccess(String message);
    public void onExecFail(String reason);
    public void onExecProgress(String message);
}

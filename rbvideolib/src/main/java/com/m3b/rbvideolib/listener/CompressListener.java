package com.m3b.rbvideolib.listener;


public interface CompressListener {
    public void onExecStart();
    public void onExecSuccess(String message);
    public void onExecFail(String reason);
    public void onExecProgress(String message);
}

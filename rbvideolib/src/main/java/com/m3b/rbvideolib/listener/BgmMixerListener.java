package com.m3b.rbvideolib.listener;

public interface BgmMixerListener {
    public void onExecStart();
    public void onExecSuccess(String message);
    public void onExecFail(String reason);
    public void onExecProgress(String message);
}

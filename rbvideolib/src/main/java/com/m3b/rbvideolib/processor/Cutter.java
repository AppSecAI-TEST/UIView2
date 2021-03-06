package com.m3b.rbvideolib.processor;

import android.app.Activity;

import com.m3b.rbvideolib.FFmpeg.FFmpeg;
import com.m3b.rbvideolib.FFmpeg.FFmpegExecuteResponseHandler;
import com.m3b.rbvideolib.FFmpeg.LoadBinaryResponseHandler;
import com.m3b.rbvideolib.exceptions.FFmpegCommandAlreadyRunningException;
import com.m3b.rbvideolib.exceptions.FFmpegNotSupportedException;
import com.m3b.rbvideolib.listener.CutterListener;
import com.m3b.rbvideolib.listener.InitListener;

/**
 * Created by m3b on 17/6/27.
 */

public class Cutter {
    public Activity a;
    public FFmpeg ffmpeg;
    public Cutter(Activity activity){
        a = activity;
        ffmpeg = FFmpeg.getInstance(a);
    }

    public void loadBinary(final InitListener mListener) {
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onStart() {}

                @Override
                public void onFailure() {
                    mListener.onLoadFail("incompatible with this device");
                }

                @Override
                public void onSuccess() {
                    mListener.onLoadSuccess();
                }
                @Override
                public void onFinish() {

                }
            });
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public void execCommand(String[] cmd, final CutterListener mListener){
        try {
            ffmpeg.execute(cmd, new FFmpegExecuteResponseHandler() {

                @Override
                public void onStart() {}

                @Override
                public void onProgress(String message) { mListener.onExecProgress(message);}

                @Override
                public void onFailure(String message) { mListener.onExecFail(message); }

                @Override
                public void onSuccess(String message) {
                    mListener.onExecSuccess(message);
                }

                @Override
                public void onFinish() {}
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }

    public boolean killRunningProcesses() {
        return ffmpeg.killRunningProcesses();
    }
}

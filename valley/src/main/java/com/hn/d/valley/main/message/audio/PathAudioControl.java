package com.hn.d.valley.main.message.audio;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.angcyo.uiview.utils.storage.StorageUtil;
import com.hn.d.valley.R;
import com.netease.nimlib.sdk.media.player.AudioPlayer;

public class PathAudioControl extends BaseAudioControl<AudioRecordPlayable> {
    private static PathAudioControl pathAudioControl = null;

    private PathAudioControl(Context context) {
        super(context, true);
    }

    public static PathAudioControl getInstance(Context context) {
        if (pathAudioControl == null) {
            synchronized (PathAudioControl.class) {
                if (pathAudioControl == null) {
                    pathAudioControl = new PathAudioControl(context);
                }
            }
        }
        return pathAudioControl;
    }

    @Override
    protected void setOnPlayListener(Playable playingPlayable, AudioControlListener audioControlListener) {
        this.audioControlListener = audioControlListener;

        BasePlayerListener basePlayerListener = new BasePlayerListener(currentAudioPlayer, playingPlayable) {

            @Override
            public void onInterrupt() {
                if (!checkAudioPlayerValid()) {
                    return;
                }

                super.onInterrupt();
            }

            @Override
            public void onError(String error) {
                if (!checkAudioPlayerValid()) {
                    return;
                }

                super.onError(error);
            }

            @Override
            public void onCompletion() {
                if (!checkAudioPlayerValid()) {
                    return;
                }

                resetAudioController(listenerPlayingPlayable);

                boolean isLoop = false;
                if (!isLoop) {
                    if (audioControlListener != null) {
                        audioControlListener.onEndPlay(currentPlayable);
                    }
                    playSuffix();
                }
            }
        };

        basePlayerListener.setAudioControlListener(audioControlListener);
        currentAudioPlayer.setOnPlayListener(basePlayerListener);
    }


    public void stopAudio() {
        super.stopAudio();
    }

    @Override
    public void startPlayAudioDelay(long delayMillis, AudioRecordPlayable playable, AudioControlListener audioControlListener, int audioStreamType) {
        if (StorageUtil.isExternalStorageExist()) {

            if (super.startAudio(playable, audioControlListener, audioStreamType, true, delayMillis)) {
            }
        } else {
            Toast.makeText(mContext, R.string.sdcard_not_exist_error, Toast.LENGTH_SHORT).show();
        }
    }

    protected boolean startAudio(
            Playable playable,
            AudioControlListener audioControlListener,
            int audioStreamType,
            boolean resetOrigAudioStreamType,
            long delayMillis) {
        String filePath = playable.getPath();
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        //正在播放，停止播放
        if (isPlayingAudio()) {
            stopAudio();
            //如果相等，就是同一个对象了
            if (currentPlayable.isAudioEqual(playable)) {
                return false;
            }
        }

//        state = AudioControllerState.stop;

        currentPlayable = playable;
        currentAudioPlayer = new AudioPlayer(mContext);
        currentAudioPlayer.setDataSource(filePath);

        setOnPlayListener(currentPlayable, audioControlListener);

        if (resetOrigAudioStreamType) {
//            this.origAudioStreamType = audioStreamType;
        }
//        this.currentAudioStreamType = audioStreamType;

        mHandler.postDelayed(playRunnable, delayMillis);

//        state = AudioControllerState.ready;
        if (audioControlListener != null) {
            audioControlListener.onAudioControllerReady(currentPlayable);
        }

        return true;
    }

    @Override
    public AudioRecordPlayable getPlayingAudio() {
        return null;
    }

}

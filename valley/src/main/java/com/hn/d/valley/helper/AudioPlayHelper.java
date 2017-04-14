package com.hn.d.valley.helper;

import android.content.Context;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.hn.d.valley.R;
import com.hn.d.valley.main.me.SkinManagerUIView;
import com.hn.d.valley.main.message.audio.AudioRecordPlayable;
import com.hn.d.valley.main.message.audio.BaseAudioControl;
import com.hn.d.valley.main.message.audio.PathAudioControl;
import com.hn.d.valley.main.message.audio.Playable;
import com.hn.d.valley.skin.SkinUtils;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：音频播放助手
 * 创建人员：Robi
 * 创建时间：2017/04/13 14:32
 * 修改人员：Robi
 * 修改时间：2017/04/13 14:32
 * 修改备注：
 * Version: 1.0.0
 */
public class AudioPlayHelper {

    ImageView mPlayImageView;

    Context mContext;
    String lastPath = "";
    private PathAudioControl mPathAudioControl;
    private AudioRecordPlayable mAudioRecordPlayable;

    public AudioPlayHelper(Context context) {
        mContext = context;
    }

    public void initPlay() {
        if (mPathAudioControl == null) {
            mPathAudioControl = PathAudioControl.getInstance(mContext);
        }
    }

    /**
     * 是否使用耳机模式
     */
    public void setEarPhoneModeEnable(boolean isEarPhoneModeEnable) {
        initPlay();
        mPathAudioControl.setEarPhoneModeEnable(isEarPhoneModeEnable);
    }

    public void initPlayImageView(ImageView imageView) {
        mPlayImageView = imageView;
        switch (SkinUtils.getSkin()) {
            case SkinManagerUIView.SKIN_BLUE:
                mPlayImageView.setImageResource(R.drawable.voice_playing_blue);
                break;
            case SkinManagerUIView.SKIN_GREEN:
                mPlayImageView.setImageResource(R.drawable.voice_playing);
                break;
            default:
                mPlayImageView.setImageResource(R.drawable.voice_playing_black);
                break;
        }
    }

    /**
     * 开始播放
     */
    public void playAudio(String path, long duration, final BaseAudioControl.AudioControlListener listener) {
        if (mPathAudioControl == null) {
            initPlay();
        } else {
            if (mPathAudioControl.isPlayingAudio()) {
                stopAudio();
                if (TextUtils.equals(lastPath, path)) {
                    return;
                }
            }
        }
        if (mAudioRecordPlayable == null) {
            mAudioRecordPlayable = new AudioRecordPlayable(path, duration);
            lastPath = path;
        } else {
            if (TextUtils.equals(lastPath, path)) {

            } else {
                mAudioRecordPlayable = new AudioRecordPlayable(path, duration);
            }
        }

        mPathAudioControl.startPlayAudioDelay(0, mAudioRecordPlayable, new BaseAudioControl.AudioControlListener() {
            @Override
            public void onAudioControllerReady(Playable playable) {
                if (mPlayImageView != null) {
                    switch (SkinUtils.getSkin()) {
                        case SkinManagerUIView.SKIN_BLUE:
                            mPlayImageView.setImageResource(R.drawable.near_voice_playing_s_blue);
                            break;
                        case SkinManagerUIView.SKIN_GREEN:
                            mPlayImageView.setImageResource(R.drawable.voice_playing_n);
                            break;
                        default:
                            mPlayImageView.setImageResource(R.drawable.near_voice_playing_s_black);
                            break;
                    }
                }

                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.base_rotate);
                animation.setInterpolator(new LinearInterpolator());
                animation.setRepeatMode(Animation.RESTART);
                animation.setRepeatCount(Animation.INFINITE);
                mPlayImageView.setAnimation(animation);
                mPlayImageView.startAnimation(animation);

                if (listener != null) {
                    listener.onAudioControllerReady(playable);
                }
            }

            @Override
            public void onEndPlay(Playable playable) {
                stopAudio();

                if (listener != null) {
                    listener.onEndPlay(playable);
                }
            }

            @Override
            public void updatePlayingProgress(Playable playable, long curPosition) {
                if (listener != null) {
                    listener.updatePlayingProgress(playable, curPosition);
                }
            }
        });
    }

    /**
     * 停止播放
     */
    public void stopAudio() {
        if (mPathAudioControl != null) {
            if (mPathAudioControl.isPlayingAudio()) {
                mPathAudioControl.stopAudio();
            }
        }
        if (mPlayImageView != null) {
            mPlayImageView.clearAnimation();
        }
    }

}

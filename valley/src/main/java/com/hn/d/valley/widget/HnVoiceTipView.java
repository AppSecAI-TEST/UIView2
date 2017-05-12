package com.hn.d.valley.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.example.m3b.Audio;
import com.hn.d.valley.R;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/05/08 18:11
 * 修改人员：Robi
 * 修改时间：2017/05/08 18:11
 * 修改备注：
 * Version: 1.0.0
 */
public class HnVoiceTipView extends AppCompatImageView {

    Drawable[] mDrawables;
    boolean isPlaying = false;
    int index = 0;

    HnPlayTimeView mPlayTimeView;

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            setImageDrawable(mDrawables[index]);
            if (mPlayTimeView != null) {
                int currentPosition = Audio.instance().getCurrentPosition(String.valueOf(mPlayTimeView.getTag()));
                mPlayTimeView.setPlayTime(currentPosition / 1000);
            }
            index++;
            if (index >= 8) {
                index = 0;
            }
            postDelayed(mRunnable, 40);
        }
    };

    public HnVoiceTipView(Context context) {
        super(context);
    }

    public HnVoiceTipView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HnVoiceTipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setImageResource(R.drawable.music_1);

        startPlaying(isPlaying);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopPlay();
    }

    /**
     * 开始动画
     */
    public void startPlaying(boolean play) {
        isPlaying = play;
        if (isPlaying) {
            ensureRes();
            stopPlay();
            post(mRunnable);
        } else {
            removeCallbacks(mRunnable);
        }
    }

    private void stopPlay() {
        removeCallbacks(mRunnable);
    }

    public void setPlayTimeView(HnPlayTimeView playTimeView) {
        mPlayTimeView = playTimeView;
    }

    private void ensureRes() {
        if (mDrawables == null) {
            mDrawables = new Drawable[8];
            mDrawables[0] = ContextCompat.getDrawable(getContext(), R.drawable.music_1);
            mDrawables[1] = ContextCompat.getDrawable(getContext(), R.drawable.music_2);
            mDrawables[2] = ContextCompat.getDrawable(getContext(), R.drawable.music_3);
            mDrawables[3] = ContextCompat.getDrawable(getContext(), R.drawable.music_4);
            mDrawables[4] = ContextCompat.getDrawable(getContext(), R.drawable.music_5);
            mDrawables[5] = ContextCompat.getDrawable(getContext(), R.drawable.music_6);
            mDrawables[6] = ContextCompat.getDrawable(getContext(), R.drawable.music_7);
            mDrawables[7] = ContextCompat.getDrawable(getContext(), R.drawable.music_8);
        }
    }
}

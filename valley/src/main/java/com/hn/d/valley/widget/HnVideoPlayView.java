package com.hn.d.valley.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

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
public class HnVideoPlayView extends AppCompatImageView {
    PlayType mPlayType = PlayType.VIDEO;

    public HnVideoPlayView(Context context) {
        super(context);
        initView();
    }

    public HnVideoPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public HnVideoPlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        loadImage();
    }

    private void loadImage() {
        switch (mPlayType) {
            case VIDEO:
                setImageResource(R.drawable.play_redianzixun);
                break;
            case VOICE_PAUSE:
                setImageResource(R.drawable.icon_pause_home_small);
                break;
            case VOICE_HOME:
                setImageResource(R.drawable.icon_play_home);
                break;
            case VOICE_HOME_PAUSE:
                setImageResource(R.drawable.icon_pause_home_small);
                break;
            case VOICE:
            default:
                setImageResource(R.drawable.icon_play_home_small);
                break;
        }
    }

    public void setPlayType(PlayType playType) {
        mPlayType = playType;
        loadImage();
    }

    public enum PlayType {
        VIDEO /*视频播放按钮*/, VOICE/*音频播放按钮*/, VOICE_PAUSE/*音频暂停按钮*/,
        VOICE_HOME/*主页音频播放按钮*/, VOICE_HOME_PAUSE/*主页音频暂停按钮*/
    }
}

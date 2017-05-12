package com.hn.d.valley.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.hn.d.valley.R;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：搜索音乐, 播放按钮
 * 创建人员：Robi
 * 创建时间：2017/05/04 16:57
 * 修改人员：Robi
 * 修改时间：2017/05/04 16:57
 * 修改备注：
 * Version: 1.0.0
 */
public class HnPlayView extends AppCompatImageView {

    boolean playing;

    public HnPlayView(Context context) {
        super(context);
        initView();
    }

    public HnPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public HnPlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setPlaying(false);
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean play) {
        playing = play;
        if (playing) {
            setImageResource(R.drawable.icon_pause);
        } else {
            setImageResource(R.drawable.icon_play);
        }
    }
}

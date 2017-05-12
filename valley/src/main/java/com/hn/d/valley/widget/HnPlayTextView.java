package com.hn.d.valley.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.angcyo.uiview.widget.RTextView;
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
public class HnPlayTextView extends RTextView {

    boolean playing;

    public HnPlayTextView(Context context) {
        super(context);
    }

    public HnPlayTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HnPlayTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();
        setPlaying(false);
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean play) {
        playing = play;
        if (playing) {
            setTopIco(R.drawable.icon_pause);
            setText(R.string.pause);
        } else {
            setTopIco(R.drawable.icon_play);
            setText(R.string.audition);
        }
    }
}

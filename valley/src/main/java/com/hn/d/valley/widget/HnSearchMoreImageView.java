package com.hn.d.valley.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.hn.d.valley.R;
import com.hn.d.valley.main.me.SkinManagerUIView;
import com.hn.d.valley.skin.SkinUtils;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/06/23 14:18
 * 修改人员：Robi
 * 修改时间：2017/06/23 14:18
 * 修改备注：
 * Version: 1.0.0
 */
public class HnSearchMoreImageView extends AppCompatImageView {
    public HnSearchMoreImageView(Context context) {
        super(context);
    }

    public HnSearchMoreImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isInEditMode()) {
            return;
        }
        switch (SkinUtils.getSkin()) {
            case SkinManagerUIView.SKIN_BLACK:
                setImageResource(R.drawable.gengduo_shousuo_black);
                break;
            case SkinManagerUIView.SKIN_GREEN:
                setImageResource(R.drawable.gengduo_shousuo_green);
                break;
            case SkinManagerUIView.SKIN_BLUE:
                setImageResource(R.drawable.gengduo_shousuo_blue);
                break;
        }
    }
}

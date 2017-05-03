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
 * 创建时间：2017/05/03 17:37
 * 修改人员：Robi
 * 修改时间：2017/05/03 17:37
 * 修改备注：
 * Version: 1.0.0
 */
public class HnAddMusicView extends AppCompatImageView {

    public HnAddMusicView(Context context) {
        super(context);
    }

    public HnAddMusicView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HnAddMusicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        switch (SkinUtils.getSkin()) {
            case SkinManagerUIView.SKIN_BLUE:
                setImageResource(R.drawable.icon_tinajia_blue);
                break;
            case SkinManagerUIView.SKIN_GREEN:
                setImageResource(R.drawable.icon_tinajia_green);
                break;
            default:
                setImageResource(R.drawable.icon_tinajia_grey);
                break;
        }
    }
}

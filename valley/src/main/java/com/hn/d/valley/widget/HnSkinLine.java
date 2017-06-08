package com.hn.d.valley.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.angcyo.uiview.skin.SkinHelper;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/04/06 10:39
 * 修改人员：Robi
 * 修改时间：2017/04/06 10:39
 * 修改备注：
 * Version: 1.0.0
 */
public class HnSkinLine extends View {
    public HnSkinLine(Context context) {
        super(context);
    }

    public HnSkinLine(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            setBackgroundColor(SkinHelper.getSkin().getThemeSubColor());
        }
    }
}

package com.hn.d.valley.main.me;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.angcyo.library.utils.L;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/07/24 14:18
 * 修改人员：Robi
 * 修改时间：2017/07/24 14:18
 * 修改备注：
 * Version: 1.0.0
 */
public class TestImageView extends AppCompatImageView {
    public TestImageView(Context context) {
        super(context);
    }

    public TestImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        if (drawable != null) {
            L.e("call: setImageDrawable([drawable])-> " + drawable.getClass().getSimpleName());
        } else {
            L.e("call: setImageDrawable([drawable])-> null");
        }
    }
}

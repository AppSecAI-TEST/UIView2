package com.hn.d.valley.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：高度一定等于宽度的view
 * 创建人员：Robi
 * 创建时间：2017/04/01 14:30
 * 修改人员：Robi
 * 修改时间：2017/04/01 14:30
 * 修改备注：
 * Version: 1.0.0
 */
public class HnView extends View {
    public HnView(Context context) {
        super(context);
    }

    public HnView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable background = getBackground();
        if (background instanceof ColorDrawable) {
            background.setBounds(getPaddingLeft(), getPaddingTop(),
                    getMeasuredWidth() - getPaddingRight(), getMeasuredHeight() - getPaddingBottom());
            background.draw(canvas);
        } else {
            super.onDraw(canvas);
        }
    }
}

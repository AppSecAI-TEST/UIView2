package com.hn.d.valley.widget.groupView;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：只根据第一个Child决定自己的大小
 * 创建人员：Robi
 * 创建时间：2017/05/16 18:18
 * 修改人员：Robi
 * 修改时间：2017/05/16 18:18
 * 修改备注：
 * Version: 1.0.0
 */
public class HnFragmentLayout extends FrameLayout {
    public HnFragmentLayout(@NonNull Context context) {
        super(context);
    }

    public HnFragmentLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HnFragmentLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View childAt = getChildAt(0);
        if (childAt != null) {
            int wSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.UNSPECIFIED);
            int hSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.UNSPECIFIED);
            childAt.measure(wSpec, hSpec);
            //setMeasuredDimension(childAt.getMeasuredWidth(), childAt.getMeasuredHeight());
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(childAt.getMeasuredWidth(), MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(childAt.getMeasuredHeight(), MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}

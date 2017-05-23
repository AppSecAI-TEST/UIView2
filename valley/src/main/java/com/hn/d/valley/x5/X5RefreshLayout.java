package com.hn.d.valley.x5;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.angcyo.uiview.rsen.EmptyRefreshLayout;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/04/20 10:55
 * 修改人员：Robi
 * 修改时间：2017/04/20 10:55
 * 修改备注：
 * Version: 1.0.0
 */
public class X5RefreshLayout extends EmptyRefreshLayout {
    public X5RefreshLayout(Context context) {
        super(context);
    }

    public X5RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean innerCanChildScrollVertically(View view, int direction, float rawX, float rawY) {
        if (view instanceof X5WebView) {
            if (direction > 0) {
                //手指向上滑动
                return true;
            } else {
                return ((X5WebView) view).canTopScroll();
            }
        } else {
            return super.innerCanChildScrollVertically(view, direction, rawX, rawY);
        }
    }

}

package com.hn.d.valley.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.angcyo.uiview.rsen.PlaceholderView;
import com.angcyo.uiview.rsen.RefreshLayout;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/27 16:35
 * 修改人员：Robi
 * 修改时间：2016/12/27 16:35
 * 修改备注：
 * Version: 1.0.0
 */
public class HnEmptyRefreshLayout extends RefreshLayout {
    public HnEmptyRefreshLayout(Context context) {
        super(context);
        initView();
    }

    public HnEmptyRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        setRefreshDirection(BOTH);
        setNotifyListener(false);
        if (!isInEditMode()) {
            setTopView(new PlaceholderView(getContext()));
            setBottomView(new PlaceholderView(getContext()));
        }
    }
}

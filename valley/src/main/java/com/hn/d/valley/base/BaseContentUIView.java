package com.hn.d.valley.base;

import android.support.annotation.CallSuper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.angcyo.uiview.base.UIContentView;
import com.hn.d.valley.R;
import com.hn.d.valley.utils.RBus;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/05 11:32
 * 修改人员：Robi
 * 修改时间：2017/01/05 11:32
 * 修改备注：
 * Version: 1.0.0
 */
public abstract class BaseContentUIView extends UIContentView {
    boolean isRegister = false;

    @Override
    protected View inflateEmptyLayout(FrameLayout baseRootLayout, LayoutInflater inflater) {
        return inflater.inflate(R.layout.empty_layout, baseRootLayout);
    }

    @Override
    protected View inflateNonetLayout(FrameLayout baseRootLayout, LayoutInflater inflater) {
        return inflater.inflate(R.layout.no_network_layout, baseRootLayout);
    }

    @CallSuper
    @Override
    public void onViewLoad() {
        super.onViewLoad();
        if (!isRegister) {
            RBus.register(this);
            isRegister = true;
        }
    }

    @CallSuper
    @Override
    public void onViewUnload() {
        super.onViewUnload();
        if (isRegister) {
            RBus.unregister(this);
        }
        isRegister = false;
    }
}

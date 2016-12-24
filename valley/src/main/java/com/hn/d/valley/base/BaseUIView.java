package com.hn.d.valley.base;

import android.graphics.Color;
import android.support.annotation.NonNull;

import com.angcyo.uiview.base.UIBaseRxView;
import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.mvp.presenter.IBasePresenter;
import com.angcyo.uiview.mvp.view.IBaseView;
import com.hn.d.valley.utils.RBus;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/12 18:14
 * 修改人员：Robi
 * 修改时间：2016/12/12 18:14
 * 修改备注：
 * Version: 1.0.0
 */
public abstract class BaseUIView<P extends IBasePresenter> extends UIBaseRxView
        implements IBaseView, UIIDialogImpl.OnDismissListener {

    protected P mPresenter;

    public void bindPresenter(P presenter) {
        mPresenter = presenter;
        mPresenter.bindView(this);
    }

    @Override
    public void onStartLoad() {
        showLoadView();
    }

    @Override
    public void onFinishLoad() {
        hideLoadView();
    }

    @Override
    public void onError(int code, @NonNull String msg) {
        T_.show(msg);
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        RBus.register(this);
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        RBus.unregister(this);
        if (mPresenter != null) {
            mPresenter.onUnload();
        }
    }

    @Override
    public void onDismiss() {
        onCancel();
        if (mPresenter != null) {
            mPresenter.onCancel();
        }
    }

    @Override
    public int getDefaultBackgroundColor() {
        return Color.WHITE;
    }
}

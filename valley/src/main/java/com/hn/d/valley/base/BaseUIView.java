package com.hn.d.valley.base;

import android.support.annotation.NonNull;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.mvp.presenter.IBasePresenter;
import com.angcyo.uiview.mvp.view.IBaseView;
import com.angcyo.uiview.utils.T_;

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
public abstract class BaseUIView<P extends IBasePresenter> extends BaseContentUIView
        implements IBaseView, UIIDialogImpl.OnDismissListener {

    protected P mPresenter;

    public void bindPresenter(P presenter) {
        mPresenter = presenter;
        mPresenter.bindView(this);
    }

    @Override
    public void onRequestStart() {
        L.i(this.getClass().getSimpleName() + "->请求开始");
        showLoadView();
    }

    @Override
    public void onRequestFinish() {
        L.i(this.getClass().getSimpleName() + "->请求完成");
        hideLoadView();
    }

    @Override
    public void onRequestError(int code, @NonNull String msg) {
        T_.show(msg);
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        if (mPresenter != null) {
            mPresenter.onUnload();
        }
    }

    @Override
    public void onDismiss() {
        L.i(this.getClass().getSimpleName() + "->进度对话框被销毁, 请求取消!");
        onRequestCancel();
        if (mPresenter != null) {
            mPresenter.onCancel();
        }
    }

    @Override
    public void onRequestCancel() {
        L.i(this.getClass().getSimpleName() + "->请求取消");
        onRequestFinish();
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }
}

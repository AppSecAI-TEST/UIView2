package com.hn.d.valley.base.rx;

import com.angcyo.uiview.mvp.view.IBaseView;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/14 18:12
 * 修改人员：Robi
 * 修改时间：2016/12/14 18:12
 * 修改备注：
 * Version: 1.0.0
 */
public abstract class UIStringSubscriber<T, V extends IBaseView> extends BaseSingleSubscriber<T> {

    V mBaseView;

    public UIStringSubscriber(V baseView) {
        mBaseView = baseView;
    }

    /**
     * 请求成功
     */
    public abstract void onSuccess(T bean);

    @Override
    public void onCompleted() {
        super.onCompleted();
        //mBaseView.onRequestFinish();
    }

    @Override
    public void onError(int code, String msg) {
        super.onError(code, msg);
        mBaseView.onRequestError(code, msg);
    }

    @Override
    public void onSucceed(T bean) {
        mBaseView.onRequestFinish();
        onSuccess(bean);
    }
}

package com.hn.d.valley.base.rx;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.mvp.view.IBaseView;

import rx.functions.Action0;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：取消订阅的处理
 * 创建人员：Robi
 * 创建时间：2016/12/15 10:39
 * 修改人员：Robi
 * 修改时间：2016/12/15 10:39
 * 修改备注：
 * Version: 1.0.0
 */
public class UnSubscriber<V extends IBaseView> implements Action0 {

    V mBaseView;

    public UnSubscriber(V baseView) {
        mBaseView = baseView;
    }

    public static <V extends IBaseView> UnSubscriber build(V baseView) {
        return new UnSubscriber(baseView);
    }

    /**
     * 当被取消订阅的时候调用
     */
    @Override
    public void call() {
        onUnsubscribe();
    }

    /**
     * 当被取消订阅的时候调用
     */
    public void onUnsubscribe() {
        L.d("订阅取消->" + this.getClass().getSimpleName());
        mBaseView.onRequestCancel();
    }
}

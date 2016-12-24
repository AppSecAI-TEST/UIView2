package com.hn.d.valley.base.rx;

import com.angcyo.uiview.mvp.view.IBaseView;
import com.angcyo.uiview.net.base.Network;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.base.excepetion.NonetException;

import rx.functions.Action0;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：订阅之前, 用来检查网络
 * 创建人员：Robi
 * 创建时间：2016/12/15 10:39
 * 修改人员：Robi
 * 修改时间：2016/12/15 10:39
 * 修改备注：
 * Version: 1.0.0
 */
public class BeforeSubscriber<V extends IBaseView> implements Action0 {

    V mBaseView;

    public BeforeSubscriber(V baseView) {
        mBaseView = baseView;
    }

    public static <V extends IBaseView> BeforeSubscriber build(V baseView) {
        return new BeforeSubscriber(baseView);
    }

    @Override
    public void call() {
        if (Network.isConnected(ValleyApp.getApp())) {
            mBaseView.onStartLoad();
        } else {
            throw new NonetException();
        }
    }
}

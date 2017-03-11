package com.hn.d.valley.base.rx;

import com.angcyo.uiview.mvp.view.IBaseView;
import com.hn.d.valley.base.Bean;

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
public class UISubscriber<O extends Object, B extends Bean<O>, V extends IBaseView> extends BaseSubscriber<O, B> {

    V mBaseView;

    public UISubscriber(V baseView) {
        mBaseView = baseView;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        mBaseView.onRequestStart();
//    }

    @Override
    public void onError(int code, String msg) {
        super.onError(code, msg);
        mBaseView.onRequestError(code, msg);
    }

    @Override
    public void onSuccess(B b) {
        super.onSuccess(b);
        mBaseView.onRequestFinish();
    }
}

package com.hn.d.valley.base.rx;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.mvp.view.IBaseView;
import com.angcyo.uiview.utils.Json;

import okhttp3.ResponseBody;
import retrofit2.Response;

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
public class UIStringSubscriber<T, V extends IBaseView> extends SingleSubscriber<Response<String>> {

    V mBaseView;

    public UIStringSubscriber(V baseView) {
        mBaseView = baseView;
    }

//    @Override
//    public void onNext(ResponseBody b) {
//
//        if (b != null) {
//            b.
//        }
//
//
//        if (b.isSuccess()) {
//            onSuccess(b);
//        } else {
//            onError(b.error.code, b.error.msg);
//        }
//    }

    /**
     * 请求成功
     */
    public void onSuccess(ResponseBody b) {
        L.d("订阅成功->" + this.getClass().getSimpleName() + "\n" + Json.to(b) + "\n");
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

//    @Override
//    public void onSuccess(ResponseBody b) {
//        super.onSuccess(b);
//        mBaseView.onRequestFinish();
//    }

    @Override
    public void onNext(Response<String> stringResponse) {
        if (stringResponse != null && stringResponse.isSuccessful()) {

        }
    }
}

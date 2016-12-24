package com.hn.d.valley.base.rx;

import com.angcyo.library.utils.L;
import com.fasterxml.jackson.core.JsonParseException;
import com.hn.d.valley.base.excepetion.NonetException;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import rx.Subscriber;
import rx.functions.Action0;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/15 11:53
 * 修改人员：Robi
 * 修改时间：2016/12/15 11:53
 * 修改备注：
 * Version: 1.0.0
 */
public abstract class SingleSubscriber<T> extends Subscriber<T> implements Action0 {

    @Override
    public void onStart() {
        super.onStart();
        L.d("开始订阅->" + this.getClass().getSimpleName());
    }

    @Override
    public void onCompleted() {
        L.d("订阅完成->" + this.getClass().getSimpleName());
    }

    @Override
    public void onError(Throwable e) {
        int errorCode;
        String errorMsg;

        if (e instanceof UnknownHostException ||
                e instanceof SocketTimeoutException ||
                e instanceof SocketException ||
                e instanceof NonetException) {
            L.e(e.getMessage());
            errorMsg = "请检查网络连接!";
            errorCode = 40000;
        } else if (e instanceof JsonParseException) {
            errorMsg = "恐龙君打了个盹，请稍后再试!"; //"数据解析错误:" + e.getMessage();
            errorCode = -40001;
        } else {
            errorMsg = "未知错误:" + e.getMessage();
            errorCode = -40000;
        }

        onError(errorCode, errorMsg);
    }

    /**
     * 当被取消订阅的时候调用
     */
    @Override
    public void call() {
        onUnsubscribe();
    }

    /**
     * 统一错误处理
     */
    public void onError(int code, String msg) {
        L.d("订阅异常->" + this.getClass().getSimpleName() + " " + msg);
    }

    /**
     * 当被取消订阅的时候调用
     */
    public void onUnsubscribe() {
        L.d("订阅取消->" + this.getClass().getSimpleName());
    }
}

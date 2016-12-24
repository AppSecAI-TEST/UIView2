package com.hn.d.valley.base;

import com.angcyo.uiview.mvp.view.IBaseView;
import com.hn.d.valley.base.rx.BeforeSubscriber;
import com.hn.d.valley.base.rx.UnSubscriber;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/24 13:56
 * 修改人员：Robi
 * 修改时间：2016/12/24 13:56
 * 修改备注：
 * Version: 1.0.0
 */
public class Transform {
    public static <T, V extends IBaseView> Observable.Transformer<T, T> defaultSchedulers(final V baseView) {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable
                        .unsubscribeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(BeforeSubscriber.build(baseView))
                        .doOnUnsubscribe(UnSubscriber.build(baseView));
            }
        };
    }
}

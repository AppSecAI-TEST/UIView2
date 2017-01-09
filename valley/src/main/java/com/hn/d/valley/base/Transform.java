package com.hn.d.valley.base;

import android.text.TextUtils;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.mvp.view.IBaseView;
import com.angcyo.uiview.utils.Json;
import com.hn.d.valley.base.rx.BeforeSubscriber;
import com.hn.d.valley.base.rx.RException;
import com.hn.d.valley.base.rx.UnSubscriber;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
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

    public static <T, V extends IBaseView> Observable.Transformer<ResponseBody, T> defaultStringSchedulers(final V baseView, final Class<T> type) {
        return new Observable.Transformer<ResponseBody, T>() {

            @Override
            public Observable<T> call(Observable<ResponseBody> responseObservable) {
                return responseObservable.unsubscribeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(BeforeSubscriber.build(baseView))
                        .doOnUnsubscribe(UnSubscriber.build(baseView))
                        .map(new Func1<ResponseBody, T>() {
                            @Override
                            public T call(ResponseBody stringResponse) {
                                T bean;
                                String body;
                                try {
                                    body = stringResponse.string();

                                    //"接口返回数据-->\n" +
                                    L.json(body);

                                    JSONObject jsonObject = new JSONObject(body);
                                    int result = jsonObject.getInt("result");
                                    if (result == 1) {
                                        //请求成功
                                        String data = jsonObject.getString("data");
                                        if (!TextUtils.isEmpty(data)) {
                                            bean = Json.from(data, type);
                                            return bean;
                                        }
                                    } else {
                                        //请求成功, 但是有错误
                                        JSONObject errorObject = jsonObject.getJSONObject("error");

                                        throw new RException(errorObject.getInt("code"),
                                                errorObject.getString("msg"),
                                                errorObject.getString("more"));
                                    }
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}

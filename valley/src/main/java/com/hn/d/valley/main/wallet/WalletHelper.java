package com.hn.d.valley.main.wallet;

import com.angcyo.uiview.RApplication;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.cache.UserCache;

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
 * 创建人员：hewking
 * 创建时间：2017/05/04 16:09
 * 修改人员：hewking
 * 修改时间：2017/05/04 16:09
 * 修改备注：
 * Version: 1.0.0
 */
public class WalletHelper {

    private WalletHelper(){}

    private static class Holder {
        private static WalletHelper sInstance = new WalletHelper();
    }

    public static WalletHelper getInstance() {
        return Holder.sInstance;
    }

    public WalletAccount getWalletAccount() {
        return mWalletAccount;
    }

    private WalletAccount mWalletAccount;

    public void setWalletAccount(WalletAccount account) {
        this.mWalletAccount = account;
    }

    public void fetchWallet(){
        RRetrofit.create(WalletService.class)
                .account(Param.buildInfoMap("uid:" + UserCache.getUserAccount(),"device:" + RApplication.getIMEI()))
                .compose(Rx.transformer(WalletAccount.class))
                .subscribe(new BaseSingleSubscriber<WalletAccount>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                    }

                    @Override
                    public void onSucceed(WalletAccount bean) {
                        super.onSucceed(bean);
                        mWalletAccount = bean;
                    }
                });

    }

    public static Observable.Transformer<ResponseBody, String> getTransformer() {
        return new Observable.Transformer<ResponseBody, String>() {
            @Override
            public Observable<String> call(Observable<ResponseBody> responseBodyObservable) {
                return responseBodyObservable.unsubscribeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .map(new Func1<ResponseBody, String>() {
                            @Override
                            public String call(ResponseBody responseBody) {
                                try {
                                    return responseBody.string();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return "";
                            }
                        })
                        .retry(3)
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

}

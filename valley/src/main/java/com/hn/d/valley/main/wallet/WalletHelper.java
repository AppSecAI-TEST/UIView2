package com.hn.d.valley.main.wallet;

import android.view.View;

import com.angcyo.uiview.RApplication;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.utils.Json;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.realm.LoginBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.me.setting.BindPhoneUIView;
import com.hn.d.valley.main.message.groupchat.RequestCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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
       fetchWallet(null);
    }

    public void fetchWallet(final RequestCallback<WalletAccount> callback) {
        RRetrofit.create(WalletService.class)
                .account(Param.buildInfoMap("uid:" + UserCache.getUserAccount(),"device:" + RApplication.getIMEI()))
                .compose(getTransformer())
                .subscribe(new BaseSingleSubscriber<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        if (callback != null) {
                            callback.onStart();
                        }
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        if (callback != null) {
                            callback.onError(msg);
                        }
                    }

                    @Override
                    public void onSucceed(String bean) {
                        super.onSucceed(bean);
                        parseResult(bean,callback);
                    }
                });
    }

    private void parseResult(String beans, RequestCallback<WalletAccount> callback) {
        int code = -1;
        String data = "";
        try {
            JSONObject jsonObject = new JSONObject(beans);
            code = jsonObject.optInt("code");
            data = jsonObject.optString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (code == 200) {
            mWalletAccount = Json.from(data,WalletAccount.class);
        } else if (code == 404) {
            //未开户
            openAccount(UserCache.getUserAccount(),callback);
        } else if (code == 400) {
            //参数缺失
        }
        if (callback != null) {
            callback.onSuccess(mWalletAccount);
        }

    }

    public void openAccount(String uid, final RequestCallback<WalletAccount> requestCallback) {
        RRetrofit.create(WalletService.class)
                .open(Param.buildInfoMap("uid:" + uid))
                .compose(getTransformer())
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onSucceed(String bean) {
                        super.onSucceed(bean);
                        if(requestCallback == null) {
                            fetchWallet();
                        } else {
                            fetchWallet(requestCallback);
                        }
                    }
                });
    }

    public void openAccount(String uid) {
        openAccount(uid,null);
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

    public static void  showBindPhoneDialog(final ILayout layout,String content , String ok,String cancel) {
        UIDialog.build()
                .setDialogContent(content)
                .setOkText(ok)
                .setOkListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 先判断是否绑定手机
                        if (!UserCache.instance().isBindPhone()) {
                            layout.startIView(new BindPhoneUIView());
                        }
                    }
                })
                .setCancelText(cancel)
                .showDialog(layout);
    }


}

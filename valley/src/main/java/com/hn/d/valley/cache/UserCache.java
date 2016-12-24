package com.hn.d.valley.cache;

import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.LoginBean;
import com.hn.d.valley.realm.RRealm;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.StatusCode;
import com.orhanobut.hawk.Hawk;

import rx.Observable;
import rx.Subscriber;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/23 18:08
 * 修改人员：Robi
 * 修改时间：2016/12/23 18:08
 * 修改备注：
 * Version: 1.0.0
 */
public class UserCache {

    /**
     * 登录成功后的用户信息
     */
    LoginBean mLoginBean;

    private UserCache() {

    }

    public static UserCache instance() {
        return Holder.instance;
    }

    public static String getUserAccount() {
        return Hawk.get(Constant.USER_ACCOUNT, "");
    }

    public static void setUserAccount(String account) {
        Hawk.put(Constant.USER_ACCOUNT, account);
    }

    public static String getUserToken() {
        return Hawk.get(Constant.USER_TOKEN, "");
    }

    public static void setUserToken(String token) {
        Hawk.put(Constant.USER_TOKEN, token);
    }

    /**
     * 云信是否已经登录成功
     */
    public static boolean isLoginSuccess() {
        StatusCode status = NIMClient.getStatus();
        return status == StatusCode.LOGINED;
    }

    /**
     * 获取用户头像
     */
    public String getAvatar() {
        if (getLoginBean() == null) {
            return "";
        }
        return mLoginBean.getAvatar();
    }

    /**
     * 获取用户信息
     */
    public Observable<LoginBean> getLoginBeanObservable() {
        return Observable.create(new Observable.OnSubscribe<LoginBean>() {
            @Override
            public void call(Subscriber<? super LoginBean> subscriber) {
                if (getLoginBean() == null) {
                    subscriber.onError(new NullPointerException("用户信息为空"));
                } else {
                    subscriber.onNext(mLoginBean);
                    subscriber.onCompleted();
                }
            }
        });

    }

    public LoginBean getLoginBean() {
        if (mLoginBean == null) {
            setLoginBean(RRealm.where(LoginBean.class).findAll().last(), false);
        }
        return mLoginBean;
    }

    /**
     * 缓存用户信息
     */
    public void setLoginBean(LoginBean loginBean) {
        setLoginBean(loginBean, true);
    }

    public void setLoginBean(LoginBean loginBean, boolean save) {
        mLoginBean = loginBean;
        setUserAccount(loginBean.getUid());
        setUserToken(loginBean.getYx_token());

        if (save) {
            RRealm.save(loginBean);
        }
    }

    private static class Holder {
        static UserCache instance = new UserCache();
    }

}

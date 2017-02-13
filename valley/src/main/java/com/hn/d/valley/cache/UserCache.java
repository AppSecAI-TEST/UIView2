package com.hn.d.valley.cache;

import android.support.annotation.UiThread;
import android.text.TextUtils;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.realm.LoginBean;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.service.UserInfoService;
import com.hn.d.valley.utils.RBus;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.StatusCode;
import com.orhanobut.hawk.Hawk;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

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
    UserInfoBean mUserInfoBean;

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

    public static String getUserAvatar() {
        return Hawk.get(Constant.USER_AVATAR, "");
    }

    public static void setUserAvatar(String avatar) {
        Hawk.put(Constant.USER_AVATAR, avatar);
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

    @UiThread
    public LoginBean getLoginBean() {
        if (mLoginBean == null) {
            RealmResults<LoginBean> all = RRealm.realm().where(LoginBean.class).findAll();
            if (!all.isEmpty()) {
                setLoginBean(all.last(), false);
            }
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
        setUserAvatar(loginBean.getAvatar());

        CrashReport.setUserId(loginBean.getUid());

        if (save) {
            RRealm.save(loginBean);
        }
    }

    @UiThread
    public UserInfoBean getUserInfoBean() {
        mUserInfoBean = getUserInfoBean(getUserAccount());
        return mUserInfoBean;
    }

    public void setUserInfoBean(UserInfoBean bean) {
        mUserInfoBean = bean;
        RRealm.save(bean);
    }

    @UiThread
    public UserInfoBean getUserInfoBean(String uid) {
        RealmResults<UserInfoBean> all = RRealm.realm().where(UserInfoBean.class).equalTo("uid", uid).findAll();
        return all.last(null);
    }

    /**
     * 从服务器拉取用户信息
     */
    public void updateUserInfo(String to_uid) {
        if (TextUtils.isEmpty(to_uid)) {
            return;
        }
        fetchUserInfo(to_uid).subscribe(new BaseSingleSubscriber<UserInfoBean>() {
            @Override
            public void onSucceed(UserInfoBean userInfoBean) {
                L.i("更新用户数据库信息:" + userInfoBean.getUid() + " " + userInfoBean.getUsername() + " 成功.");
                RBus.post(userInfoBean);

                if (TextUtils.equals(userInfoBean.getUid(), getUserAccount())) {
                    setUserAvatar(userInfoBean.getAvatar());
                }
            }
        });
    }

    public void updateUserInfo() {
        updateUserInfo(getUserAccount());
    }

    public Observable<UserInfoBean> fetchUserInfo(String to_uid) {
        Map<String, String> map = new HashMap<>();
        map.put("uid", getUserAccount());
        map.put("to_uid", to_uid);
        return RRetrofit.create(UserInfoService.class)
                .userInfo(Param.map(map))
                .compose(Rx.transformer(UserInfoBean.class))
                .map(new Func1<UserInfoBean, UserInfoBean>() {
                    @Override
                    public UserInfoBean call(UserInfoBean userInfoBean) {
                        if (userInfoBean == null) {
                            L.e("fetchUserInfo 拉取用户信息失败. ->即将重试");
                            throw new NullPointerException("fetchUserInfo 拉取用户信息失败.");
                        }
                        RRealm.save(userInfoBean);
                        return userInfoBean;
                    }
                });
    }

    public Observable<UserInfoBean> fetchUserInfo() {
        return fetchUserInfo(getUserAccount());
    }

    private static class Holder {
        static UserCache instance = new UserCache();
    }

}

package com.hn.d.valley.cache;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.net.RRetrofit;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.Transform;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.realm.LoginBean;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.sub.user.service.UserInfoService;
import com.hwangjr.rxbus.RxBus;
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
            RealmResults<LoginBean> all = RRealm.where(LoginBean.class).findAll();
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

        CrashReport.setUserId(loginBean.getUid());

        if (save) {
            RRealm.save(loginBean);
        }
    }

    public UserInfoBean getUserInfoBean() {
        if (mUserInfoBean == null) {
            mUserInfoBean = getUserInfoBean(getUserAccount());
        }
        return mUserInfoBean;
    }

    public void setUserInfoBean(UserInfoBean bean) {
        mUserInfoBean = bean;
        RRealm.save(bean);
    }

    public UserInfoBean getUserInfoBean(String uid) {
        RealmResults<UserInfoBean> all = RRealm.where(UserInfoBean.class).equalTo("uid", uid).findAll();
        return all.last(null);
    }

    /**
     * 从服务器拉取用户信息
     */
    public void updateUserInfo(String to_uid) {
        fetchUserInfo(to_uid).subscribe(new BaseSingleSubscriber<UserInfoBean>() {
            @Override
            public void onNext(UserInfoBean userInfoBean) {
                L.i("更新用户数据库信息:" + userInfoBean.getUid() + " " + userInfoBean.getUsername() + " 成功.");
                RxBus.get().post(userInfoBean);
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
                .compose(Transform.defaultStringSchedulers(UserInfoBean.class))
                .map(new Func1<UserInfoBean, UserInfoBean>() {
                    @Override
                    public UserInfoBean call(UserInfoBean userInfoBean) {
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

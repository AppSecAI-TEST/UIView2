package com.hn.d.valley.control;

import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.RApplication;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.utils.Json;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.R;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.activity.HnSplashActivity;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.receiver.JPushReceiver;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.realm.LoginBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.nim.RNim;
import com.hn.d.valley.start.service.StartService;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.orhanobut.hawk.Hawk;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

import static com.hn.d.valley.control.AutoLoginControl.AutoLoginBean.LOGIN_PHONE;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：自动登录控制
 * 创建人员：Robi
 * 创建时间：2017/05/19 14:52
 * 修改人员：Robi
 * 修改时间：2017/05/19 14:52
 * 修改备注：
 * Version: 1.0.0
 */
public class AutoLoginControl {
    static final String KEY_LOGIN = "key_login";
    //登录的日期, 每天至少要登录一次
    static final String KEY_LOGIN_DATE = "key_login_date";
    static final String KEY_LOGIN_BEAN = "key_login_bean";

    AutoLoginListener mAutoLoginListener;

    private AutoLoginControl() {
    }

    public static AutoLoginControl instance() {
        return Holder.instance;
    }

    /**
     * 设置是否登录了, 如果登录了, 下次就会自动登录
     */
    public static void setLogin(boolean login) {
        Hawk.put(KEY_LOGIN, login);
        if (login) {
            Hawk.put(KEY_LOGIN_DATE, System.currentTimeMillis());
        }
    }

    /**
     * 保存自动登录需要的信息
     */
    public static void saveAutoLoginBean(int login_type, String open_id, String phone, String pwd) {
        AutoLoginBean bean = new AutoLoginBean(login_type, open_id, phone, pwd);
        Hawk.put(KEY_LOGIN_BEAN, Json.to(bean));
    }

    static AutoLoginBean getAutoLoginBean() {
        String json = Hawk.get(KEY_LOGIN_BEAN, "");
        AutoLoginBean bean = Json.from(json, AutoLoginBean.class);
        return bean;
    }

    public static boolean canAutoLogin() {
        Boolean login = Hawk.get(KEY_LOGIN, false);
        if (login) {
            long nowTime = System.currentTimeMillis();
            Long date = Hawk.get(KEY_LOGIN_DATE, nowTime);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String format1 = simpleDateFormat.format(new Date(date));
            String format2 = simpleDateFormat.format(new Date(nowTime));

            if (!TextUtils.equals(format1, format2)) {
                //时间不是同一天, 不允许自动登录
                login = false;
            }
        }
        return login;
    }

    void onLoginSuccess(final Activity activity, LoginBean loginBean) {
        //登录成功, 保存用户的头像
        Hawk.put(loginBean.getPhone(), loginBean.getAvatar());
        UserCache.instance().setLoginBean(loginBean);

        //2: 登录云信
        RNim.login(loginBean.getUid(), loginBean.getYx_token(),
                new RequestCallbackWrapper<LoginInfo>() {
                    @Override
                    public void onResult(int code, LoginInfo result, Throwable exception) {
                        if (code == ResponseCode.RES_SUCCESS) {
                            if (mAutoLoginListener != null) {
                                mAutoLoginListener.onLoginSuccess();
                            }
                        } else {
                            T_.show(activity.getResources().getString(R.string.network_exception));
                            if (mAutoLoginListener != null) {
                                mAutoLoginListener.onLoginError();
                            }
                        }
                    }
                });
    }

    /**
     * 手机号码的方式登录
     */
    private void loginPhone(final Activity activity, String phone, String pwd) {
        Map<String, String> map = new HashMap<>();
        //手机号登录
        map.put("phone", phone);
        map.put("pwd", pwd);

        L.i("正在使用手机号码:" + phone + " 密码:" + pwd + " 自动登录.");
        loginInner(activity, map);
    }

    private void loginInner(final Activity activity, Map<String, String> map) {
        buildMap(map);

        RRetrofit.create(StartService.class)
                .userLogin2(Param.map(map))
                .compose(Rx.transformer(LoginBean.class))
                .subscribe(new BaseSingleSubscriber<LoginBean>() {
                    @Override
                    public void onSucceed(LoginBean bean) {
                        onLoginSuccess(activity, bean);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        if (mAutoLoginListener != null) {
                            mAutoLoginListener.onLoginError();
                        }
                    }
                });
    }

    /**
     * 第三方平台的方式登录
     */
    private void loginPlatform(Activity activity, String open_id, String open_type) {
        Map<String, String> map = new HashMap<>();
        //第三方登录
        map.put("open_id", open_id);
        /**
         * 第三方类型【1-qq,2-微信】【第三方登录必填】
         */
        map.put("open_type", open_type);

        L.i("正在使用" + (TextUtils.equals(open_type, "1") ? " QQ " : " 微信") + " open_id:" + open_id + " 自动登录.");
        loginInner(activity, map);
    }

    /**
     * 公共信息
     */
    private void buildMap(Map<String, String> map) {
        String jpushId;
        if (TextUtils.isEmpty(JPushReceiver.mRegistrationId)) {
            jpushId = JPushInterface.getRegistrationID(ValleyApp.getApp());
        } else {
            jpushId = JPushReceiver.mRegistrationId;
        }
        map.put("push_device_id", jpushId);

        map.put("os_version", Build.VERSION.RELEASE);
        map.put("phone_model", Build.MODEL);
        map.put("device_id", RApplication.getIMEI());
    }

    /**
     * 开始登录
     */
    public void startLogin(Activity activity, AutoLoginListener listener) {
        mAutoLoginListener = listener;
        if (canAutoLogin()) {
            //自动登录流程
            AutoLoginBean autoLoginBean = getAutoLoginBean();
            if (autoLoginBean.login_type == LOGIN_PHONE) {
                loginPhone(activity, autoLoginBean.phone, autoLoginBean.pwd);
            } else {
                loginPlatform(activity, autoLoginBean.open_id, String.valueOf(autoLoginBean.login_type));
            }
        } else {
            //正常登录流程
            HnSplashActivity.launcher(activity, false);
            activity.finish();
        }
    }

    public interface AutoLoginListener {
        void onLoginError();

        void onLoginSuccess();
    }

    private static class Holder {
        static AutoLoginControl instance = new AutoLoginControl();
    }

    public static class AutoLoginBean {
        //第三方类型【1-qq,2-微信】【第三方登录必填】
        public static final int LOGIN_PHONE = 0;
        public static final int LOGIN_QQ = 1;
        public static final int LOGIN_WX = 2;
        public int login_type = LOGIN_PHONE;
        public String open_id = "";
        public String phone = "";
        public String pwd = "";//RSA加密后的密码

        public AutoLoginBean() {
        }

        public AutoLoginBean(int login_type, String open_id, String phone, String pwd) {
            this.login_type = login_type;
            this.open_id = open_id;
            this.phone = phone;
            this.pwd = pwd;
        }
    }

}

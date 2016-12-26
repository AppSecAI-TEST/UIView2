package com.hn.d.valley.start.mvp;

import android.os.Build;
import android.text.TextUtils;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.mvp.presenter.BasePresenter;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.rsa.RSA;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.base.Bean;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.Transform;
import com.hn.d.valley.base.receiver.JPushReceiver;
import com.hn.d.valley.base.rx.UISubscriber;
import com.hn.d.valley.bean.LoginBean;
import com.hn.d.valley.start.service.StartService;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/14 13:40
 * 修改人员：Robi
 * 修改时间：2016/12/14 13:40
 * 修改备注：
 * Version: 1.0.0
 */
public class LoginPresenter extends BasePresenter<Start.ILoginView> implements Start.ILoginPresenter {

    @Override
    public void login(String phone, String pwd, String open_id, String open_type, String open_nick,
                      String open_avatar, String open_sex) {

        Map<String, String> map = new HashMap<>();
        if (TextUtils.isEmpty(phone)) {
            //第三方登录
            map.put("open_id", open_id);
            map.put("open_type", open_type);
            map.put("open_nick", open_nick);
            map.put("open_avatar", open_avatar);
            map.put("open_sex", open_sex);
        } else {
            //手机号登录
            map.put("phone", phone);
            map.put("pwd", RSA.encode(pwd));
        }

        String jpushId;
        if (TextUtils.isEmpty(JPushReceiver.mRegistrationId)) {
            jpushId = JPushInterface.getRegistrationID(ValleyApp.getApp());
        } else {
            jpushId = JPushReceiver.mRegistrationId;
        }
        L.e("push_device_id-->" + jpushId);
        map.put("push_device_id", jpushId);

        map.put("os_version", Build.VERSION.RELEASE);
        map.put("phone_model", Build.MODEL);

        UISubscriber<LoginBean, Bean<LoginBean>, Start.ILoginView> subscriber =
                new UISubscriber<LoginBean, Bean<LoginBean>, Start.ILoginView>(mBaseView) {
                    @Override
                    public void onSuccess(Bean<LoginBean> loginBeanBean) {
                        super.onSuccess(loginBeanBean);
                        mBaseView.onLoginSuccess(loginBeanBean);
                    }
                };

        add(RRetrofit.create(StartService.class)
                .login(Param.map(map))
                .compose(Transform.<Bean<LoginBean>, Start.ILoginView>defaultSchedulers(mBaseView))
                .subscribe(subscriber));
    }
}

package com.hn.d.valley.start.mvp;

import com.angcyo.uiview.mvp.presenter.BasePresenter;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.TransformUtils;
import com.angcyo.uiview.net.rsa.RSA;
import com.hn.d.valley.base.Bean;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BeforeSubscriber;
import com.hn.d.valley.base.rx.UISubscriber;
import com.hn.d.valley.start.service.StartService;

import java.util.HashMap;
import java.util.Map;

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
public class Register2Presenter extends BasePresenter<Start.IRegister2View> implements Start.IRegister2Presenter {

    @Override
    public void register(String username, String pwd, String phone, String avatar, String sex, String code) {
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("pwd", RSA.encode(pwd));
        map.put("phone", phone);
        map.put("avatar", avatar);
        map.put("sex", sex);
        map.put("code", code);

        UISubscriber<String, Bean<String>, Start.IRegister2View> subscriber = new UISubscriber<String, Bean<String>, Start.IRegister2View>(mBaseView) {
            @Override
            public void onSuccess(Bean<String> stringBean) {
                super.onSuccess(stringBean);
                mBaseView.onRegisterSuccess(stringBean);
            }
        };

        mCompositeSubscription.add(RRetrofit.create(StartService.class)
                .register(Param.map(map))
                .compose(TransformUtils.<Bean<String>>defaultSchedulers())
                .doOnSubscribe(BeforeSubscriber.build(mBaseView))
                .doOnUnsubscribe(subscriber)
                .subscribe(subscriber));
    }

}
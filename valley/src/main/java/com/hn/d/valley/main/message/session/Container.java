package com.hn.d.valley.main.message.session;

import android.app.Activity;

import com.angcyo.uiview.base.UILayoutActivity;
import com.angcyo.uiview.container.ILayout;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/18 14:03
 * 修改人员：hewking
 * 修改时间：2017/05/18 14:03
 * 修改备注：
 * Version: 1.0.0
 */
public class Container {

    public final UILayoutActivity activity;
    public final String account;
    public final SessionTypeEnum sessionType;
    public final SessionProxy proxy;
    public final ILayout mLayout;

    public Container(UILayoutActivity activity, String account, SessionTypeEnum sessionType, ILayout layout, SessionProxy proxy) {
        this.activity = activity;
        this.account = account;
        this.sessionType = sessionType;
        this.proxy = proxy;
        this.mLayout = layout;
    }

}

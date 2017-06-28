package com.hn.d.valley.main.message.session;

import android.Manifest;
import android.view.View;

import com.hn.d.valley.R;
import com.hn.d.valley.bean.realm.AmapBean;
import com.hn.d.valley.main.other.AmapUIView;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/18 15:14
 * 修改人员：hewking
 * 修改时间：2017/05/18 15:14
 * 修改备注：
 * Version: 1.0.0
 */
public class LocationCommandItem extends CommandItemInfo {

    public LocationCommandItem() {
        this(R.drawable.location_xiaoxi_n, "位置");
    }

    public LocationCommandItem(int icoResId, String text) {
        super(icoResId, text);
    }

    @Override
    protected void onClick() {
        getContainer().activity.checkPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            //位置
                            getContainer().mLayout.startIView(new AmapUIView(new Action1<AmapBean>() {
                                @Override
                                public void call(AmapBean bean) {
                                    if (bean == null) {
                                        return;
                                    }
                                    IMMessage locationMessage = MessageBuilder.createLocationMessage(getContainer().account, getContainer().sessionType, bean.latitude, bean.longitude, bean.address);
                                    getContainer().proxy.sendMessage(locationMessage);
                                }
                            }, null, null, true));
                        }
                    }
                });


    }
}

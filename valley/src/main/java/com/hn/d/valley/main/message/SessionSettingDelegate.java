package com.hn.d.valley.main.message;

import android.support.v7.widget.SwitchCompat;

import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.utils.NetworkUtil;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.service.GroupChatService;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/16 17:44
 * 修改人员：hewking
 * 修改时间：2017/04/16 17:44
 * 修改备注：
 * Version: 1.0.0
 */
public class SessionSettingDelegate {

    List<String> toplist;

    private SessionSettingDelegate(){}

    private static class Holder {
        private static SessionSettingDelegate sInstance = new SessionSettingDelegate();
    }

    public static SessionSettingDelegate getInstance() {
        return Holder.sInstance;
    }

    public boolean checkTop (String sessionId) {
        if(toplist == null) {
            toplist = new ArrayList<>();
        }

        Iterator<String> it = toplist.iterator();
        for(;it.hasNext();) {
            String uid = it.next();
            if (uid.equals(sessionId)) {
                return true;
            }
        }
        fetchTopList(sessionId);
        return false;
    }

    public void fetchTopList(String sessionId) {
        RRetrofit.create(GroupChatService.class)
                .topList(Param.buildMap("sessionId:" + sessionId))
                .compose(Rx.transformerList(String.class))
                .subscribe(new BaseSingleSubscriber<List<String>>() {
                    @Override
                    public void onSucceed(List<String> bean) {
                        super.onSucceed(bean);
                        toplist = bean;
                    }

                    @Override
                    public void onNoNetwork() {
                        super.onNoNetwork();
                    }
                });
    }

    public void setTop(final String sessionId, SessionTypeEnum sessionType,String isTop) {
        String type = sessionType == SessionTypeEnum.Team ? "2" : "1";
        RRetrofit.create(GroupChatService.class)
                .setTop(Param.buildMap("to:" + sessionId,"is_top:" + isTop, "type:" + type))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onSucceed(String bean) {
                        super.onSucceed(bean);
                        fetchTopList(sessionId);
                    }

                    @Override
                    public void onNoNetwork() {
                        super.onNoNetwork();
                    }
                });
    }

    public void setMessageNotify(String sessionId , final boolean checkState, final SwitchCompat switchCompat) {
        if (!NetworkUtil.isNetAvailable(ValleyApp.getApp())) {
            T_.show("无网络！");
            switchCompat.setSelected(!checkState);
            return;
        }

        NIMClient.getService(FriendService.class).setMessageNotify(sessionId, checkState).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                if (checkState) {
//                    Toast.makeText(MessageInfoActivity.this, "开启消息提醒成功", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(MessageInfoActivity.this, "关闭消息提醒成功", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(int code) {
                if (code == 408) {
//                    Toast.makeText(MessageInfoActivity.this, R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(MessageInfoActivity.this, "on failed:" + code, Toast.LENGTH_SHORT).show();
                }
                switchCompat.setSelected(!checkState);
            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }


}

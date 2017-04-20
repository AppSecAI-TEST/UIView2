package com.hn.d.valley.main.message;

import android.support.v7.widget.SwitchCompat;

import com.angcyo.library.utils.L;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

    Set<String> remoteTopList;

    Set<String> localTopList;

    private SessionSettingDelegate() {
        remoteTopList = new HashSet<>();
        localTopList = new HashSet<>();
    }

    private static class Holder {
        private static SessionSettingDelegate sInstance = new SessionSettingDelegate();
    }

    public static SessionSettingDelegate getInstance() {
        return Holder.sInstance;
    }

    public boolean checkTop(String sessionId) {
        if (remoteTopList == null) {
            remoteTopList = new HashSet<>();
            fetchTopList();
        }

        if (localTopList == null) {
            localTopList = new HashSet<>();
        }

//        Iterator<String> it = remoteTopList.iterator();
//        for (; it.hasNext(); ) {
//            String uid = it.next();
//            if (uid.equals(sessionId)) {
//                return true;
//            }
//        }

        L.i("checkTop fetchTopList", remoteTopList.toString());
        L.i("checkTop localTopList", localTopList.toString());

        if (remoteTopList.contains(sessionId)) {
            return true;
        }

        if (localTopList.contains(sessionId)) {
            return true;
        }

        return false;
    }

    public void fetchTopList() {
        L.i("call fetchTopList");
        RRetrofit.create(GroupChatService.class)
                .topList(Param.buildMap())
                .compose(Rx.transformerList(String.class))
                .subscribe(new BaseSingleSubscriber<List<String>>() {
                    @Override
                    public void onSucceed(List<String> bean) {
                        super.onSucceed(bean);
                        remoteTopList = new HashSet<>(bean);
                        localTopList.addAll(remoteTopList);
                        L.i("fetchTopList", remoteTopList.toString());
                        L.i("localTopList", localTopList.toString());
                    }

                    @Override
                    public void onNoNetwork() {
                        super.onNoNetwork();
                    }
                });
    }

    public void setTop(final String sessionId, SessionTypeEnum sessionType, int isTop) {
        if (isTop == 1) {
            localTopList.add(sessionId);
        } else if (isTop == 0) {
            localTopList.remove(sessionId);
            if (remoteTopList.contains(sessionId)) {
                remoteTopList.remove(sessionId);
            }
        }

        L.i("settop fetchTopList", remoteTopList.toString());
        L.i("settop localTopList", localTopList.toString());

        String type = sessionType == SessionTypeEnum.Team ? "2" : "1";
        RRetrofit.create(GroupChatService.class)
                .setTop(Param.buildMap("to:" + sessionId, "is_top:" + isTop, "type:" + type))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onSucceed(String bean) {
                        super.onSucceed(bean);
                        fetchTopList();
                    }

                    @Override
                    public void onNoNetwork() {
                        super.onNoNetwork();
                    }
                });
    }

    public void setMessageNotify(String sessionId, final boolean checkState, final SwitchCompat switchCompat) {
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

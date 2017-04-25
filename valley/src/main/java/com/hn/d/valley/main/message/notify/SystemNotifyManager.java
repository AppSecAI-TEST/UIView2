package com.hn.d.valley.main.message.notify;

import com.angcyo.library.utils.L;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.friend.model.AddFriendNotify;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.msg.model.SystemMessage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/24 8:59
 * 修改人员：hewking
 * 修改时间：2017/04/24 8:59
 * 修改备注：
 * Version: 1.0.0
 */
public class SystemNotifyManager {

    private static String TAG = SystemNotifyManager.class.getSimpleName();

    private static final boolean MERGE_ADD_FRIEND_VERIFY = false; // 是否要合并好友申请，同一个用户仅保留最近一条申请内容（默认不合并）


    private SystemNotifyManager(){}

    private static class Holder {
        private static SystemNotifyManager sInstance = new SystemNotifyManager();
    }

    public static SystemNotifyManager getInstance() {
        return Holder.sInstance;
    }

    private List<SystemMessage> items = new ArrayList<>();

    private Set<String> addFriendVerifyRequestAccounts = new HashSet<>(); // 发送过好友申请的账号（好友申请合并用）


    public void registerSystemObserver(boolean register) {
        L.i(TAG,"registerSystemObserver " + register);
        NIMClient.getService(SystemMessageObserver.class).observeReceiveSystemMsg(systemMessageObserver, register);

//        NIMClient.getService(SystemMessageService.class).querySystemMessages(0,1000).setCallback(new RequestCallback<List<SystemMessage>>() {
//            @Override
//            public void onSuccess(List<SystemMessage> param) {
//                L.i(TAG,"querySystemMessages onSuccess" + param.toString());
//
//            }
//
//            @Override
//            public void onFailed(int code) {
//                L.i(TAG,"querySystemMessages onFailed" );
//
//            }
//
//            @Override
//            public void onException(Throwable exception) {
//                L.i(TAG,"querySystemMessages onException" );
//
//            }
//        });

    }

    Observer<SystemMessage> systemMessageObserver = new Observer<SystemMessage>() {
        @Override
        public void onEvent(SystemMessage systemMessage) {
            L.i(TAG,"systemMessageObserver : " + systemMessage.getContent());
            onIncomingMessage(systemMessage);
        }
    };

    /**
     * 新消息到来
     */
    private void onIncomingMessage(final SystemMessage message) {
        // 同一个账号的好友申请仅保留最近一条
        if (addFriendVerifyFilter(message)) {
            SystemMessage del = null;
            for (SystemMessage m : items) {
                if (m.getFromAccount().equals(message.getFromAccount()) && m.getType() == SystemMessageType.AddFriend) {
                    AddFriendNotify attachData = (AddFriendNotify) m.getAttachObject();
                    if (attachData != null && attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST) {
                        del = m;
                        break;
                    }
                }
            }

            if (del != null) {
                items.remove(del);
            }
        }

        items.add(0,message);
    }

    // 同一个账号的好友申请仅保留最近一条
    private boolean addFriendVerifyFilter(final SystemMessage msg) {
        if (!MERGE_ADD_FRIEND_VERIFY) {
            return false; // 不需要MERGE，不过滤
        }

        if (msg.getType() != SystemMessageType.AddFriend) {
            return false; // 不过滤
        }

        AddFriendNotify attachData = (AddFriendNotify) msg.getAttachObject();
        if (attachData == null) {
            return true; // 过滤
        }

        if (attachData.getEvent() != AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST) {
            return false; // 不过滤
        }

        if (addFriendVerifyRequestAccounts.contains(msg.getFromAccount())) {
            return true; // 过滤
        }

        addFriendVerifyRequestAccounts.add(msg.getFromAccount());
        return false; // 不过滤
    }

}

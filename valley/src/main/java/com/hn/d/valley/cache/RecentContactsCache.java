package com.hn.d.valley.cache;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.angcyo.library.utils.L;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.event.LastContactsEvent;
import com.hn.d.valley.bean.event.UpdateDataEvent;
import com.hn.d.valley.main.message.session.AitHelper;
import com.hn.d.valley.main.message.session.RecentContactsControl;
import com.hn.d.valley.nim.RNim;
import com.hn.d.valley.utils.Preconditions;
import com.hn.d.valley.utils.RBus;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/26 10:30
 * 修改人员：Robi
 * 修改时间：2016/12/26 10:30
 * 修改备注：
 * Version: 1.0.0
 */
public class RecentContactsCache implements ICache {

    private static final String TAG = RecentContactsControl.class.getSimpleName();

    List<RecentContact> mRecentContactList = new ArrayList<>();

    // 暂存消息，当RecentContact 监听回来时使用，结束后清掉
    private Map<String, Set<IMMessage>> cacheMessages = new HashMap<>();

    //会话列表改变监听
    private Observer<List<RecentContact>> mRecentContactObserver = new Observer<List<RecentContact>>() {
        @Override
        public void onEvent(List<RecentContact> recentContacts) {
            if (recentContacts == null || recentContacts.isEmpty()) {
                return;
            }

            if (mRecentContactList == null) {
                mRecentContactList = new ArrayList<>();
            }

            mRecentContactList.clear();

            onRecentContactChanged(recentContacts);

            List<RecentContact> blockContacts = RNim.service(MsgService.class).queryRecentContactsBlock();
            if (blockContacts == null || blockContacts.isEmpty()) {
                return;
            }
            mRecentContactList.addAll(blockContacts);
//            mRecentContactList.addAll(recentContacts);

            List<String> users = new ArrayList<>();
            for (RecentContact contact : recentContacts) {
                if (NimUserInfoCache.getInstance().getUserInfo(contact.getContactId()) == null) {
                    users.add(contact.getContactId());
                }
            }

            NimUserInfoCache.getInstance().fetchUserInfoFromRemote(users);

            L.d(TAG,"post updatedataevent mRecentContactObserver");
            RBus.post(Constant.TAG_UPDATE_RECENT_CONTACTS, new UpdateDataEvent());

            RBus.post(new LastContactsEvent(recentContacts.get(recentContacts.size() - 1)));
        }
    };

    private void onRecentContactChanged(List<RecentContact> recentContacts) {
        int index;
        for (RecentContact r : recentContacts) {
//            index = -1;
//            for (int i = 0; i < mRecentContactList.size(); i++) {
//                if (r.getContactId().equals(mRecentContactList.get(i).getContactId())
//                        && r.getSessionType() == (mRecentContactList.get(i).getSessionType())) {
//                    index = i;
//                    break;
//                }
//            }
//
//            if (index >= 0) {
//                mRecentContactList.remove(index);
//            }
//
//            mRecentContactList.add(r);
            L.d(RecentContactsCache.class.getSimpleName(),"unreadcount " + r.getUnreadCount());

            if (r.getSessionType() == SessionTypeEnum.Team && cacheMessages.get(r.getContactId()) != null) {
                AitHelper.setRecentContactAited(r, cacheMessages.get(r.getContactId()));
            }
        }
//        cacheMessages.clear();
    }

    //监听在线消息中是否有@我
    private Observer<List<IMMessage>> messageReceiverObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> imMessages) {

            L.i(TAG, "messageReceiverObserver onEvent " + imMessages.get(0).getContent());

            if (imMessages != null) {
                for (IMMessage imMessage : imMessages) {
                    if (!AitHelper.isAitMessage(imMessage)) {
                        continue;
                    }
                    Set<IMMessage> cacheMessageSet = cacheMessages.get(imMessage.getSessionId());
                    if (cacheMessageSet == null) {
                        cacheMessageSet = new HashSet<>();
                        cacheMessages.put(imMessage.getSessionId(), cacheMessageSet);
                    }
                    cacheMessageSet.add(imMessage);
                }
            }
        }
    };

    //会话列表被删除
    private Observer<RecentContact> mRecentContactDeleteObserver = new Observer<RecentContact>() {
        @Override
        public void onEvent(RecentContact recentContact) {
            if (recentContact != null) {
                for (RecentContact contact : mRecentContactList) {
                    if (TextUtils.equals(contact.getContactId(), recentContact.getContactId())) {
                        mRecentContactList.remove(contact);
                        break;
                    }
                }
            } else {
                mRecentContactList.clear();
            }
            RBus.post(Constant.TAG_UPDATE_RECENT_CONTACTS, new UpdateDataEvent());
        }
    };

    private RecentContactsCache() {
    }

    public static RecentContactsCache instance() {
        return Holder.instance;
    }

    @Override
    public void registerObservers(boolean register) {
        RNim.msgServiceObserve().observeRecentContact(mRecentContactObserver, register);
        RNim.msgServiceObserve().observeRecentContactDeleted(mRecentContactDeleteObserver, register);
        RNim.msgServiceObserve().observeReceiveMessage(messageReceiverObserver, register);
    }

    @Override
    public void buildCache() {
        RNim.queryRecentContacts(new RequestCallbackWrapper<List<RecentContact>>() {
            @Override
            public void onResult(int code, List<RecentContact> result, Throwable exception) {
                if (code == ResponseCode.RES_SUCCESS) {
                    if (result == null || result.size() == 0) {
                        return;
                    }
                    mRecentContactList.clear();
                    mRecentContactList.addAll(result);
                    RBus.post(Constant.TAG_UPDATE_RECENT_CONTACTS, new UpdateDataEvent());
                }
            }
        });
    }

    @Override
    public void clear() {
        mRecentContactList.clear();
    }

    public int getTotalUnreadCount() {
        return RNim.service(MsgService.class).getTotalUnreadCount();
    }

    public List<RecentContact> getRecentContactList() {
        return mRecentContactList;
    }

    /**
     * 获取被@的消息对象
     * @return
     */
    public Map<String, Set<IMMessage>> getCacheMessages() {
        return cacheMessages;
    }

    private static class Holder {
        static RecentContactsCache instance = new RecentContactsCache();
    }
}

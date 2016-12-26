package com.hn.d.valley.cache;

import android.text.TextUtils;

import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.event.UpdateDataEvent;
import com.hn.d.valley.nim.RNim;
import com.hn.d.valley.utils.RBus;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.util.ArrayList;
import java.util.List;

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
    List<RecentContact> mRecentContactList = new ArrayList<>();

    //会话列表改变监听
    private Observer<List<RecentContact>> mRecentContactObserver = new Observer<List<RecentContact>>() {
        @Override
        public void onEvent(List<RecentContact> recentContacts) {
            mRecentContactList.clear();
            mRecentContactList.addAll(RNim.service(MsgService.class).queryRecentContactsBlock());
//            mRecentContactList.addAll(recentContacts);
            RBus.post(Constant.TAG_UPDATE_RECENT_CONTACTS, new UpdateDataEvent());
        }
    };
    //会话列表被删除
    private Observer<RecentContact> mRecentContactDeleteObserver = new Observer<RecentContact>() {
        @Override
        public void onEvent(RecentContact recentContact) {
            for (RecentContact contact : mRecentContactList) {
                if (TextUtils.equals(contact.getContactId(), recentContact.getContactId())) {
                    mRecentContactList.remove(contact);
                    break;
                }
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
    }

    @Override
    public void buildCache() {
        RNim.queryRecentContacts(new RequestCallbackWrapper<List<RecentContact>>() {
            @Override
            public void onResult(int code, List<RecentContact> result, Throwable exception) {
                if (code == ResponseCode.RES_SUCCESS) {
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

    private static class Holder {
        static RecentContactsCache instance = new RecentContactsCache();
    }
}

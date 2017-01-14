package com.hn.d.valley.control;

import com.hn.d.valley.realm.RRealm;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：未读消息管理
 * 创建人员：Robi
 * 创建时间：2017/01/07 10:50
 * 修改人员：Robi
 * 修改时间：2017/01/07 10:50
 * 修改备注：
 * Version: 1.0.0
 */
public class UnreadMessageControl {

    /**
     * 获取所有消息未读的数量
     */
    public static int getUnreadCount() {
        int size = 0;
        Realm realm = null;
        try {
            realm = RRealm.getRealmInstance();
            size = realm.where(UnreadMessage.class).findAll().size();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return size;
    }

    /**
     * 通过消息id, 判断消息是否未读
     */
    public static boolean isMessageUnread(String messageId) {
        boolean result;

        Realm realm = null;
        try {
            realm = RRealm.getRealmInstance();
            result = realm.where(UnreadMessage.class).equalTo("messageUid", messageId).findAll().size() > 0;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return result;
    }


    public static int getMessageUnreadCount(String messageId) {
        int size = 0;
        Realm realm = null;
        try {
            realm = RRealm.getRealmInstance();
            size = realm.where(UnreadMessage.class).equalTo("messageUid", messageId).findAll().size() > 0 ? 1 : 0;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return size;
    }

    /**
     * 消息标记已读
     */
    public static void addMessageUnread(String messageId, String sessionId) {
        if (isMessageUnread(messageId)) {
            return;
        }
        RRealm.save(new UnreadMessage(messageId, sessionId));
    }

    /**
     * 移除已读消息
     */
    public static void removeMessageUnread(final String sessionId) {
        RRealm.where(new Action1<Realm>() {
            @Override
            public void call(Realm realm) {
                final RealmResults<UnreadMessage> realmResults = realm.where(UnreadMessage.class)
                        .equalTo("sessionId", sessionId).findAll();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realmResults.deleteAllFromRealm();
                    }
                });
            }
        });
    }
}

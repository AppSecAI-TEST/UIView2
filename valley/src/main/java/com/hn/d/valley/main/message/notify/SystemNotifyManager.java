package com.hn.d.valley.main.message.notify;

import android.text.TextUtils;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.utils.Json;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.event.AnnounceUpdateEvent;
import com.hn.d.valley.bean.event.GroupDissolveEvent;
import com.hn.d.valley.bean.event.UpdateDataEvent;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.utils.RBus;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.CustomNotification;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

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

    private SystemNotifyManager(){}

    private static class Holder {
        private static SystemNotifyManager sInstance = new SystemNotifyManager();
    }

    public static SystemNotifyManager getInstance() {
        return Holder.sInstance;
    }

    private List<CustomNotification> items = new ArrayList<>();

    public void registerCustomNotificationObserver(boolean register) {
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(customNotificationObserver, register);
    }

    Observer<CustomNotification> customNotificationObserver = new Observer<CustomNotification>() {
        @Override
        public void onEvent(CustomNotification customNotification) {
            L.i(TAG,"customNotificationObserver " + customNotification.getContent());

//            if (!items.contains(customNotification) && customNotification.getContent() != null) {
//                items.add(0, customNotification);
//            }
            parseNotification(customNotification);
        }
    };

    private void parseNotification(CustomNotification customNotification) {
        String content = customNotification.getContent();
        if(TextUtils.isEmpty(content)) {
            return;
        }

        BaseNotification baseNotification = Json.from(content,BaseNotification.class);
        switch (baseNotification.getExtend_type()) {
            case "group_announcement":

                notifyAnnouncementUpdate(customNotification, content);

                break;
            case "new_discuss":

                RBus.post(Constant.TAG_NO_READ_NUM, new UpdateDataEvent(1, 1));

                break;
            case "new_visitor":
                notifyNewVisitor();
                break;
            case "group_dismiss":
                notifyGroupDisslove(customNotification, content);
                break;
        }

    }

    private void notifyGroupDisslove(CustomNotification customNotification, String content) {
        RBus.post(new GroupDissolveEvent(customNotification.getSessionId(), Json.from(content,GroupDissolve.class)));
    }

    private void notifyAnnouncementUpdate(CustomNotification customNotification, String content) {
        RBus.post(new AnnounceUpdateEvent(customNotification.getSessionId(), Json.from(content,GroupAnnounceNotification.class)));
    }

    private void notifyNewVisitor() {
        // 设置新的访客通知
        final UserInfoBean userInfoBean = UserCache.instance().getUserInfoBean();
        RRealm.exe(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                L.i(TAG,"setNew_notification true");
                userInfoBean.setNew_visitor(true);
            }
        });
    }


}

package com.hn.d.valley.main.message.notify;

import android.text.TextUtils;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.utils.Json;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.event.AnnounceUpdateEvent;
import com.hn.d.valley.bean.event.GroupDissolveEvent;
import com.hn.d.valley.bean.event.UpdateDataEvent;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.cache.LogoutHelper;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.AdsControl;
import com.hn.d.valley.main.me.setting.MsgNotifySetting;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.utils.RBus;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.CustomNotification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.realm.Realm;

import static com.hn.d.valley.base.constant.Constant.POS_HOME;

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

    // temp values
    private Map<String,BaseNotification> mNotificationMap = new HashMap<>();

    private SystemNotifyManager(){}

    private static class Holder {
        private static SystemNotifyManager sInstance = new SystemNotifyManager();
    }

    public static SystemNotifyManager getInstance() {
        return Holder.sInstance;
    }

    private List<CustomNotification> items = new ArrayList<>();

    public void registerCustomNotificationObserver(boolean register) {
        L.i(TAG,"customNotificationObserver " + register);
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
        if (TextUtils.isEmpty(baseNotification.getExtend_type())) {
            return;
        }
        switch (baseNotification.getExtend_type()) {
            case SystemNotifyType.GROUP_ANNOUNCEMENT:

                notifyAnnouncementUpdate(customNotification, content);

                break;
            case SystemNotifyType.NEW_DISCUSS:
                // 设置是否发送圈子提醒
                if (MsgNotifySetting.instance().isCircleCNotify()) {
                    RBus.post(Constant.TAG_NO_READ_NUM, new UpdateDataEvent(1, POS_HOME));
                }
                break;
            case SystemNotifyType.NEW_VISITOR:
                notifyNewVisitor();
                break;
            case SystemNotifyType.GROUP_DISMISS:
                // 群被后台解散
                notifyGroupDisslove(customNotification, content);
                break;
            case SystemNotifyType.ADS_UPDATE:
                AdsControl.INSTANCE.updateAds();
                break;
            case SystemNotifyType.USER_FREEZE:
            case SystemNotifyType.USER_LOCK:
                // 用戶被封号 踢出下线
                //帐号被踢
                LogoutHelper.logout();
                RBus.post(StatusCode.FORBIDDEN);
                break;
            default:

        }

    }

    private void notifyGroupDisslove(CustomNotification customNotification, String content) {
        RBus.post(new GroupDissolveEvent(customNotification.getSessionId(), Json.from(content,GroupDissolve.class)));
    }

    private void notifyAnnouncementUpdate(CustomNotification customNotification, String content) {
        putNotification(customNotification.getSessionId(),Json.from(content,GroupAnnounceNotification.class));
        RBus.post(new AnnounceUpdateEvent(customNotification.getSessionId(), Json.from(content,GroupAnnounceNotification.class)));
    }

    private void putNotification(String key , BaseNotification notification) {
        mNotificationMap.put(key,notification);
    }

    public BaseNotification checkForKey(String key) {
        BaseNotification noti ;
        if (mNotificationMap.containsKey(key)) {
            noti = mNotificationMap.get(key);
            mNotificationMap.remove(key);
            return noti;
        } else {
            return null;
        }
    }

    public void onAppQuit() {
        if (!mNotificationMap.isEmpty()) {
            // 保存数据库
//            RRealm.instance().save();
        }
    }

    public void init() {
        // 从数据库中恢复数据 之后清空数据库
//        RRealm.instance().getRealm().where();
    }

    private void notifyNewVisitor() {
        // 设置新的访客通知
        final UserInfoBean userInfoBean = UserCache.instance().getUserInfoBean();
        if (userInfoBean == null) {
            return;
        }
        RRealm.exe(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                L.i(TAG,"setNew_notification true");
                userInfoBean.setNew_visitor(true);
            }
        });
    }


}

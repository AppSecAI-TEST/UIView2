package com.hn.d.valley.control;

import com.angcyo.uiview.RCrashHandler;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.net.RSubscriber;
import com.hn.d.valley.BuildConfig;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.cache.DataCacheManager;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.notify.SystemNotifyManager;
import com.hn.d.valley.nim.RNim;
import com.liulishuo.FDown;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/11 15:18
 * 修改人员：Robi
 * 修改时间：2017/01/11 15:18
 * 修改备注：
 * Version: 1.0.0
 */
public class MainControl {
    public static void onMainCreate() {
        DataCacheManager.buildDataCacheAsync();

        // 系统消息监听
        SystemNotifyManager.getInstance().registerCustomNotificationObserver(true);

        UserCache.instance()
                .fetchUserInfo()
                .subscribe(new RSubscriber<UserInfoBean>() {
                    @Override
                    public void onSucceed(UserInfoBean userInfoBean) {
                        if (userInfoBean != null) {
                            UserCache.instance().setUserInfoBean(userInfoBean);
                        }
                    }
                });
        TagsControl.getTags(null);
    }

    public static void onMainUnload() {
        SystemNotifyManager.getInstance().registerCustomNotificationObserver(false);

        FDown.unInit();
    }

    public static void onLoginOut() {
        RNim.logout();
        AutoLoginControl.setLogin(false);
    }

    public static void onLoginIn() {
        AutoLoginControl.setLogin(true);
    }

    public static void checkCrash(ILayout iLayout) {
        if (BuildConfig.SHOW_DEBUG) {
            RCrashHandler.QQ_GROUP_KEY = "oo8iBWHEAOxrj06LFKxcy2yDJTILXamC";
            RCrashHandler.checkCrash(iLayout);
        }
    }
}

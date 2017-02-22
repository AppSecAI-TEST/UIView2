package com.hn.d.valley.control;

import com.angcyo.uiview.net.RSubscriber;
import com.hn.d.valley.BuildConfig;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.cache.DataCacheManager;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.nim.RNim;

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
        onMainUnload(BuildConfig.DEBUG);
    }

    public static void onMainUnload(boolean quit) {
        if (quit) {
            RNim.logout();
        }
    }
}

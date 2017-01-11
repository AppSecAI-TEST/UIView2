package com.hn.d.valley.control;

import com.hn.d.valley.BuildConfig;
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
        UserCache.instance().updateUserInfo();
        TagsControl.getTags(null);
    }

    public static void onMainUnload() {
        if (BuildConfig.DEBUG) {
            RNim.logout();
        }
    }
}

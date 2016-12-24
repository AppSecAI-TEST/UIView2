package com.hn.d.valley;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.RApplication;
import com.hn.d.valley.nim.RNim;
import com.hn.d.valley.utils.RAmap;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;

import cn.jpush.android.api.JPushInterface;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：星期五 2016-12-16
 * 创建人员：Robi
 * 创建时间：2016/12/08 17:10
 * 修改人员：Robi
 * 修改时间：2016/12/08 17:10
 * 修改备注：
 * Version: 1.0.0
 */
public class ValleyApp extends RApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    protected void onInit() {
        L.init(BuildConfig.DEBUG, "dvalley");

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("valley.realm")
                .schemaVersion(1)
                .build();
        Realm.setDefaultConfiguration(config);

        CrashReport.setIsDevelopmentDevice(getApplicationContext(), BuildConfig.DEBUG);
        //CrashReport.initCrashReport(getApplicationContext(), "207e18ac24", BuildConfig.DEBUG);
        Bugly.init(getApplicationContext(), "207e18ac24", BuildConfig.DEBUG);

        CrashReport.setUserId(getIMEI());  //该用户本次启动后的异常日志用户ID都将是9527

          /*极光*/
        JPushInterface.setDebugMode(BuildConfig.DEBUG);
        JPushInterface.init(this);

        RNim.init();

        RAmap.init(this);
    }
}

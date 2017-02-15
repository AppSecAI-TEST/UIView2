package com.hn.d.valley;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.RApplication;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.storage.StorageUtil;
import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.hn.d.valley.bean.realm.LoginBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.nim.RNim;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.utils.RAmap;
import com.lzy.imagepicker.ImagePickerHelper;
import com.orhanobut.hawk.Hawk;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;

import cn.jpush.android.api.JPushInterface;
import rx.functions.Action1;

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
           /*sp持久化库*/
        Hawk.init(this)
                .build();
        RNim.init(this);
        super.onCreate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (BuildConfig.DEBUG) {
            T_.show("请注意, 内存过低!");
        }
        Glide.get(this).clearMemory();
        //Fresco.shutDown();
        if (Fresco.hasBeenInitialized()) {
            Fresco.getImagePipeline().clearMemoryCaches();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
//        if (BuildConfig.DEBUG) {
//            T_.show("请求释放内存..." + level);
//        }
        Glide.get(this).trimMemory(level);
        Glide.get(this).clearMemory();
        if (Fresco.hasBeenInitialized()) {
            Fresco.getImagePipeline().clearMemoryCaches();
        }
    }

    @Override
    protected void onInit() {
        L.init(BuildConfig.DEBUG, "dvalley");

        RRetrofit.DEBUG = BuildConfig.DEBUG;

        RRealm.init(this);

        CrashReport.setIsDevelopmentDevice(this, BuildConfig.DEBUG);
//        CrashReport.initCrashReport(this, "207e18ac24", false/*BuildConfig.DEBUG*/);
        Bugly.init(this, "207e18ac24", false);

        UserCache.instance().getLoginBeanObservable()
                .subscribe(new Action1<LoginBean>() {
                    @Override
                    public void call(LoginBean loginBean) {
                        CrashReport.setUserId(loginBean.getUid());  //该用户本次启动后的异常日志用户ID都将是9527
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        CrashReport.setUserId(String.valueOf(System.currentTimeMillis()));
                    }
                });

          /*极光*/
        JPushInterface.setDebugMode(BuildConfig.DEBUG);
        JPushInterface.init(this);

        RNim.initOnce(this);

        RAmap.init(this);

        ImagePickerHelper.init();

        StorageUtil.init(this, null);

        //腾讯TBS X5内核浏览器初始化
        QbSdk.initX5Environment(getApplicationContext(), null);
    }
}

package com.hn.d.valley;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.RApplication;
import com.angcyo.uiview.Root;
import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.utils.storage.StorageUtil;
import com.angcyo.umeng.UM;
import com.example.m3b.Audio;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.hn.d.valley.bean.realm.LoginBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.library.fresco.DraweeViewUtil;
import com.hn.d.valley.main.message.session.SessionHelper;
import com.hn.d.valley.nim.RNim;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.utils.RAmap;
import com.liulishuo.FDown;
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
 * 类的描述：星期五 2016-12-16 恐龙谷Application
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
    protected void onInit() {
        super.onInit();
        RRealm.init(this);
        FDown.init(this, false/*BuildConfig.DEBUG*/);
    }

    @Override
    protected void onAsyncInit() {
        Root.APP_FOLDER = "DValley";

        L.init(BuildConfig.DEBUG, "dvalley");

        Fresco.initialize(this);

        RRetrofit.DEBUG = BuildConfig.DEBUG;

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

        //RNim.initOnce(this);//移至MainActivity中初始化

        RAmap.init(this);

        ImagePickerHelper.init();

        StorageUtil.init(this, null);

        // 会话窗口的定制初始化。
        SessionHelper.init();

        if (BuildConfig.DEBUG) {
//            Stetho.initializeWithDefaults(this);
        }

        //腾讯TBS X5内核浏览器初始化
        QbSdk.initX5Environment(getApplicationContext(), null);

        Audio.init(this);

        UM.init(this, BuildConfig.DEBUG);

        UIBaseView.ENABLE_LAYOUT_CHANGE_ANIM = false;

        DraweeViewUtil.init(this);

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (Fresco.hasBeenInitialized()) {
            Fresco.getImagePipeline().clearMemoryCaches();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (Fresco.hasBeenInitialized()) {
            Fresco.getImagePipeline().clearMemoryCaches();
        }
    }
}

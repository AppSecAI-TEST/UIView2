package com.hn.d.valley;

import com.angcyo.github.utilcode.utils.Utils;
import com.angcyo.library.utils.L;
import com.angcyo.uidemo.layout.demo.view.HnCardView;
import com.angcyo.uidemo.layout.demo.view.HnDiceView;
import com.angcyo.uiview.RApplication;
import com.angcyo.uiview.Root;
import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.utils.storage.StorageUtil;
import com.angcyo.umeng.UM;
import com.example.m3b.Audio;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.github.moduth.blockcanary.BlockCanary;
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
        Utils.init(this);

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

        UM.init(this, BuildConfig.DEBUG);

        if (BuildConfig.DEBUG) {
//            Takt.stock(this)
//                    .seat(Seat.TOP_LEFT)
//                    .play();

            BlockCanary.install(this, new AppBlockCanaryContext()).start();
        }

        //getMainLooper().setMessageLogging(new LogPrinter(Log.ERROR, "Looooooooper...."));
    }

    @Override
    protected void onAsyncInit() {
        Root.APP_FOLDER = "DValley";

        L.init(BuildConfig.DEBUG, "dvalley");

        Fresco.initialize(this);

        RRetrofit.DEBUG = BuildConfig.DEBUG;

        CrashReport.setIsDevelopmentDevice(this, BuildConfig.DEBUG);
//        CrashReport.initCrashReport(this, "207e18ac24", false/*BuildConfig.DEBUG*/);
        Bugly.init(this, "1106097075", false);

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

        RNim.initOnce(this);//移至MainActivity中初始化

        RAmap.init(this);

        ImagePickerHelper.init();

        StorageUtil.init(this, null);

        // 会话窗口的定制初始化。
        SessionHelper.init();

        // 是否显示红包钱包等功能
//        DynamicFuncManager2.instance().load();

        if (BuildConfig.DEBUG) {
//            Stetho.initializeWithDefaults(this);
        }

        //腾讯TBS X5内核浏览器初始化
        QbSdk.initX5Environment(getApplicationContext(), null);

        Audio.init(this);

        UIBaseView.ENABLE_LAYOUT_CHANGE_ANIM = false;

        DraweeViewUtil.init(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        clearFresco();
    }

    protected void clearFresco() {
        try {
            HnCardView.Companion.clear();
            HnDiceView.Companion.clear();

            if (Fresco.hasBeenInitialized()) {
                ImagePipeline imagePipeline = Fresco.getImagePipeline();
                if (imagePipeline != null) {
                    imagePipeline.clearMemoryCaches();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        clearFresco();
    }

    @Override
    public void onTerminate() {
//        if (BuildConfig.SHOW_DEBUG) {
//            Takt.finish();
//        }
        super.onTerminate();
    }
}

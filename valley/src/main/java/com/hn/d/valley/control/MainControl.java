package com.hn.d.valley.control;

import android.view.View;

import com.angcyo.github.utilcode.utils.AppUtils;
import com.angcyo.uiview.RCrashHandler;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.net.REmptySubscriber;
import com.angcyo.uiview.net.RSubscriber;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.utils.ProgressNotify;
import com.hn.d.valley.BuildConfig;
import com.hn.d.valley.R;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.activity.HnUIMainActivity;
import com.hn.d.valley.bean.VersionBean;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.cache.DataCacheManager;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.notify.SystemNotifyManager;
import com.hn.d.valley.nim.RNim;
import com.hn.d.valley.utils.RAmap;
import com.liulishuo.FDown;
import com.liulishuo.FDownListener;
import com.liulishuo.filedownloader.BaseDownloadTask;

import java.util.concurrent.TimeUnit;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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

        Rx.interval(5l, TimeUnit.MINUTES)
                .map(new Func1<Long, Long>() {
                    @Override
                    public Long call(Long aLong) {
                        RAmap.startLocation(true);
                        return aLong;
                    }
                })
                .subscribeOn(Schedulers.computation())
                .subscribe(new REmptySubscriber<Long>());
    }

    public static void onMainUnload() {
        SystemNotifyManager.getInstance().registerCustomNotificationObserver(false);

        FDown.unInit();

        RAmap.stopLocation();
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

    public static void checkVersion(final ILayout layout) {
        //版本检测
        VersionControl.INSTANCE.checkVersion(new Function1<VersionBean, Unit>() {
            @Override
            public Unit invoke(final VersionBean versionBean) {
                UIDialog.build()
                        .setDialogTitle("发现新版本:" + versionBean.getVersion())
                        .setDialogContent(versionBean.getDetail())
                        .setOkText(
                                VersionControl.INSTANCE.isFileDowned() ? "安装" : "立即下载"
                        )
                        .setCancelText(versionBean.getForceUpdate() ? "" : "下次再说")
                        .setCancelClick(new UIDialog.OnDialogClick() {
                            @Override
                            public void onDialogClick(UIDialog dialog, View clickView) {
                                dialog.setAutoFinishDialog(true);
                            }
                        })
                        .setOkClick(new UIDialog.OnDialogClick() {
                            @Override
                            public void onDialogClick(final UIDialog dialog, View clickView) {
                                dialog.setAutoFinishDialog(false);

                                if (VersionControl.INSTANCE.isFileDowned()) {
                                    AppUtils.installApp(ValleyApp.getApp(),
                                            VersionControl.INSTANCE.getTargetFile());
                                } else {
                                    dialog.setCancelText("");
                                    dialog.setOkText("下载中...");
                                    VersionControl.INSTANCE.downFile(new FDownListener() {
                                        @Override
                                        public void onCompleted(BaseDownloadTask task) {
                                            super.onCompleted(task);
                                            dialog.setOkText("安装");
                                            AppUtils.installApp(ValleyApp.getApp(),
                                                    VersionControl.INSTANCE.getTargetFile());

                                            showProgressNotify(100);
                                        }

                                        @Override
                                        public void onProgress(BaseDownloadTask task, int soFarBytes, int totalBytes, float progress) {
                                            super.onProgress(task, soFarBytes, totalBytes, progress);
                                            dialog.setIncertitudeProgress(totalBytes == -1);
                                            dialog.setProgress((int) progress);

                                            //L.d("下载进度:" + task.getUrl() + ":" + VersionControl.INSTANCE.getTargetFile().getAbsolutePath() + " -> total:" + totalBytes + " :" + progress);

                                            showProgressNotify((int) progress);
                                        }
                                    });
                                }
                            }
                        })
                        .setCanCanceledOnOutside(false)
                        .setCanCancel(!versionBean.getForceUpdate())
                        .showDialog(layout);
                return null;
            }
        });
    }

    private static void showProgressNotify(int progress) {
        ProgressNotify.instance()
                .setClickActivity(HnUIMainActivity.class)
                .setTargetFilePath(VersionControl.INSTANCE.getTargetFile().getAbsolutePath())
                .show(ValleyApp.getApp().getResources().getString(R.string.app_name), R.drawable.logo, progress);
    }
}

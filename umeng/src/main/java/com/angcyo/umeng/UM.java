package com.angcyo.umeng;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.UmengTool;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/05/18 10:00
 * 修改人员：Robi
 * 修改时间：2017/05/18 10:00
 * 修改备注：
 * Version: 1.0.0
 */
public class UM {

    private static Application sApplication;

    /**
     * 请记得调用初始化方法
     */
    public static void init(Application application, boolean debug) {
        sApplication = application;

        Config.DEBUG = debug;

        UMShareAPI.get(sApplication);

        initPlatformConfig();
    }

    static void initPlatformConfig() {
        try {
            ApplicationInfo applicationInfo = sApplication.getPackageManager()
                    .getApplicationInfo(sApplication.getPackageName(), PackageManager.GET_META_DATA);

            String qq_id = String.valueOf(applicationInfo.metaData.get("QQ_ID"));
            String qq_key = String.valueOf(applicationInfo.metaData.get("QQ_KEY"));
            String wx_id = String.valueOf(applicationInfo.metaData.get("WX_ID"));
            String wx_key = String.valueOf(applicationInfo.metaData.get("WX_KEY"));

            PlatformConfig.setWeixin(wx_id, wx_key);
            PlatformConfig.setQQZone(qq_id, qq_key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * QQ 不能分享纯文本
     */
    public static void shareText(Activity activity, SHARE_MEDIA shareMedia,
                                 String shareText, UMShareListener listener) {
        new ShareAction(activity)
                .setPlatform(shareMedia)
                .withText(shareText)
                .setCallback(listener)
                .share();
    }

    public static void shareImage(Activity activity, SHARE_MEDIA shareMedia,
                                  String imageUrl, int thumbRes,
                                  UMShareListener listener) {
        UMImage umImage = new UMImage(activity, imageUrl);
        UMImage umThumb = new UMImage(activity, thumbRes);
        umImage.setThumb(umThumb);
        new ShareAction(activity)
                .setPlatform(shareMedia)
                .withMedia(umImage)
                .setCallback(listener)
                .share();
    }

    public static void checkQQ(Activity activity) {
        UmengTool.checkQQ(activity);
    }

    public static void checkWX(Activity activity) {
        UmengTool.checkWx(activity);
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        UMShareAPI.get(sApplication).onActivityResult(requestCode, resultCode, data);
    }

    public static void onDestroy() {
        UMShareAPI.get(sApplication).release();
    }
}

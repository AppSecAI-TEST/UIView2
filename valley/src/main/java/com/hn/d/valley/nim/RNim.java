package com.hn.d.valley.nim;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.text.TextUtils;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.RApplication;
import com.angcyo.uiview.utils.ScreenUtil;
import com.hn.d.valley.R;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.cache.DataCacheManager;
import com.hn.d.valley.cache.LogoutHelper;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.start.SplashActivity;
import com.hn.d.valley.utils.RBus;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;

import java.util.List;

import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/23 17:42
 * 修改人员：Robi
 * 修改时间：2016/12/23 17:42
 * 修改备注：
 * Version: 1.0.0
 */
public class RNim {
    public static void init(ValleyApp app) {
        NIMClient.init(app, loginInfo(), options());
    }

    public static void initOnce(Application application) {
        /*云信登录状态监听*/
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(
                new Observer<StatusCode>() {
                    public void onEvent(StatusCode status) {
                        L.w("User status changed to: " + status);
                        if (status.wontAutoLogin()) {
                            // 被踢出、账号被禁用、密码错误等情况，自动登录失败，需要返回到登录界面进行重新登录操作
                        }
                        if (status == StatusCode.KICKOUT || status == StatusCode.KICK_BY_OTHER_CLIENT) {
                            //帐号被踢
                            LogoutHelper.logout();
                        }
                        RBus.post(status);
                    }
                }, true);

        /*消息通知打开*/
        NIMClient.toggleNotification(true);

        //注册数据变化监听
        DataCacheManager.observeSDKDataChanged(true);

        if (!TextUtils.isEmpty(UserCache.getUserAccount())) {
            DataCacheManager.buildDataCache(); // build data cache on auto login
        }

        ScreenUtil.init(application);
    }


    // 如果返回值为 null，则全部使用默认参数。
    private static SDKOptions options() {
        RApplication application = ValleyApp.getApp();
        SDKOptions options = new SDKOptions();

        // 如果将新消息通知提醒托管给 SDK 完成，需要添加以下配置。否则无需设置。
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        config.notificationEntrance = SplashActivity.class; // 点击通知栏跳转到该Activity
        config.notificationSmallIconId = R.drawable.login_logo;
        // 呼吸灯配置
        config.ledARGB = Color.GREEN;
        config.ledOnMs = 1000;
        config.ledOffMs = 1500;
        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://com.netease.nim.demo/raw/msg";
        options.statusBarNotificationConfig = config;

        // 配置保存图片，文件，log 等数据的目录
        // 如果 options 中没有设置这个值，SDK 会使用下面代码示例中的位置作为 SDK 的数据目录。
        // 该目录目前包含 log, file, image, audio, video, thumb 这6个目录。
        // 如果第三方 APP 需要缓存清理功能， 清理这个目录下面个子目录的内容即可。
        String sdkPath = Environment.getExternalStorageDirectory() + "/" + application.getPackageName() + "/nim";
        options.sdkStorageRootPath = sdkPath;

        // 配置数据库加密秘钥
        options.databaseEncryptKey = "NETEASE";

        // 配置是否需要预下载附件缩略图，默认为 true
        options.preloadAttach = true;

        // 配置附件缩略图的尺寸大小。表示向服务器请求缩略图文件的大小
        // 该值一般应根据屏幕尺寸来确定， 默认值为 Screen.width / 2
        options.thumbnailSize = application.getResources().getDisplayMetrics().widthPixels / 2;

        // 定制通知栏提醒文案（可选，如果不定制将采用SDK默认文案）
        //options.messageNotifierCustomization = messageNotifierCustomization;

        // 在线多端同步未读数
        options.sessionReadAck = true;

        // 用户资料提供者, 目前主要用于提供用户资料，用于新消息通知栏中显示消息来源的头像和昵称
        options.userInfoProvider = new UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String account) {
                return null;
            }

            @Override
            public int getDefaultIconResId() {
                return R.drawable.login_logo;
            }

            @Override
            public Bitmap getTeamIcon(String tid) {
                return null;
            }

            @Override
            public Bitmap getAvatarForMessageNotifier(String account) {
                return null;
            }

            @Override
            public String getDisplayNameForMessageNotifier(String account, String sessionId,
                                                           SessionTypeEnum sessionType) {
                return null;
            }
        };
        return options;
    }

    // 如果已经存在用户登录信息，返回LoginInfo，否则返回null即可
    private static LoginInfo loginInfo() {
        String account = UserCache.getUserAccount();
        String token = UserCache.getUserToken();
        L.i("account:" + account + " token:" + token);
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }


    /**
     * 自动登录是否成功了
     */
    public static boolean isAutoLoginSucceed() {
        StatusCode status = NIMClient.getStatus();
        return status == StatusCode.LOGINED;
    }

    /**
     * 登出云信
     */
    public static void logout() {
        NIMClient.getService(AuthService.class).logout();
    }

    /**
     * 测试模式下的登录方法
     */
    public static void debugLogin(final Action1<Boolean> action1) {
        login("50015", "725161648c0116d850e839d22ff69f0b", new RequestCallbackWrapper<LoginInfo>() {
            @Override
            public void onResult(int code, LoginInfo result, Throwable exception) {
                action1.call(code == ResponseCode.RES_SUCCESS);
            }
        });
    }

    /**
     * 登入云信
     */
    public static AbortableFuture<LoginInfo> login(String account, String token, RequestCallbackWrapper<LoginInfo> callback) {
        L.e("登入云信:" + account + " " + token);
        AbortableFuture<LoginInfo> login = NIMClient.getService(AuthService.class).login(new LoginInfo(account, token));
        login.setCallback(callback);
        return login;
    }

    /**
     * 删除会话
     */
    public static void deleteRecentContact(final RecentContact recent) {
        NIMClient.getService(MsgService.class).deleteRecentContact(recent);
        //删除历史消息
        NIMClient.getService(MsgService.class).clearChattingHistory(recent.getContactId(), recent.getSessionType());
    }

    /**
     * 添加会话tag
     */
    public static void addRecentContactTag(final RecentContact recent, long tag) {
        tag = recent.getTag() | tag;
        recent.setTag(tag);
        NIMClient.getService(MsgService.class).updateRecent(recent);
    }

    /**
     * 移除会话tag
     */
    public static void removeRecentContactTag(final RecentContact recent, long tag) {
        tag = recent.getTag() & ~tag;
        recent.setTag(tag);
        NIMClient.getService(MsgService.class).updateRecent(recent);
    }

    /**
     * 会话tag是否存在
     */
    public static boolean isRecentContactTag(final RecentContact recent, long tag) {
        return (recent.getTag() & tag) == tag;
    }

    /**
     * 查询会话列表.
     */
    public static void queryRecentContacts(final RequestCallbackWrapper<List<RecentContact>> callback) {
        NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(callback);
    }
}

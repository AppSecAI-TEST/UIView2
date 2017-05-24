package com.hn.d.valley.main.message.uinfo;

import com.angcyo.library.utils.L;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.cache.TeamDataCache;
import com.hn.d.valley.cache.UserCache;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.util.List;

public class UserInfoHelper {

    private static UserInfoObservable userInfoObservable;

    // 获取用户显示在标题栏和最近联系人中的名字
    public static String getUserTitleName(String id, SessionTypeEnum sessionType) {
        if (sessionType == SessionTypeEnum.P2P) {
            if (UserCache.getUserAccount().equals(id)) {
                return "我的电脑";
            } else {
                return NimUserInfoCache.getInstance().getUserDisplayName(id);
            }
        }  else if (sessionType == SessionTypeEnum.Team) {
            return TeamDataCache.getInstance().getTeamName(id);
        }
        return id;
    }

    /**
     * 注册用户资料变化观察者。<br>
     *     注意：不再观察时(如Activity destroy后)，要unregister，否则会造成资源泄露
     * @param observer 观察者
     */
    public static void registerObserver(UserInfoObservable.UserInfoObserver observer) {
        if (userInfoObservable == null) {
            userInfoObservable = new UserInfoObservable(ValleyApp.getApp().getApplicationContext());
        }
        userInfoObservable.registerObserver(observer);
    }

    /**
     * 注销用户资料变化观察者。
     * @param observer 观察者
     */
    public static void unregisterObserver(UserInfoObservable.UserInfoObserver observer) {
        if (userInfoObservable != null) {
            userInfoObservable.unregisterObserver(observer);
        }
    }

    /**
     * 当用户资料发生改动时，请调用此接口，通知更新UI
     * @param accounts 有用户信息改动的帐号列表
     */
    public static void notifyChanged(List<String> accounts) {
        L.i("notifyChanged  " + accounts);
        if (userInfoObservable != null) {
            userInfoObservable.notifyObservers(accounts);
        }
    }
}

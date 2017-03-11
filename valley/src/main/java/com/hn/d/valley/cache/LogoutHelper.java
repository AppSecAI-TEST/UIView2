package com.hn.d.valley.cache;


import com.hn.d.valley.nim.RNim;

/**
 * 注销帮助类
 * Created by huangjun on 2015/10/8.
 */
public class LogoutHelper {
    public static void logout() {
        // 清理缓存&注销监听&清除状态
        RNim.logout();
        DataCacheManager.clearDataCache();
//        ChatRoomHelper.logout();
//        DemoCache.clear();
//        LoginSyncDataStatusObserver.getInstance().reset();
//        DropManager.getInstance().destroy();
    }
}

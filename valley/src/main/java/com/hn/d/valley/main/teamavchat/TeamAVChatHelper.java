package com.hn.d.valley.main.teamavchat;

import android.widget.Toast;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.utils.T;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.TimeUtil;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.avchat.AVChatProfile;
import com.hn.d.valley.main.teamavchat.test.teamavchat.activity.TeamAVChatActivity;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.constant.LoginSyncStatus;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.CustomNotification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hzchenkang on 2017/5/5.
 */

public class TeamAVChatHelper {

    private static final String KEY_ID = "id";
    private static final String KEY_MEMBER = "members";
    private static final String KEY_TID = "teamId";
    private static final String KEY_RID = "room";
    private static final String KEY_TNAME = "teamName";

    private static final long OFFLINE_EXPIRY = 45 * 1000;

    private static final int ID = 3;

    private boolean isTeamAVChatting = false;
    private boolean isSyncComplete = true; // 未开始也算同步完成，可能存在不启动同步的情况

    public String buildContent(String roomName, String teamID, List<String> accounts, String teamName) {
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        try{
            json.put(KEY_ID, ID);
            array.put(UserCache.getUserAccount());
            for (String account : accounts) {
                array.put(account);
            }
            json.put(KEY_MEMBER, array);
            json.put(KEY_TID, teamID);
            json.put(KEY_RID, roomName);
            json.put(KEY_TNAME, teamName);
        }catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }

    private JSONObject parseContentJson(CustomNotification notification) throws JSONException {
        if (notification != null) {
            String content = notification.getContent();
            return new JSONObject(content);
        }
        return null;
    }

    private boolean isTeamAVChatInvite(JSONObject json) {
        if (json != null) {
            int id = json.optInt(KEY_ID);
            return id == ID;
        }
        return false;
    }

    /**
     * 监听自定义通知消息，id = 3 是群视频邀请
     *
     * @param register
     */

    private Observer<CustomNotification> customNotificationObserver = new Observer<CustomNotification>() {
        @Override
        public void onEvent(CustomNotification customNotification) {
            try {
                JSONObject jsonObject = parseContentJson(customNotification);
                // 收到群视频邀请
                if (isTeamAVChatInvite(jsonObject)) {
                    final String roomName = jsonObject.getString(KEY_RID);
                    final String teamId = jsonObject.getString(KEY_TID);
                    JSONArray accountArray = jsonObject.getJSONArray(KEY_MEMBER);
                    final ArrayList<String> accounts = new ArrayList<>();
                    final String teamName = jsonObject.getString(KEY_TNAME);
                    if (accountArray != null) {
                        for ( int i = 0 ; i < accountArray.length() ; i++) {
                            accounts.add(accountArray.optString(i));
                        }
                    }

                    // 接收到群视频邀请，启动来点界面
                    L.i("receive team video chat notification " + teamId + " room " + roomName);
                    if (isTeamAVChatting || AVChatProfile.getInstance().isAVChatting()) {
                        L.i("cancel launch team av chat isTeamAVChatting = " + isTeamAVChatting);
                        T_.show("正在进行视频通话");
                        return;
                    }
                    L.i("isSyncComplete = " + isSyncComplete);
                    if (isSyncComplete || !checkOfflineOutTime(customNotification)) {
                        isTeamAVChatting = true;
//                        launchActivity(teamId, roomName, accounts, teamName);
                          TeamAVChatActivity.startActivity(ValleyApp.getApp(), true, teamId, roomName, accounts, teamName);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private void launchActivity(final String teamId, final String roomName, final ArrayList<String> accounts, final String teamName) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                // 欢迎界面正在运行，则等MainActivity启动之后再启动，否则直接启动 TeamAVChatActivity
//                if (!UserCache.isMainTaskLaunching()) {
//                    TeamAVChatActivity.startActivity(ValleyApp.getApp(), true, teamId, roomName, accounts, teamName);
//                } else {
                    L.i("launch TeamAVChatActivity delay for WelComeActivity is Launching");
//                    launchActivity(teamId, roomName, accounts, teamName);
//                }
            }
        };

        Handlers.sharedHandler(ValleyApp.getApp()).postDelayed(r, 200);
    }

    private Observer<LoginSyncStatus> loginSyncStatusObserver = new Observer<LoginSyncStatus>() {
        @Override
        public void onEvent(LoginSyncStatus loginSyncStatus) {
            isSyncComplete = (loginSyncStatus == LoginSyncStatus.SYNC_COMPLETED ||
                    loginSyncStatus == LoginSyncStatus.NO_BEGIN);
        }
    };

    public boolean checkOfflineOutTime(CustomNotification notification) {
        // 时间差在45s内，考虑本地时间误差，条件适当放宽
        long time = TimeUtil.currentTimeMillis() - notification.getTime();
        L.i("rev offline team AVChat request time = " + time);
        return time > OFFLINE_EXPIRY || time < -OFFLINE_EXPIRY;
    }

    public void setTeamAVChatting(boolean teamAVChatting) {
        isTeamAVChatting = teamAVChatting;
    }

    public boolean isTeamAVChatting() {
        return isTeamAVChatting;
    }

    public void registerObserver(boolean register) {
        NIMClient.getService(AuthServiceObserver.class).observeLoginSyncDataStatus(loginSyncStatusObserver, register);
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(customNotificationObserver, register);
    }

    public static TeamAVChatHelper sharedInstance() {
        return InstanceHolder.teamAVChatHelper;
    }

    private static class InstanceHolder {
        private final static TeamAVChatHelper teamAVChatHelper = new TeamAVChatHelper();
    }
}

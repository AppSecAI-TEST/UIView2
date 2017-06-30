package com.hn.d.valley.main.message.session;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIBaseRxView;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.string.StringUtil;
import com.hn.d.valley.R;
import com.hn.d.valley.cache.SimpleCallback;
import com.hn.d.valley.cache.TeamDataCache;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.avchat.AVChatProfile;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.message.groupchat.BaseContactSelectAdapter;
import com.hn.d.valley.main.message.groupchat.GroupInfoUIVIew;
import com.hn.d.valley.main.message.groupchat.GroupMemberItem;
import com.hn.d.valley.main.message.groupchat.GroupMemberSelectUIVIew;
import com.hn.d.valley.main.message.groupchat.RequestCallback;
import com.hn.d.valley.main.message.groupchat.TeamCreateHelper;
import com.hn.d.valley.main.teamavchat.TeamAVChatHelper;
import com.hn.d.valley.main.teamavchat.test.teamavchat.activity.TeamAVChatActivity;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.model.AVChatChannelInfo;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.CustomNotificationConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import rx.functions.Action3;

/**
 * Created by hzchenkang on 2017/5/3.
 */

public class TeamAVChatCommandItem extends AVChatCommandItem {

    private static final int MAX_INVITE_NUM = 8;

    // private String teamID;

    private LaunchTransaction transaction;
    private String gid;

    public void setGid(String gid) {
        this.gid = gid;
    }

    public TeamAVChatCommandItem(AVChatType avChatType) {
        super(avChatType);
    }

    @Override
    protected void onClick() {
        if (AVChatProfile.getInstance().isAVChatting()) {
            T_.show("正在进行P2P视频通话，请先退出");
            return;
        }

        if (TeamAVChatHelper.sharedInstance().isTeamAVChatting()) {
            // 视频通话界面正在运行，singleTop所以直接调起来
            Intent localIntent = new Intent();
            localIntent.setClass(getContainer().activity, TeamAVChatActivity.class);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            getContainer().activity.startActivity(localIntent);
            return;
        }

        if (transaction != null) {
            return;
        }

        final String tid = getContainer().account;
        if (TextUtils.isEmpty(tid)) {
            return;
        }
        transaction = new LaunchTransaction();
        transaction.setTeamID(tid);

        // load 一把群成员
        TeamDataCache.getInstance().fetchTeamMemberList(tid, new SimpleCallback<List<TeamMember>>() {
            @Override
            public void onResult(boolean success, List<TeamMember> result) {
                // 检查下 tid 是否相等
                if (!checkTransactionValid()) {
                    return;
                }
                if (success && result != null) {
                    if (result.size() < 2) {
                        transaction = null;
                        T_.show(getContainer().activity.getString(R.string.t_avchat_not_start_with_less_member));
                    } else {
                        if (TextUtils.isEmpty(gid)) {
                            return;
                        }
                        BaseContactSelectAdapter.Options option = new BaseContactSelectAdapter.Options(RModelAdapter.MODEL_MULTI,9,true,true,false);
                        GroupMemberSelectUIVIew.start(getContainer().mLayout,option
                                , null,gid, new Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback>() {
                                    @Override
                                    public void call(UIBaseRxView uiBaseDataView, final List<AbsContactItem> absContactItems, final RequestCallback requestCallback) {
                                        requestCallback.onSuccess(null);

                                        ArrayList<String> accounts = new ArrayList<>();
                                        for (AbsContactItem item : absContactItems) {
                                            GroupMemberItem member = (GroupMemberItem) item;
                                            accounts.add(member.getMemberBean().getUserId());
                                        }
                                        //自己添加第一个位置
//                                        accounts.add(0,UserCache.getUserAccount());
                                        if (accounts.size() < 1) {
                                            onSelectedAccountFail();
                                        }
                                        onSelectedAccountsResult(accounts);
                                    }
                                });

                    }
                }
            }
        });
    }

    public void onSelectedAccountFail() {
        transaction = null;
    }

    public void onSelectedAccountsResult(final ArrayList<String> accounts) {
        L.i("start teamVideo " + getContainer().account + " accounts = " + accounts);

        if (!checkTransactionValid()) {
            return;
        }

        final String roomName = StringUtil.get32UUID();
        L.i("create room " + roomName);
        // 创建房间
        AVChatManager.getInstance().createRoom(roomName, null, new AVChatCallback<AVChatChannelInfo>() {
            @Override
            public void onSuccess(AVChatChannelInfo avChatChannelInfo) {
                L.i("create room " + roomName + " success !");
                if (!checkTransactionValid()) {
                    return;
                }
                onCreateRoomSuccess(roomName, accounts);
                transaction.setRoomName(roomName);

                String teamName = TeamDataCache.getInstance().getTeamName(transaction.getTeamID());

                TeamAVChatHelper.sharedInstance().setTeamAVChatting(true);
                TeamAVChatActivity.startActivity(getContainer().activity, false, transaction.getTeamID(), roomName, accounts, teamName);
                transaction = null;
            }

            @Override
            public void onFailed(int code) {
                if (!checkTransactionValid()) {
                    return;
                }
                onCreateRoomFail();
            }

            @Override
            public void onException(Throwable exception) {
                if (!checkTransactionValid()) {
                    return;
                }
                onCreateRoomFail();
            }
        });
    }

    private boolean checkTransactionValid() {
        if (transaction == null) {
            return false;
        }
        if (transaction.getTeamID() == null || !transaction.getTeamID().equals(getContainer().account)) {
            transaction = null;
            return false;
        }
        return true;
    }


    private void onCreateRoomSuccess(String roomName, List<String> accounts) {
        String teamID = transaction.getTeamID();
        // 在群里发送tip消息
        IMMessage message = MessageBuilder.createTipMessage(teamID, SessionTypeEnum.Team);
        CustomMessageConfig tipConfig = new CustomMessageConfig();
        tipConfig.enableHistory = false;
        tipConfig.enableRoaming = false;
        tipConfig.enablePush = false;
        String teamNick = TeamDataCache.getInstance().getDisplayNameWithoutMe(teamID, UserCache.getUserAccount());
        message.setContent(teamNick + getContainer().activity.getString(R.string.t_avchat_start));
        message.setConfig(tipConfig);
        getContainer().proxy.sendMessage(message);
        // 对各个成员发送点对点自定义通知
        String teamName = TeamDataCache.getInstance().getTeamName(transaction.getTeamID());
        String content = TeamAVChatHelper.sharedInstance().buildContent(roomName, teamID, accounts, teamName);
        CustomNotificationConfig config = new CustomNotificationConfig();
        config.enablePush = true;
        config.enablePushNick = false;
        config.enableUnreadCount = true;

        for (String account : accounts) {
            CustomNotification command = new CustomNotification();
            command.setSessionId(account);
            command.setSessionType(SessionTypeEnum.P2P);
            command.setConfig(config);
            command.setContent(content);
            command.setApnsText(teamNick + getContainer().activity.getString(R.string.t_avchat_push_content));

            command.setSendToOnlineUserOnly(false);
            NIMClient.getService(MsgService.class).sendCustomNotification(command);
        }
    }

    private void onCreateRoomFail() {
        // 本地插一条tip消息
        IMMessage message = MessageBuilder.createTipMessage(transaction.getTeamID(), SessionTypeEnum.Team);
        message.setContent(getContainer().activity.getString(R.string.t_avchat_create_room_fail));
        message.setStatus(MsgStatusEnum.success);
        NIMClient.getService(MsgService.class).saveMessageToLocal(message, true);
    }

    private class LaunchTransaction implements Serializable {
        private String teamID;
        private String roomName;

        public String getRoomName() {
            return roomName;
        }

        public String getTeamID() {
            return teamID;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public void setTeamID(String teamID) {
            this.teamID = teamID;
        }
    }
}

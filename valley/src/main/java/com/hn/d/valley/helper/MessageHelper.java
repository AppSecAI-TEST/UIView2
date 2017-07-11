package com.hn.d.valley.helper;

import com.hn.d.valley.cache.TeamDataCache;
import com.hn.d.valley.cache.UserCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * Created by hzxuwen on 2016/8/19.
 */
public class MessageHelper {

    public static MessageHelper getInstance() {
        return InstanceHolder.instance;
    }

    // 消息撤回本地显示
    public void onRevokeMessage(IMMessage item) {
        if (item == null) {
            return;
        }

        IMMessage message = MessageBuilder.createTipMessage(item.getSessionId(), item.getSessionType());
        String nick = "";
        if (item.getSessionType() == SessionTypeEnum.Team) {
            nick = TeamDataCache.getInstance().getTeamMemberDisplayNameYou(item.getSessionId(), item.getFromAccount());
        } else if (item.getSessionType() == SessionTypeEnum.P2P) {
            nick = item.getFromAccount().equals(UserCache.getUserAccount()) ? "你" : "对方";
        }
        message.setContent(nick + "撤回了一条消息");
        message.setStatus(MsgStatusEnum.success);
        CustomMessageConfig config = new CustomMessageConfig();
        config.enableUnreadCount = false;
        message.setConfig(config);
        // 本地显示
        NIMClient.getService(MsgService.class).saveMessageToLocalEx(message, true, item.getTime());

        // 对方显示
//        IMMessage revokeTip = MessageBuilder.createTipMessage(item.getSessionId(), item.getSessionType());
//        if (item.getSessionType() == SessionTypeEnum.Team) {
//            nick = TeamDataCache.getInstance().getTeamMemberDisplayNameYou(item.getSessionId(), item.getFromAccount());
//        } else if (item.getSessionType() == SessionTypeEnum.P2P) {
//            nick = item.getFromAccount().equals(UserCache.getUserAccount()) ? "对方" : "你";
//        }
//        revokeTip.setContent(nick + "撤回了一条消息");
//        message.setConfig(config);
//        NIMClient.getService(MsgService.class).sendMessage(revokeTip,false);

    }

    // 对方撤回
    public void onRevokeOtherSideMessage(IMMessage item) {
        if (item == null) {
            return;
        }

        IMMessage message = MessageBuilder.createTipMessage(item.getSessionId(), item.getSessionType());
        String nick = "";
        if (item.getSessionType() == SessionTypeEnum.Team) {
            nick = TeamDataCache.getInstance().getTeamMemberDisplayNameYou(item.getSessionId(), item.getFromAccount());
        } else if (item.getSessionType() == SessionTypeEnum.P2P) {
            nick = item.getFromAccount().equals(UserCache.getUserAccount()) ? "你" : "对方";
        }
        message.setContent(nick + "撤回了一条消息");
        message.setStatus(MsgStatusEnum.success);
        CustomMessageConfig config = new CustomMessageConfig();
        config.enableUnreadCount = false;
        message.setConfig(config);
        NIMClient.getService(MsgService.class).saveMessageToLocalEx(message, true, item.getTime());
    }



    static class InstanceHolder {
        final static MessageHelper instance = new MessageHelper();
    }
}

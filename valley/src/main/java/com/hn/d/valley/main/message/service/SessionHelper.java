package com.hn.d.valley.main.message.service;

import com.angcyo.uiview.container.ILayout;
import com.hn.d.valley.main.message.groupchat.GroupChatUIView;
import com.hn.d.valley.main.message.p2pchat.P2PChatUIView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * Created by hewking on 2017/3/30.
 */
public class SessionHelper {

    public static void startP2PSession(ILayout mLayout, String sessionId, SessionTypeEnum sessionType) {
        startP2PSession(mLayout,sessionId,sessionType,null);
    }

    public static void startP2PSession(ILayout mLayout, String sessionId, SessionTypeEnum sessionType, IMMessage anchor) {
        P2PChatUIView.start(mLayout,sessionId,sessionType,anchor);
    }

    public static void startTeamSession(ILayout mLayout, String sessionId, SessionTypeEnum sessionType) {
        startTeamSession(mLayout,sessionId,sessionType,null);
    }

    public static void startTeamSession(ILayout mLayout, String sessionId, SessionTypeEnum sessionType, IMMessage anchor) {
        GroupChatUIView.start(mLayout,sessionId,sessionType,anchor);
    }

}

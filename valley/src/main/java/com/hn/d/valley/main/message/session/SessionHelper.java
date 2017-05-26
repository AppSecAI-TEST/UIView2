package com.hn.d.valley.main.message.session;

import android.os.Build;

import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.container.ILayout;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.main.me.UserDetailUIView2;
import com.hn.d.valley.main.message.chat.ChatUIView2;
import com.hn.d.valley.main.message.groupchat.GroupChatUIView;
import com.hn.d.valley.main.message.p2pchat.P2PChatUIView;
import com.hn.d.valley.main.other.KLJUIView;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by hewking on 2017/3/30.
 */
public class SessionHelper {

    private static SessionCustomization p2pCustomization;
    private static SessionCustomization customization;
    private static SessionCustomization teamCustomization;
    private static SessionCustomization kljCustomization;
    private static SessionCustomization walletCustomization;
    private static SessionCustomization hotSpotCustomization;

    public static SessionEventListener getSessionListener() {
        return sessionListener;
    }

    public static void setSessionListener() {
        SessionHelper.sessionListener = new SessionEventListener() {
            @Override
            public void onAvatarClicked(UIBaseView uiBaseView, IMMessage message) {
                if (message.getFromAccount().equals(Constant.klj)) {
                    uiBaseView.startIView(new KLJUIView());
                } else if (message.getFromAccount().equals(Constant.wallet)) {

                } else if (message.getFromAccount().equals(Constant.hot_news)) {

                } else {
                    uiBaseView.startIView(new UserDetailUIView2(message.getFromAccount()));
                }
            }

            @Override
            public void onAvatarLongClicked(UIBaseView uiBaseView, IMMessage message) {

            }
        };
    }

    // 会话窗口消息列表一些点击事件的响应处理函数
    private static SessionEventListener sessionListener;

    public static void startP2PSession(ILayout mLayout, String sessionId, SessionTypeEnum sessionType) {
        startP2PSession(mLayout,sessionId,sessionType,null);
    }

    public static void startP2PSession(ILayout mLayout, String sessionId, SessionTypeEnum sessionType, IMMessage anchor) {
        P2PChatUIView.start(mLayout,sessionId,sessionType,anchor,getP2pCustomization());
    }

    public static void startTeamSession(ILayout mLayout, String sessionId, SessionTypeEnum sessionType) {
        startTeamSession(mLayout,sessionId,sessionType,null);
    }

    public static void startTeamSession(ILayout mLayout, String sessionId, SessionTypeEnum sessionType, IMMessage anchor) {
        startTeamSession(mLayout,sessionId,sessionType,anchor,null);
    }

    public static void startTeamSession(ILayout mLayout, String sessionId, SessionTypeEnum sessionType, IMMessage anchor, Set<IMMessage> aitMessages) {
        GroupChatUIView.start(mLayout,sessionId,sessionType,anchor,aitMessages,getTeamCustomization());
    }

    public static void init() {
        setSessionListener();
    }

    public static void startSession(ILayout mlayout, String sessionId , SessionTypeEnum sessionType) {
        switch (sessionId) {
            case Constant.klj:
                ChatUIView2.start(mlayout,sessionId,SessionTypeEnum.P2P,getKljCustomization());
                break;
            case Constant.wallet:
                ChatUIView2.start(mlayout,sessionId,SessionTypeEnum.P2P,getKljCustomization());
                break;
            case Constant.hot_news:
                ChatUIView2.start(mlayout,sessionId,SessionTypeEnum.P2P,getHotSpotCustomization());
                break;
        }
    }

    private static SessionCustomization getP2pCustomization() {
        if (p2pCustomization == null) {
            p2pCustomization = new SessionCustomization(){
                @Override
                public List<CommandItemInfo> createItems() {
                    List<CommandItemInfo> items = new ArrayList<>();
                    items.add(new AVChatCommandItem(AVChatType.AUDIO));
                    items.add(new AVChatCommandItem(AVChatType.VIDEO));
                    items.add(new LocationCommandItem());
                    items.add(new PersonalCardCommandItem());
                    items.add(new RedPacketCommandItem());
                    return items;
                }
            };
        }

        return p2pCustomization;

    }
    private static SessionCustomization getTeamCustomization() {
        if (teamCustomization == null) {
            teamCustomization = new SessionCustomization() {
                @Override
                public List<CommandItemInfo> createItems() {
                    List<CommandItemInfo> items = new ArrayList<>();
                    items.add(new LocationCommandItem());
                    items.add(new PersonalCardCommandItem());
                    //因为 发红包涉及到 群聊人数 先注释
//                    items.add(new RedPacketCommandItem());
                    return items;
                }
            };
        }
        return teamCustomization;
    }

    private static SessionCustomization getKljCustomization() {
        if (kljCustomization == null) {
            kljCustomization = new SessionCustomization() {
                @Override
                public List<CommandItemInfo> createItems() {
                    List<CommandItemInfo> items = new ArrayList<>();
                    return items;
                }
            };
        }
        return kljCustomization;
    }


    private static SessionCustomization getHotSpotCustomization() {
        if (hotSpotCustomization == null) {
            hotSpotCustomization = new SessionCustomization() {
                @Override
                public List<CommandItemInfo> createItems() {
                    List<CommandItemInfo> items = new ArrayList<>();
                    return items;
                }
            };
        }
        hotSpotCustomization.setShowInputPanel(false);
        return hotSpotCustomization;
    }



}

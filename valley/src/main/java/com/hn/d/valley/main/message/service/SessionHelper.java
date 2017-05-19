package com.hn.d.valley.main.message.service;

import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.container.ILayout;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.main.me.UserDetailUIView2;
import com.hn.d.valley.main.message.session.CommandItemInfo;
import com.hn.d.valley.main.message.session.SessionCustomization;
import com.hn.d.valley.main.message.session.SessionEventListener;
import com.hn.d.valley.main.message.chat.ChatUIView2;
import com.hn.d.valley.main.message.groupchat.GroupChatUIView;
import com.hn.d.valley.main.message.p2pchat.P2PChatUIView;
import com.hn.d.valley.main.other.KLJUIView;
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
        P2PChatUIView.start(mLayout,sessionId,sessionType,anchor);
    }

    public static void startTeamSession(ILayout mLayout, String sessionId, SessionTypeEnum sessionType) {
        startTeamSession(mLayout,sessionId,sessionType,null);
    }

    public static void startTeamSession(ILayout mLayout, String sessionId, SessionTypeEnum sessionType, IMMessage anchor) {
        startTeamSession(mLayout,sessionId,sessionType,anchor,null);
    }

    public static void startTeamSession(ILayout mLayout, String sessionId, SessionTypeEnum sessionType, IMMessage anchor, Set<IMMessage> aitMessages) {
        GroupChatUIView.start(mLayout,sessionId,sessionType,anchor,aitMessages);
    }

    public static void init() {
        setSessionListener();
    }

    public static void startSession(ILayout mlayout, String sessionId , SessionTypeEnum sessionType) {
        ChatUIView2.start(mlayout,sessionId,SessionTypeEnum.P2P,getCustomization());
    }

    private static SessionCustomization getP2pCustomization() {
        if (p2pCustomization != null) {
            p2pCustomization = new SessionCustomization(){

                @Override
                public List<CommandItemInfo> createItems() {
                    return null;
                }
            };
        }

        return p2pCustomization;

    }

    private static SessionCustomization getCustomization() {
        if (customization == null) {
            customization = new SessionCustomization(){
                @Override
                public List<CommandItemInfo> createItems() {
                    return null;
                }
            };
        }
        customization.setShowInputPanel(false);
        return customization;
    }

    private static SessionCustomization getTeamCustomization() {
        if (teamCustomization == null) {
            teamCustomization = new SessionCustomization() {
                @Override
                public List<CommandItemInfo> createItems() {
                    List<CommandItemInfo> items = new ArrayList<>();
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

    private static SessionCustomization getWalletCustomization() {
        if (walletCustomization == null) {
            walletCustomization = new SessionCustomization() {
                @Override
                public List<CommandItemInfo> createItems() {
                    List<CommandItemInfo> items = new ArrayList<>();
                    return items;
                }
            };
        }
        return walletCustomization;
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

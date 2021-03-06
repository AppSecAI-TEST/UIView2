package com.hn.d.valley.main.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.github.swipe.recyclerview.SwipeMenuRecyclerView;
import com.angcyo.uiview.model.TitleBarPattern;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.event.UpdateDataEvent;
import com.hn.d.valley.cache.MsgCache;
import com.hn.d.valley.cache.RecentContactsCache;
import com.hn.d.valley.main.friend.FriendMenuUIDialog;
import com.hn.d.valley.main.friend.FriendNewUIView2;
import com.hn.d.valley.main.friend.FriendUIView;
import com.hn.d.valley.main.friend.ItemTypes;
import com.hn.d.valley.main.message.chat.ChatActivity;
import com.hn.d.valley.main.message.search.GlobalSearchUIView2;
import com.hn.d.valley.main.message.session.RecentContactsControl;
import com.hn.d.valley.main.message.session.SessionHelper;
import com.hn.d.valley.sub.user.NewNotifyUIView;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：主页 -> 消息界面
 * 创建人员：Robi
 * 创建时间：2016/12/16 18:44
 * 修改人员：Robi
 * 修改时间：2016/12/16 18:44
 * 修改备注：
 * Version: 1.0.0
 */
public class MessageUIView extends BaseUIView {

    SwipeMenuRecyclerView mSwipeRecyclerView;

    private boolean isLoading = false;

    private RecentContactsControl mRecentContactsControl;

    @Override
    protected String getBaseEmptyTip() {
        return mActivity.getResources().getString(R.string.chat_empty_tip);
    }

    @Override
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_main_message_layout);
    }

    @Override
    public void onViewCreate(View rootView) {
        super.onViewCreate(rootView);
        mRecentContactsControl = new RecentContactsControl(mActivity,
                new Action1<RecentContact>() {
                    @Override
                    public void call(RecentContact recentContact) {
                        switch (recentContact.getContactId()) {
                            case Constant.add_contact:
                                //打开新朋友界面
                                mParentILayout.startIView(new FriendNewUIView2());
                                return;
                            case Constant.comment:
                                //打开动态通知页面
                                mParentILayout.startIView(new NewNotifyUIView(recentContact.getContactId(), recentContact.getSessionType()));
                                return;
                            case Constant.klj:
                                SessionHelper.startSession(mParentILayout,Constant.klj,SessionTypeEnum.P2P);
                                return;
                            case Constant.wallet:
                            case Constant.hot_news:
                                SessionHelper.startSession(mParentILayout, recentContact.getContactId(), recentContact.getSessionType());
                                return;
                        }

                        if (recentContact.getSessionType() == SessionTypeEnum.Team) {
                            // 被@的信息 进入聊天页面清除 cachmessages
                            Map<String, Set<IMMessage>> cacheMessages = RecentContactsCache.instance().getCacheMessages();
                            Set<IMMessage> aitMessages = cacheMessages.get(recentContact.getContactId());
                            if (aitMessages != null && aitMessages.size() > 0) {
                                SessionHelper.startTeamSession(mParentILayout, recentContact.getContactId(), recentContact.getSessionType(), null, aitMessages);
                            } else {
                                SessionHelper.startTeamSession(mParentILayout, recentContact.getContactId(), recentContact.getSessionType());
                            }
                            cacheMessages.remove(recentContact.getContactId());
                            return;
                        }
                        //打开对话界面
//                        ChatActivity.start(mActivity,recentContact.getContactId());
                        SessionHelper.startP2PSession(mParentILayout, recentContact.getContactId(), recentContact.getSessionType());
                        //HnChatActivity.launcher(mActivity, recentContact.getFromAccount());
                    }
                },
                new Action0() {
                    @Override
                    public void call() {
                        startSearch();
                    }
                });
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
//        SessionSettingDelegate.getInstance().fetchTopList();

    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        mRecentContactsControl.unLoad();
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mSwipeRecyclerView = v(R.id.swipe_recycler_view);

        mRecentContactsControl.init(mBaseContentLayout);
        onEvent(null);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> leftItems = new ArrayList<>();
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();

        leftItems.add(TitleBarPattern.TitleBarItem.build().setRes(R.drawable.top_friends_3).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParentILayout.startIView(new FriendUIView());
            }
        }));
        rightItems.add(TitleBarPattern.TitleBarItem.build().setRes(R.drawable.tianjia_3)
                .setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                mParentILayout.startIView(new FriendUIView());

//                MenuPopUpWindow popUpWindow = new MenuPopUpWindow(mActivity,mParentILayout);
//                popUpWindow.showAsDropDown(v, Gravity.BOTTOM,0,0);

                        mParentILayout.startIView(new FriendMenuUIDialog(v, mParentILayout));


                    }
                }));

        return super.getTitleBar().setTitleString(mActivity.getString(R.string.nav_message_text)).setLeftItems(leftItems).setRightItems(rightItems);
    }

    /**
     * 打开搜索界面
     */
    private void startSearch() {
        GlobalSearchUIView2.start(mParentILayout, GlobalSearchUIView2.Options.sOptions, new int[]{ItemTypes.FRIEND, ItemTypes.GROUP, ItemTypes.MSG,});
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        // 进入最近联系人列表界面，建议放在onResume中
//        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_ALL, SessionTypeEnum.None);
        L.w("messageuiview onvidwshow");
        checkEmpty();
        MsgCache.notifyNoreadNum();
    }

    @Override
    public void onViewHide() {
        super.onViewHide();

        // 退出聊天界面或离开最近联系人列表界面，建议放在onPause中
//        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
    }

    private void checkEmpty() {
        if (RecentContactsCache.instance().getRecentContactList().isEmpty()) {
            showEmptyLayout();
        } else {
            showContentLayout();
        }
    }

    /**
     * 滚动置顶
     */
    public void scrollToTop() {
        if (mSwipeRecyclerView != null) {
            mSwipeRecyclerView.smoothScrollToPosition(0);
        }
    }

    @Subscribe(tags = {@Tag(Constant.TAG_UPDATE_RECENT_CONTACTS)})
    public void onEvent(UpdateDataEvent event) {
        L.d("messageuiview","onevent : " );
        mRecentContactsControl.setRefreshEnd();
        mRecentContactsControl.setRecentContact(RecentContactsCache.instance().getRecentContactList());
        MsgCache.notifyNoreadNum();
        checkEmpty();
    }
}

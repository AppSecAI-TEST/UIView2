package com.hn.d.valley.main.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIBaseRxView;
import com.angcyo.uiview.dialog.UIItemDialog;
import com.angcyo.uiview.github.swipe.recyclerview.SwipeMenuRecyclerView;
import com.angcyo.uiview.model.TitleBarPattern;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.event.UpdateDataEvent;
import com.hn.d.valley.cache.MsgCache;
import com.hn.d.valley.cache.RecentContactsCache;
import com.hn.d.valley.main.friend.AbsFriendItem;
import com.hn.d.valley.main.message.groupchat.ContactSelectUIVIew;
import com.hn.d.valley.main.message.groupchat.GroupChatUIView;
import com.hn.d.valley.main.message.groupchat.RequestCallback;
import com.hn.d.valley.main.message.groupchat.TeamCreateHelper;
import com.hn.d.valley.sub.user.NewFriend2UIView;
import com.hn.d.valley.sub.user.NewNotifyUIView;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Action3;

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

    @BindView(R.id.swipe_recycler_view)
    SwipeMenuRecyclerView mSwipeRecyclerView;
    private boolean isLoading = false;
    private RecentContactsControl mRecentContactsControl;

    @Override
    protected String getBaseEmptyTip() {
        return mActivity.getResources().getString(R.string.chat_empty_tip);
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_main_message_layout);
    }

    @Override
    public void onViewCreate(View rootView) {
        super.onViewCreate(rootView);
        mRecentContactsControl = new RecentContactsControl(mActivity,
                new Action1<RecentContact>() {
                    @Override
                    public void call(RecentContact recentContact) {
                        if (recentContact.getSessionType() == SessionTypeEnum.Team) {
                            GroupChatUIView.start(mOtherILayout, recentContact.getContactId(), recentContact.getSessionType());
                            return;
                        }
                        //打开对话界面
                        P2PChatUIView.start(mOtherILayout, recentContact.getContactId(), recentContact.getSessionType());
                        //HnChatActivity.launcher(mActivity, recentContact.getFromAccount());
                    }
                },
                new Action0() {
                    @Override
                    public void call() {
                        startSearch();
                    }
                });

        mRecentContactsControl.setItemAddContactsAction(new Action1<RecentContact>() {
            @Override
            public void call(RecentContact contact) {
                //打开新朋友界面
                mOtherILayout.startIView(new NewFriend2UIView());
            }
        });

        mRecentContactsControl.setItemCommentAction(new Action1<RecentContact>() {
            @Override
            public void call(RecentContact contact) {
                //打开动态通知页面
                mOtherILayout.startIView(new NewNotifyUIView(contact.getContactId(), contact.getSessionType()));
            }
        });

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
        mRecentContactsControl.init(mBaseContentLayout);
        onEvent(null);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> leftItems = new ArrayList<>();
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();

        leftItems.add(TitleBarPattern.TitleBarItem.build().setRes(R.drawable.top_add_friends).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //T_.show(mActivity.getString(R.string.searchUser));
                startSearch();
            }
        }));
        rightItems.add(TitleBarPattern.TitleBarItem.build()/*.setText(mActivity.getString(R.string.contacts))*/.setRes(R.drawable.switch_camera_n).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                T_.show(mActivity.getString(R.string.contacts));
                UIItemDialog.build()
                        .addItem(getString(R.string.add_friend), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mOtherILayout.startIView(new SearchUserUIView());
                            }
                        })
                        .addItem("添加群聊", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ContactSelectUIVIew targetView = new ContactSelectUIVIew();
                                targetView.setSelectAction(new Action3<UIBaseRxView, List<AbsFriendItem>, RequestCallback>() {
                                    @Override
                                    public void call(UIBaseRxView uiBaseDataView, List<AbsFriendItem> absFriendItems, RequestCallback requestCallback) {
                                        TeamCreateHelper.createAndSavePhoto(uiBaseDataView,absFriendItems,requestCallback);
                                    }
                                });
                                mOtherILayout.startIView(targetView);
                            }
                        }).showDialog(mOtherILayout);
            }
        }));

        return super.getTitleBar().setTitleString(mActivity.getString(R.string.nav_message_text)).setLeftItems(leftItems).setRightItems(rightItems);
    }

    /**
     * 打开搜索界面
     */
    private void startSearch() {
        mOtherILayout.startIView(new SearchUserUIView());
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        // 进入最近联系人列表界面，建议放在onResume中
        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_ALL, SessionTypeEnum.None);
        L.w("messageuiview onvidwshow");
        checkEmpty();
        MsgCache.notifyNoreadNum();
    }

    @Override
    public void onViewHide() {
        super.onViewHide();

        // 退出聊天界面或离开最近联系人列表界面，建议放在onPause中
        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
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
        mRecentContactsControl.setRefreshEnd();
        mRecentContactsControl.setRecentContact(RecentContactsCache.instance().getRecentContactList());
        MsgCache.notifyNoreadNum();
        checkEmpty();
    }
}

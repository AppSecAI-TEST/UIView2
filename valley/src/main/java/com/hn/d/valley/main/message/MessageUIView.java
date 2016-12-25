package com.hn.d.valley.main.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.model.TitleBarPattern;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.T_;
import com.hn.d.valley.nim.RNim;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.util.ArrayList;
import java.util.List;

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

    private boolean isLoading = false;
    private RecentContactsHelper mRecentContactsHelper;
    private Observer<List<RecentContact>> mRecentContactObserver;
    private Observer<RecentContact> mRecentContactDeleteObserver;

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_main_message_layout);
    }

    @Override
    public void onViewCreate() {
        super.onViewCreate();
        mRecentContactsHelper = new RecentContactsHelper(mActivity, new Action1<RecentContact>() {
            @Override
            public void call(RecentContact recentContact) {
                T_.show(recentContact.getFromNick());
            }
        });
        //会话列表改变监听
        mRecentContactObserver = new Observer<List<RecentContact>>() {
            @Override
            public void onEvent(List<RecentContact> recentContacts) {
                mRecentContactsHelper.setRecentContact(recentContacts);
            }
        };
        //会话列表被删除
        mRecentContactDeleteObserver = new Observer<RecentContact>() {
            @Override
            public void onEvent(RecentContact recentContact) {
                mRecentContactsHelper.removeRecentContact(recentContact);
            }
        };
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        RNim.msgServiceObserve().observeRecentContact(mRecentContactObserver, true);
        RNim.msgServiceObserve().observeRecentContactDeleted(mRecentContactDeleteObserver, true);
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        RNim.msgServiceObserve().observeRecentContact(mRecentContactObserver, false);
        RNim.msgServiceObserve().observeRecentContactDeleted(mRecentContactDeleteObserver, false);
    }

    @Override
    protected void initContentLayout() {
        super.initContentLayout();
        mRecentContactsHelper.init(mBaseContentLayout);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> leftItems = new ArrayList<>();
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();

        leftItems.add(TitleBarPattern.TitleBarItem.build().setRes(R.drawable.add_friends_n).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                T_.show(mActivity.getString(R.string.search));
            }
        }));
        rightItems.add(TitleBarPattern.TitleBarItem.build().setText(mActivity.getString(R.string.contacts)).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                T_.show(mActivity.getString(R.string.contacts));
            }
        }));

        return super.getTitleBar().setTitleString(mActivity.getString(R.string.nav_message_text)).setLeftItems(leftItems).setRightItems(rightItems);
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);

        if (!isLoading) {
            isLoading = true;
            RNim.queryRecentContacts(new RequestCallbackWrapper<List<RecentContact>>() {
                @Override
                public void onResult(int code, List<RecentContact> result, Throwable exception) {
                    L.i("code:" + code + " " + result.size());
                    isLoading = false;
                    if (result.size() == 0) {
                        showEmptyLayout();
                    } else {
                        mRecentContactsHelper.setRecentContact(result);
                        showContentLayout();
                    }
//                    List<NimUserInfo> allUserInfo = NIMClient.getService(UserService.class).getAllUserInfo();
//                    L.i("allUserInfo:" + allUserInfo.size());
                }
            });
        }
    }
}

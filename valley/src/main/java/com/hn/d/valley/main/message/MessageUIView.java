package com.hn.d.valley.main.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.uiview.model.TitleBarPattern;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.T_;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.event.UpdateDataEvent;
import com.hn.d.valley.cache.MsgCache;
import com.hn.d.valley.cache.RecentContactsCache;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.util.ArrayList;

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

    private boolean isLoading = false;
    private RecentContactsControl mRecentContactsControl;


    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_main_message_layout);
    }

    @Override
    public void onViewCreate() {
        super.onViewCreate();
        mRecentContactsControl = new RecentContactsControl(mActivity,
                new Action1<RecentContact>() {
                    @Override
                    public void call(RecentContact recentContact) {
                        T_.show(recentContact.getFromNick());
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
    public void onViewLoad() {
        super.onViewLoad();
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
    }

    @Override
    protected void initContentLayout() {
        super.initContentLayout();
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
                //T_.show(mActivity.getString(R.string.search));
                startSearch();
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

    /**
     * 打开搜索界面
     */
    private void startSearch() {
        mOtherILayout.startIView(new SearchUserUIView());
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        checkEmpty();
        MsgCache.notifyNoreadNum();
    }

    private void checkEmpty() {
        if (RecentContactsCache.instance().getRecentContactList().isEmpty()) {
            showEmptyLayout();
        } else {
            showContentLayout();
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

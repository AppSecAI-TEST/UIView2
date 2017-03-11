package com.hn.d.valley.main.message;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.hn.d.valley.R;
import com.hn.d.valley.activity.HnChatActivity;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.inner.RSupportFragment;
import com.hn.d.valley.bean.event.UpdateDataEvent;
import com.hn.d.valley.cache.MsgCache;
import com.hn.d.valley.cache.RecentContactsCache;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/29 8:56
 * 修改人员：Robi
 * 修改时间：2016/12/29 8:56
 * 修改备注：
 * Version: 1.0.0
 */
public class MessageFragment extends RSupportFragment {
    private RecentContactsControl mRecentContactsControl;

    public static MessageFragment newInstance() {
        Bundle args = new Bundle();
        MessageFragment fragment = new MessageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.view_main_message_layout;
    }

    @Override
    protected void onCreateView() {
        mRecentContactsControl = new RecentContactsControl(_mActivity,
                new Action1<RecentContact>() {
                    @Override
                    public void call(RecentContact recentContact) {
                        //ChatUIView.start(mOtherILayout, recentContact.getFromAccount());
                        HnChatActivity.launcher(_mActivity, recentContact.getFromAccount());
                    }
                },
                new Action0() {
                    @Override
                    public void call() {
                    }
                });
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        mRecentContactsControl.init(mViewHolder.itemView);
        onEvent(null);
    }

    @Subscribe(tags = {@Tag(Constant.TAG_UPDATE_RECENT_CONTACTS)})
    public void onEvent(UpdateDataEvent event) {
        mRecentContactsControl.setRefreshEnd();
        mRecentContactsControl.setRecentContact(RecentContactsCache.instance().getRecentContactList());
        MsgCache.notifyNoreadNum();
    }
}

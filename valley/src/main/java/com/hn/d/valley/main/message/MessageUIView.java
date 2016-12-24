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
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_main_message_layout);
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
                }
            });
        }
    }
}

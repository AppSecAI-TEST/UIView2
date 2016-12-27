package com.hn.d.valley.main.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.angcyo.uiview.base.UIContentView;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.hn.d.valley.R;
import com.hn.d.valley.cache.NimUserInfoCache;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：聊天界面
 * 创建人员：Robi
 * 创建时间：2016/12/27 17:46
 * 修改人员：Robi
 * 修改时间：2016/12/27 17:46
 * 修改备注：
 * Version: 1.0.0
 */
public class ChatUIView extends UIContentView {

    String account;

    public ChatUIView(String account) {
        this.account = account;
    }

    public static void start(ILayout mLayout, String account) {
        mLayout.startIView(new ChatUIView(account), new UIParam().setLaunchMode(UIParam.SINGLE_TOP));
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_chat_layout);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setTitleString(NimUserInfoCache.getInstance().getUserDisplayName(account))
                .setShowBackImageView(true);
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                showLoadView();
            }
        }, 2000);
    }
}

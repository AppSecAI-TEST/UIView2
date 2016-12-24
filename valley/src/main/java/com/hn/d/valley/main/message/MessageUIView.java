package com.hn.d.valley.main.message;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.uiview.model.TitleBarPattern;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.T_;

import java.util.ArrayList;

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
    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_main_message_layout);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> leftItems = new ArrayList<>();
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();

        leftItems.add(TitleBarPattern.TitleBarItem.build().setRes(R.drawable.top_search).setListener(new View.OnClickListener() {
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
}

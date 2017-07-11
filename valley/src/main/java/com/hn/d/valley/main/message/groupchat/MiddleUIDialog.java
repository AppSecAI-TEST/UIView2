package com.hn.d.valley.main.message.groupchat;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.uiview.base.UIIDialogImpl;
import com.hn.d.valley.R;

import rx.functions.Action0;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/25 14:22
 * 修改人员：hewking
 * 修改时间：2017/04/25 14:22
 * 修改备注：
 * Version: 1.0.0
 */
public class MiddleUIDialog extends UIIDialogImpl {

    TextView titleView;
    TextView contentView;
    View lineLayout;
    LinearLayout surelayout;

    private String title;
    private String content;

    private Action0 action;

    public void setAction(Action0 action) {
        this.action = action;
    }

    public MiddleUIDialog(String title , String content,Action0 action) {
        this.title = title;
        this.content = content;
        this.action = action;
    }

    @Override
    protected View inflateDialogView(FrameLayout dialogRootLayout, LayoutInflater inflater) {
        setGravity(Gravity.CENTER);
        return inflater.inflate(R.layout.middle_dialog_layout, dialogRootLayout);
    }

    @Override
    public void loadContentView(View rootView) {
        super.loadContentView(rootView);

        titleView = (TextView) rootView.findViewById(R.id.base_dialog_title_view);
        contentView = (TextView) rootView.findViewById(R.id.base_dialog_content_view);
        surelayout = (LinearLayout) rootView.findViewById(R.id.sure_layout);

        titleView.setText(title);
        contentView.setText(content);

        surelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDialog();
                if (action != null) {
                    action.call();
                }
            }
        });

    }
}

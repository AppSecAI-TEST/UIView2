package com.hn.d.valley.main.message.redpacket;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;

import butterknife.BindView;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/24 19:47
 * 修改人员：hewking
 * 修改时间：2017/04/24 19:47
 * 修改备注：
 * Version: 1.0.0
 */
public class ChoosePayWayUIDialog extends UIIDialogImpl {


    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.base_dialog_title_view)
    TextView baseDialogTitleView;
    @BindView(R.id.base_item_info_layout)
    ItemInfoLayout baseItemInfoLayout;
    @BindView(R.id.base_dialog_root_layout)
    LinearLayout baseDialogRootLayout;

    @Override
    protected View inflateDialogView(RelativeLayout dialogRootLayout, LayoutInflater inflater) {
        setGravity(Gravity.CENTER);
        return inflater.inflate(R.layout.pay_way_dialog_layout, dialogRootLayout);
    }

    @Override
    public void loadContentView(View rootView) {
        super.loadContentView(rootView);


    }
}

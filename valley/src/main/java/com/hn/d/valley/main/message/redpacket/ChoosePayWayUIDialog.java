package com.hn.d.valley.main.message.redpacket;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;

import butterknife.BindView;
import rx.functions.Action1;

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
    @BindView(R.id.base_dialog_root_layout)
    LinearLayout baseDialogRootLayout;
    @BindView(R.id.info_alipay)
    ItemInfoLayout infoAlipay;
    @BindView(R.id.info_wechat)
    ItemInfoLayout infoWechat;
    @BindView(R.id.info_balance)
    ItemInfoLayout infoBalance;

    private PayUIDialog.Params params;
    private Action1 action;

    public ChoosePayWayUIDialog(Action1 action,PayUIDialog.Params params) {
        this.params = params;
        this.action = action;
    }

    @Override
    protected View inflateDialogView(RelativeLayout dialogRootLayout, LayoutInflater inflater) {
        setGravity(Gravity.CENTER);
        return inflater.inflate(R.layout.pay_way_dialog_layout, dialogRootLayout);
    }

    @Override
    public void loadContentView(View rootView) {
        super.loadContentView(rootView);
        infoBalance.setItemText(mActivity.getString(R.string.text_balance));
        infoBalance.setLeftDrawableRes(R.drawable.icon_chai);
        if (params.getBalance() != -1) {
            infoBalance.setItemDarkText("￥" + params.getBalance() / 100f);
        }

        infoAlipay.setLeftDrawableRes(R.drawable.icon_alipay_wallet);
        infoAlipay.setItemText(mActivity.getString(R.string.text_alipay));

        infoWechat.setLeftDrawableRes(R.drawable.icon_wechat_wallet);
        infoWechat.setItemText(mActivity.getString(R.string.text_wechat));

        infoBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (params.getBalance() >= params.money) {
                    startIView(new PayUIDialog(action, params));
                    finishDialog();
                } else {
                    T_.show(mActivity.getString(R.string.text_balance_not_enough));
                }
            }
        });

        infoAlipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(new ThirdPayUIDialog(action,params,ThirdPayUIDialog.ALIPAY,1));
                finishDialog();
            }
        });

        infoWechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(new ThirdPayUIDialog(action,params,ThirdPayUIDialog.WECHAT,1));
                finishDialog();
            }
        });


    }
}

package com.hn.d.valley.main.message.redpacket;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;

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


    ImageView ivCancel;
    TextView baseDialogTitleView;
    LinearLayout baseDialogRootLayout;
    ItemInfoLayout infoAlipay;
//    ItemInfoLayout infoWechat;
    ItemInfoLayout infoBalance;

    private PayUIDialog.Params params;
    private Action1 action;

    public ChoosePayWayUIDialog(Action1 action,PayUIDialog.Params params) {
        this.params = params;
        this.action = action;
    }

    @Override
    protected View inflateDialogView(FrameLayout dialogRootLayout, LayoutInflater inflater) {
        setGravity(Gravity.CENTER);
        return inflater.inflate(R.layout.pay_way_dialog_layout, dialogRootLayout);
    }

    @Override
    public void loadContentView(View rootView) {
        super.loadContentView(rootView);

        ivCancel = v(R.id.iv_cancel);
        baseDialogTitleView = v(R.id.base_dialog_title_view);
        baseDialogRootLayout = v(R.id.base_dialog_root_layout);
        infoAlipay = v(R.id.info_alipay);
//        infoWechat = v(R.id.info_wechat);
        infoBalance = v(R.id.info_balance);

        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDialog();
            }
        });


        if (!params.enableBalance) {
            infoBalance.setVisibility(View.GONE);
        } else {
            infoBalance.setVisibility(View.VISIBLE);
            infoBalance.setItemText(mActivity.getString(R.string.text_balance));
            infoBalance.setLeftDrawableRes(R.drawable.icon_chai);
            if (params.getBalance() != -1) {
                infoBalance.setItemDarkText("￥" + params.getBalance() / 100f);
            }
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

        }



        infoAlipay.setLeftDrawableRes(R.drawable.icon_alipay_wallet);
        infoAlipay.setItemText(mActivity.getString(R.string.text_alipay));

//        infoWechat.setLeftDrawableRes(R.drawable.icon_wechat_wallet);
//        infoWechat.setItemText(mActivity.getString(R.string.text_wechat));

        infoAlipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(new ThirdPayUIDialog(action,params,ThirdPayUIDialog.ALIPAY,params.missionType));
                finishDialog();
            }
        });

//        infoWechat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startIView(new ThirdPayUIDialog(action,params,ThirdPayUIDialog.WECHAT,params.missionType));
//                finishDialog();
//            }
//        });


    }
}

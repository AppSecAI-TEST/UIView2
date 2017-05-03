package com.hn.d.valley.main.message.redpacket;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
 * 创建时间：2017/05/02 17:39
 * 修改人员：hewking
 * 修改时间：2017/05/02 17:39
 * 修改备注：
 * Version: 1.0.0
 */
public class ThirdPayUIDialog extends UIIDialogImpl {

    public static final String ALIPAY = "alipay";
    public static final String WECHAT = "wechat";

    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.base_dialog_title_view)
    TextView baseDialogTitleView;
    @BindView(R.id.base_dialog_content_view)
    TextView baseDialogContentView;
    @BindView(R.id.base_item_info_layout)
    ItemInfoLayout baseItemInfoLayout;
    @BindView(R.id.btn_send)
    Button btnSend;

    private PayUIDialog.Params params;

    private String type;

    public ThirdPayUIDialog(PayUIDialog.Params params, String thirdType) {
        this.params = params;
        this.type = thirdType;
    }

    @Override
    protected View inflateDialogView(RelativeLayout dialogRootLayout, LayoutInflater inflater) {
        setGravity(Gravity.CENTER);
        return inflater.inflate(R.layout.pay_third_dialog_layout, dialogRootLayout);
    }

    @Override
    public void loadContentView(View rootView) {
        super.loadContentView(rootView);

        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDialog();
            }
        });

        baseDialogTitleView.setText(R.string.text_cashier_desk);
        baseDialogContentView.setText("￥ " + params.money);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendRedPacket();
            }
        });

        btnSend.setText(R.string.text_immediately_pay);

        baseItemInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOtherILayout.startIView(new ChoosePayWayUIDialog(params));
                finishDialog();
            }
        });

        switch (type) {
            case ALIPAY:
                baseItemInfoLayout.setLeftDrawableRes(R.drawable.icon_alipay_wallet);
                baseItemInfoLayout.setItemText(mActivity.getString(R.string.text_alipay));
                break;
            case WECHAT:
                baseItemInfoLayout.setLeftDrawableRes(R.drawable.icon_wechat_wallet);
                baseItemInfoLayout.setItemText(mActivity.getString(R.string.text_wechat));
                break;
        }


    }


}

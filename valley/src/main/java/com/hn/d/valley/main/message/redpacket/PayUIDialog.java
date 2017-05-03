package com.hn.d.valley.main.message.redpacket;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.service.RedPacketService;
import com.hn.d.valley.main.wallet.WalletService;
import com.hn.d.valley.widget.PasscodeView;

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
public class PayUIDialog extends UIIDialogImpl {

    public static final String TAG = PayUIDialog.class.getSimpleName();

    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.base_dialog_title_view)
    TextView baseDialogTitleView;
    @BindView(R.id.base_dialog_content_view)
    TextView baseDialogContentView;
    @BindView(R.id.line_layout)
    View lineLayout;
    @BindView(R.id.base_item_info_layout)
    ItemInfoLayout baseItemInfoLayout;
//    @BindView(R.id.btn_send)
//    Button btn_send;
    @BindView(R.id.base_dialog_root_layout)
    LinearLayout baseDialogRootLayout;
    @BindView(R.id.passcode_view)
    PasscodeView passcodeView;

    private Params params;

    public PayUIDialog(Params params) {
        this.params = params;
    }

    @Override
    protected View inflateDialogView(RelativeLayout dialogRootLayout, LayoutInflater inflater) {
        setGravity(Gravity.CENTER);
        return inflater.inflate(R.layout.pay_dialog_layout, dialogRootLayout);
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        balanceCheck();
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

//        btn_send.setText(R.string.text_immediately_pay);

        baseDialogTitleView.setText(R.string.text_cashier_desk);
        baseDialogContentView.setText("￥ " + params.money);
        baseItemInfoLayout.setItemText(mActivity.getString(R.string.text_balance));
        baseItemInfoLayout.setLeftDrawableRes(R.drawable.icon_chai);
        baseItemInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOtherILayout.startIView(new ChoosePayWayUIDialog(params));
                finishDialog();
            }
        });

        passcodeView.setPasscodeEntryListener(new PasscodeView.PasscodeEntryListener() {
            @Override
            public void onPasscodeEntered(String passcode) {
                passwdConfirm(passcode);
            }
        });

//        btn_send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendRedPacket();
//            }
//        });
    }

    private void passwdConfirm(String passcode) {
        RRetrofit.create(WalletService.class)
                .passwordConfirm(Param.buildInfoMap("uid:" + UserCache.getUserAccount(),"password:" + passcode))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        T_.show("支付密码校验失败！");
                    }

                    @Override
                    public void onSucceed(String beans) {
                        sendRedPacket();
                    }
                });
    }

    private void balanceCheck() {
        RRetrofit.create(WalletService.class)
                .balanceCheck(Param.buildInfoMap("uid:" + UserCache.getUserAccount()))
                .compose(Rx.transformer(Integer.class))
                .subscribe(new BaseSingleSubscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onSucceed(Integer bean) {
                        super.onSucceed(bean);
                        baseItemInfoLayout.setItemDarkText("￥" + bean / 100f);
                    }
                });
    }

    private void sendRedPacket() {
        String type = checkType();

        RRetrofit.create(RedPacketService.class)
                .newbag(Param.buildInfoMap("uid:" + UserCache.getUserAccount(),"num:" + params.num,"money:" + params.money,"content:" + params.content,type))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        finishDialog();
                        T_.show("发送失败！");
                    }

                    @Override
                    public void onSucceed(String beans) {
//                        replaceIView(new NextSendRPUIView());
                        finishDialog();
                        L.i(TAG,beans);
                    }
                });

    }

    private String checkType() {
        if (params.to_uid != null) {
            return "to_uid:" + params.to_uid;
        } else if (params.to_gid != null) {
            return "to_gid:" + params.to_gid;
        }
        return "to_uid:" + params.to_uid;
    }

    public static class Params {

        int num;
        int money;
        int random;
        String to_gid;
        String content;
        String to_uid;

        public Params(int num, int money, String content, String to_uid,String to_gid) {
            this.num = num;
            this.money = money;
            this.content = content;
            this.to_uid = to_uid;
            this.to_gid = to_gid;
        }
    }

    public static enum RedPacketType{
        PERSON("uid"),GROUP("gid"),SQURE("squre");

        String type;

        RedPacketType(String type) {
            this.type = type;
        }
    }


}

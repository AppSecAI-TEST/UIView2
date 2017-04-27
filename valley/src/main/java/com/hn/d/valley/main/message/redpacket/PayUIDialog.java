package com.hn.d.valley.main.message.redpacket;

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
import com.hn.d.valley.bean.GroupAnnouncementBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.service.RedPacketService;
import com.hn.d.valley.service.GroupChatService;

import java.util.List;

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
    @BindView(R.id.btn_send)
    Button btn_send;
    @BindView(R.id.base_dialog_root_layout)
    LinearLayout baseDialogRootLayout;

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
    public void loadContentView(View rootView) {
        super.loadContentView(rootView);

        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDialog();
            }
        });

        btn_send.setText("立即支付");

        baseDialogTitleView.setText("收银台");
        baseDialogContentView.setText("￥ 200");
        baseItemInfoLayout.setItemText("余额");
        baseItemInfoLayout.setLeftDrawableRes(R.drawable.icon_chai);
        baseItemInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOtherILayout.startIView(new ChoosePayWayUIDialog());
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRedPacket();
            }
        });
    }

    private void sendRedPacket() {

        RRetrofit.create(RedPacketService.class)
                .newbag(Param.buildInfoMap("uid:" + UserCache.getUserAccount(),"num:" + params.num,"money:" + params.money,"content:" + params.content,"to_uid:" + params.to_uid))
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
                        replaceIView(new NextSendRPUIView());
                        finishDialog();
                        L.i(TAG,beans);
                    }
                });

    }

    public static class Params {

        int num;
        int money;
        String content;
        String to_uid;

        public Params(int num, int money, String content, String to_uid) {
            this.num = num;
            this.money = money;
            this.content = content;
            this.to_uid = to_uid;
        }
    }


}

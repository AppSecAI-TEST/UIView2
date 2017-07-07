package com.hn.d.valley.main.message.gift;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.GiftBean;
import com.hn.d.valley.bean.realm.LoginBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.me.setting.BindPhoneUIView;
import com.hn.d.valley.main.message.attachment.GiftReceiveAttachment;
import com.hn.d.valley.main.message.redpacket.ChoosePayWayUIDialog;
import com.hn.d.valley.main.message.redpacket.Constants;
import com.hn.d.valley.main.message.service.RedPacketService;
import com.hn.d.valley.main.wallet.WalletService;
import com.hn.d.valley.sub.other.KLGCoinUIVIew;
import com.hn.d.valley.widget.HnGlideImageView;
import com.hn.d.valley.widget.PasscodeView;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.json.JSONException;
import org.json.JSONObject;

import rx.functions.Action0;
import rx.functions.Action1;

import static com.hn.d.valley.main.message.chat.ChatUIView2.msgService;
import static com.hn.d.valley.main.message.redpacket.GrabPacketHelper.balanceCheck;
import static com.hn.d.valley.main.wallet.WalletHelper.getTransformer;

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
public class SendGiftUIDialog extends UIIDialogImpl {

    public static final String TAG = SendGiftUIDialog.class.getSimpleName();

    private GiftBean gift;
    private boolean giftEnable = true;

    public void setGiftEnable(boolean giftEnable) {
        this.giftEnable = giftEnable;
    }

    Action0 action;

    public SendGiftUIDialog(GiftBean gift,Action0 action) {
        this.gift = gift;
        this.action = action;
    }

    @Override
    protected View inflateDialogView(RelativeLayout dialogRootLayout, LayoutInflater inflater) {
        setGravity(Gravity.CENTER);
        return inflater.inflate(R.layout.send_gift_dialog_layout, dialogRootLayout);
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
    }

    @Override
    public void loadContentView(View rootView) {
        super.loadContentView(rootView);
        if (gift == null) {
            return;
        }
        HnGlideImageView iv_thumb = (HnGlideImageView) rootView.findViewById(R.id.iv_gift_thumb);
        TextView tv_gift_name = (TextView) rootView.findViewById(R.id.tv_gift_name);
        TextView tv_gift_status = (TextView) rootView.findViewById(R.id.tv_git_status);
        TextView tv_cancel = (TextView) rootView.findViewById(R.id.tv_cancel);
        TextView tv_ok = (TextView) rootView.findViewById(R.id.tv_ok);

        iv_thumb.setImageUrl(gift.getThumb());
        tv_gift_name.setText(gift.getName());
        if (giftEnable) {
            tv_gift_status.setText(gift.getCoins() .equals("0") ? "免费" : String.format("%s 龙币",gift.getCoins()));
        } else {
            tv_gift_status.setText("下架");
        }

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDialog();
            }
        });

        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 发送礼物
                if(Integer.valueOf(UserCache.instance().getLoginBean().getCoins())
                        < Integer.valueOf(gift.getCoins())){
                    //龙币不足
                    UIDialog.build()
                            .setDialogContent(mActivity.getString(R.string.text_klgcoin_not_enough))
                            .setOkText(mActivity.getString(R.string.text_recharge_now))
                            .setCancelText(mActivity.getString(R.string.cancel))
                            .setOkListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(!isBindPhone()){
                                        startIView(new KLGCoinUIVIew());
                                    }else{
                                        toBindPhone();
                                    }
                                }
                            }).showDialog(mParentILayout);
                    finishDialog();
                    return;
                }
                sendGift();
                finishDialog();
            }
        });
    }

    private boolean isBindPhone() {
        LoginBean loginBean = UserCache.instance().getLoginBean();
        return TextUtils.isEmpty(loginBean.getPhone());
    }

    private void toBindPhone() {
        UIDialog.build()
                .setDialogContent(mActivity.getString(R.string.text_please_bind_phone))
                .setOkText(mActivity.getString(R.string.text_bind_phone))
                .setCancelText(mActivity.getString(R.string.cancel))
                .setCancelListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setOkListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(new BindPhoneUIView());
                    }
                })
                .showDialog(mParentILayout);
    }

    private void sendGift() {
        action.call();
    }


}

package com.hn.d.valley.main.message.gift;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.utils.BmpUtil;
import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hn.d.valley.R;
import com.hn.d.valley.base.oss.OssHelper;
import com.hn.d.valley.bean.GiftBean;
import com.hn.d.valley.bean.realm.LoginBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.me.setting.BindPhoneUIView;
import com.hn.d.valley.sub.other.KLGCoinUIVIew;
import com.hn.d.valley.widget.HnGlideImageView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import rx.functions.Action0;

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
public class ReceiveGiftUIDialog extends UIIDialogImpl {

    public static final String TAG = ReceiveGiftUIDialog.class.getSimpleName();

    private GiftBean gift;
    private SessionTypeEnum mSessionType;

    public ReceiveGiftUIDialog(GiftBean gift, SessionTypeEnum sessionType) {
        this.gift = gift;
        this.mSessionType = sessionType;
    }

    @Override
    protected View inflateDialogView(FrameLayout dialogRootLayout, LayoutInflater inflater) {
        setGravity(Gravity.CENTER);
        return inflater.inflate(R.layout.receive_gift_dialog_layout, dialogRootLayout);
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
    }

    @Override
    public void loadContentView(View rootView) {
        super.loadContentView(rootView);
        final ImageView ivShine = mViewHolder.imgV(R.id.iv_gift_anim);
        final TextView ivTip = mViewHolder.tv(R.id.tv_tip_anim);
        final LinearLayout ll_shine = mViewHolder.v(R.id.ll_shine);

        BitmapTypeRequest<String> builder = Glide.with(mActivity)
                .load(gift.getThumb())
                .asBitmap();

        ivTip.setText(String.format("送你%s", gift.getName()));
//        builder.placeholder(R.drawable.defauit_avatar_contact);
//        builder.error(R.drawable.defauit_avatar_contact);

        final Animation.AnimationListener animListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finishDialog();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };

        if (mSessionType == SessionTypeEnum.P2P) {
            ivShine.setVisibility(View.GONE);
            Animation animShine = AnimationUtils.loadAnimation(mActivity, R.anim.up_scale_dismiss);
            animShine.setAnimationListener(animListener);
            ivTip.startAnimation(animShine);
            return;
        }

        builder.into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                ivShine.setImageBitmap(resource);
                Animation animShine = AnimationUtils.loadAnimation(mActivity, R.anim.shine);

                animShine.setAnimationListener(animListener);
                ivShine.startAnimation(animShine);
//                ivTip.startAnimation(animShine);
//                ll_shine.startAnimation(animShine);
            }
        });

    }

}

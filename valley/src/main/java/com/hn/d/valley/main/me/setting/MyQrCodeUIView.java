package com.hn.d.valley.main.me.setting;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.rsa.Base64Utils;
import com.angcyo.uiview.net.rsa.RSAUtils;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.utils.BmpUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.widget.HnGenderView;
import com.hn.d.valley.widget.HnGlideImageView;

import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：我的二维码
 * 创建人员：Robi
 * 创建时间：2017/02/22 16:14
 * 修改人员：Robi
 * 修改时间：2017/02/22 16:14
 * 修改备注：
 * Version: 1.0.0
 */
public class MyQrCodeUIView extends BaseContentUIView {
    public static String QR_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCeLIMWjuXWrHn49cd3H1ZaDobjImmMsshQbT5gvF6hUxiCi4ubRsDVlqTuL8XUZvSRNYe9TfgsY2ITJWF27FezEBBVt8zMsCN4njBi9QN7V3/zJdtfNyuKY6qEK/0iYsOWZfxA3rplviQaM98fYZtBVBoef6bYqsQ9WjfjSiI5aQIDAQAB";
    public static String QR_PRIVATE_KEY = "MIICXAIBAAKBgQCeLIMWjuXWrHn49cd3H1ZaDobjImmMsshQbT5gvF6hUxiCi4ubRsDVlqTuL8XUZvSRNYe9TfgsY2ITJWF27FezEBBVt8zMsCN4njBi9QN7V3/zJdtfNyuKY6qEK/0iYsOWZfxA3rplviQaM98fYZtBVBoef6bYqsQ9WjfjSiI5aQIDAQABAoGAXrfPBBosPkJohBJCEO5+Gk2qrqczx6Jj2+2fNfR3QmntOndv8VsMLJsaRtvqvoesmqwQjeb73zDgURDIbZuX49yI/ePTMZiIF7NTq6wYonDi1zK4uPGOKN7yWNKyizft0L7VDiiqHOdG6EbosaLzeJBgfkM0W6TT0mmHx3OaBMUCQQDMgkQLbUfSTPsN4FPmhfo5XYBz9KuXExYbDhru2HKuQHCq3c5lRIzuGCoymVNK0abQ8qnlYumIS1HACQWUKqw3AkEAxf+y9x1R0YExTkApdrb4NqjgQpqp7iGIGaZLjUOypNtIj2il04lQkx80BdUep43z5/z6r/hkDCJWW4BJrde3XwJBAI0ozTbl81EheZiWYtMXXyQBegyPsXDR58w87DI4jM/iAuKtvyz/KBef7mCGnItkMrS/Cq4em/tLod3fXE5tNfkCQAQ9HBy0MPs2M9MEBp82/YtWBC8I1ph1eU9rQvTMPTfQRfZj/CDSMLplkZyKWnSl0lHmFYvM2n90ALtGvM0O8CsCQACg/gVssbETBwAd4KKQfPoy3zR4w85uOT+TmZU4sGrA3Jg6vDE1dr12zFKqgHCsbFmSOxuAEx2QykSJnwdc4RY=";

    public static Observable<Bitmap> createQrCode(final String date, final int size, final int color, final Bitmap logo) {
        return Observable
                .create(new Observable.OnSubscribe<Bitmap>() {
                    @Override
                    public void call(Subscriber<? super Bitmap> subscriber) {
                        try {
                            subscriber.onStart();
                            subscriber.onNext(QRCodeEncoder.syncEncodeQRCode(date, size, color, logo));
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            e.printStackTrace();
                            subscriber.onError(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 加密
     */
    public static String encode(String data) {
        String encode = "";
        byte[] encodedData;
        try {
            encodedData = RSAUtils.encryptByPublicKey(data.getBytes(), QR_PUBLIC_KEY);
            encode = Base64Utils.encode(encodedData);
        } catch (Exception e) {
        }
        return encode.replaceAll("\\n", "");
    }

    /**
     * 解密
     */
    public static String decode(String encode) {
        String target = "";
        byte[] decodedData;
        try {
            decodedData = RSAUtils.decryptByPrivateKey(Base64Utils.decode(encode), QR_PRIVATE_KEY);
            target = new String(decodedData);
        } catch (Exception e) {
        }
        return target;
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_my_qr_code);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setShowBackImageView(true);
    }

    @Override
    public int getDefaultBackgroundColor() {
        return mActivity.getResources().getColor(R.color.chat_bg_color);
    }

    @Override
    protected int getTitleResource() {
        return R.string.my_qr_code;
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        UserInfoBean userInfoBean = UserCache.instance().getUserInfoBean();
        final HnGlideImageView imageView = mViewHolder.v(R.id.image_view);
        //imageView.setImageUrl(UserCache.getUserAvatar());

        HnGenderView genderView = mViewHolder.v(R.id.grade);
        genderView.setGender(userInfoBean.getSex(), userInfoBean.getGrade());

        View authView = mViewHolder.v(R.id.auth);
        authView.setVisibility("1".equalsIgnoreCase(userInfoBean.getIs_auth()) ? View.VISIBLE : View.INVISIBLE);

        TextView userName = mViewHolder.v(R.id.username);
        userName.setText(userInfoBean.getUsername());

        final ImageView qrView = mViewHolder.v(R.id.qr_code_view);
        Glide.with(mActivity)
                .load(UserCache.getUserAvatar())
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Bitmap cornerBitmap = BmpUtil.getRoundedCornerBitmap(resource, 1000);
                        imageView.setImageBitmap(cornerBitmap);

                        createQrCode(encode("uid=" + UserCache.getUserAccount()),
                                (int) ResUtil.dpToPx(mActivity, 300),
                                mActivity.getResources().getColor(R.color.theme_color_primary),
                                cornerBitmap)
                                .subscribe(new Action1<Bitmap>() {
                                    @Override
                                    public void call(Bitmap bitmap) {
                                        qrView.setImageBitmap(bitmap);
                                    }
                                });
                    }
                });
    }
}

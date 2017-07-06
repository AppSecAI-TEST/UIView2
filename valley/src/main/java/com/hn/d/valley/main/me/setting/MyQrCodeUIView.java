package com.hn.d.valley.main.me.setting;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.rcode.QRCodeEncoder;
import com.angcyo.uiview.Root;
import com.angcyo.uiview.dialog.UIBottomItemDialog;
import com.angcyo.uiview.github.utilcode.utils.FileUtils;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.rsa.Base64Utils;
import com.angcyo.uiview.net.rsa.RSAUtils;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.BmpUtil;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.file.AttachmentStore;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hn.d.valley.BuildConfig;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.bean.realm.QrCodeBean;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.widget.HnGenderView;
import com.hn.d.valley.widget.HnGlideImageView;
import com.lzy.imagepicker.ImagePicker;
import com.orhanobut.hawk.Hawk;

import java.io.File;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
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
    public static final String KEY_NEED_CREATE_QR = "key_need_create_qr";
    public static String QR_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCeLIMWjuXWrHn49cd3H1ZaDobjImmMsshQbT5gvF6hUxiCi4ubRsDVlqTuL8XUZvSRNYe9TfgsY2ITJWF27FezEBBVt8zMsCN4njBi9QN7V3/zJdtfNyuKY6qEK/0iYsOWZfxA3rplviQaM98fYZtBVBoef6bYqsQ9WjfjSiI5aQIDAQAB";
    public static String QR_PRIVATE_KEY = "MIICXAIBAAKBgQCeLIMWjuXWrHn49cd3H1ZaDobjImmMsshQbT5gvF6hUxiCi4ubRsDVlqTuL8XUZvSRNYe9TfgsY2ITJWF27FezEBBVt8zMsCN4njBi9QN7V3/zJdtfNyuKY6qEK/0iYsOWZfxA3rplviQaM98fYZtBVBoef6bYqsQ9WjfjSiI5aQIDAQABAoGAXrfPBBosPkJohBJCEO5+Gk2qrqczx6Jj2+2fNfR3QmntOndv8VsMLJsaRtvqvoesmqwQjeb73zDgURDIbZuX49yI/ePTMZiIF7NTq6wYonDi1zK4uPGOKN7yWNKyizft0L7VDiiqHOdG6EbosaLzeJBgfkM0W6TT0mmHx3OaBMUCQQDMgkQLbUfSTPsN4FPmhfo5XYBz9KuXExYbDhru2HKuQHCq3c5lRIzuGCoymVNK0abQ8qnlYumIS1HACQWUKqw3AkEAxf+y9x1R0YExTkApdrb4NqjgQpqp7iGIGaZLjUOypNtIj2il04lQkx80BdUep43z5/z6r/hkDCJWW4BJrde3XwJBAI0ozTbl81EheZiWYtMXXyQBegyPsXDR58w87DI4jM/iAuKtvyz/KBef7mCGnItkMrS/Cq4em/tLod3fXE5tNfkCQAQ9HBy0MPs2M9MEBp82/YtWBC8I1ph1eU9rQvTMPTfQRfZj/CDSMLplkZyKWnSl0lHmFYvM2n90ALtGvM0O8CsCQACg/gVssbETBwAd4KKQfPoy3zR4w85uOT+TmZU4sGrA3Jg6vDE1dr12zFKqgHCsbFmSOxuAEx2QykSJnwdc4RY=";

    /**
     * 本地QR文件路径
     */
    protected String mQrFilePath;

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
        return R.string.my_code;
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        UserInfoBean userInfoBean = UserCache.instance().getUserInfoBean();
        final HnGlideImageView imageView = mViewHolder.v(R.id.image_view);
        //imageView.setImageUrl(UserCache.getUserAvatar());
        imageView.setAuth(userInfoBean.getIs_auth());

        HnGenderView genderView = mViewHolder.v(R.id.grade);
        genderView.setGender(userInfoBean.getSex(), userInfoBean.getGrade());

//        View authView = mViewHolder.v(R.id.auth);
//        authView.setVisibility("1".equalsIgnoreCase(userInfoBean.getIs_auth()) ? View.VISIBLE : View.INVISIBLE);

        TextView userName = mViewHolder.v(R.id.username);
        userName.setText(userInfoBean.getUsername());


        mViewHolder.tv(R.id.create_qr_tip).setTextColor(SkinHelper.getSkin().getThemeSubColor());
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        final HnGlideImageView imageView = mViewHolder.v(R.id.image_view);
        final ImageView qrView = mViewHolder.v(R.id.qr_code_view);

        if (needCreateQrCode()) {
            createQrCodeView(imageView, qrView);
        } else {
            RRealm.where(new Action1<Realm>() {
                @Override
                public void call(Realm realm) {
                    final RealmResults<QrCodeBean> realmResults = realm.where(QrCodeBean.class)
                            .equalTo("uid", UserCache.getUserAccount()).findAll();
                    if (realmResults.isEmpty()) {
                        createQrCodeView(imageView, qrView);
                    } else {
                        String path = realmResults.last().getPath();
                        String avatar = realmResults.last().getAvatar();
                        if (!new File(path).exists() || !new File(avatar).exists()) {
                            createQrCodeView(imageView, qrView);
                        } else {
                            mQrFilePath = path;
                            setQrCodeView(imageView, qrView, path, avatar);
                        }
                    }
                }
            });
        }

        qrView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!TextUtils.isEmpty(mQrFilePath)) {
                    final File file = new File(mQrFilePath);
                    if (file.exists()) {
                        UIBottomItemDialog.build()
                                .addItem(getString(R.string.save_to_phone), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        File toFile = new File(Root.getAppExternalFolder("images"), Root.createFileName(".jpeg"));
                                        if (FileUtils.copyFile(file, toFile)) {
                                            ImagePicker.galleryAddPic(mActivity, toFile);
                                            T_.ok(getString(R.string.save_to_phone_format, toFile.getAbsolutePath()));
                                        } else {
                                            T_.error(getString(R.string.save_error));
                                        }
                                    }
                                })
                                .showDialog(MyQrCodeUIView.this);
                    }
                }
                return true;
            }
        });
    }

    protected boolean needCreateQrCode() {
        if (BuildConfig.SHOW_DEBUG) {
            return true;
        }
        return Hawk.get(KEY_NEED_CREATE_QR, true);
    }


    protected void setQrCodeView(final HnGlideImageView imageView, final ImageView qrView, String path, String avatar) {
        Glide.with(mActivity)
                .load(new File(path))
                .into(qrView);

        Glide.with(mActivity)
                .load(new File(avatar))
                .into(imageView);
    }


    /**
     * 返回需要创建二维码的内容
     */
    protected String getQrCodeContent() {
        return "个人名片:uid=" + UserCache.getUserAccount();
    }

    /**
     * 返回需要创建的图片地址
     */
    protected String getQRImgUrl() {
        return UserCache.getUserAvatar();
    }

    /**
     * 二维码创建成功之后的回调, 可以用来本地缓存二维码
     *
     * @param bitmap 二维码图片
     * @param avatar 头像本地地址
     */
    protected void onQrCodeCreateEnd(Bitmap bitmap, String avatar) {
        mQrFilePath = mActivity.getCacheDir().getAbsolutePath() + File.separator + UUID.randomUUID().toString();
        AttachmentStore.saveBitmap(bitmap, mQrFilePath, false);

        RRealm.save(new QrCodeBean(UserCache.getUserAccount(), mQrFilePath, avatar));

        Hawk.put(MyQrCodeUIView.KEY_NEED_CREATE_QR, false);
    }

    protected void createQrCodeView(final HnGlideImageView imageView, final ImageView qrView) {
        Glide.with(mActivity)
                .load(getQRImgUrl())
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Bitmap cornerBitmap = BmpUtil.getRoundedCornerBitmap(resource, 1000);
                        imageView.setImageBitmap(cornerBitmap);
                        final String avatar = mActivity.getCacheDir().getAbsolutePath() + File.separator + UUID.randomUUID().toString();
                        AttachmentStore.saveBitmap(cornerBitmap, avatar, false);

                        createQrCode(getQrCodeContent(),
                                (int) ResUtil.dpToPx(mActivity, 200),
                                Color.BLACK /*SkinHelper.getSkin().getThemeSubColor()*/,
                                cornerBitmap)
                                .subscribe(new Action1<Bitmap>() {
                                    @Override
                                    public void call(Bitmap bitmap) {
                                        qrView.setImageBitmap(bitmap);
                                        onQrCodeCreateEnd(bitmap, avatar);
                                    }
                                });
                    }
                });
    }
}

package com.hn.d.valley.main.message.groupchat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.uiview.Root;
import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.dialog.UIBottomItemDialog;
import com.angcyo.uiview.github.utilcode.utils.FileUtils;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.GroupDescBean;
import com.hn.d.valley.bean.realm.QrCodeBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.me.setting.MyQrCodeUIView;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.service.GroupChatService;
import com.hn.d.valley.widget.HnGlideImageView;
import com.lzy.imagepicker.ImagePicker;
import com.orhanobut.hawk.Hawk;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.functions.Action1;

import static com.hn.d.valley.main.message.groupchat.GroupInfoUIVIew.KEY_SESSION_ID;

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
public class GroupQrCodeUIView extends MyQrCodeUIView {
    public static final String KEY_NEED_CREATE_GROUP_QR = "key_need_create_group_qr";
    public static final String GROUP_DESC = "group_desc";

    private String mSessionId;

    private GroupDescBean mGroupDescBean;

    HnGlideImageView imageView;
    ImageView qrView;
    TextView userName;

    public GroupQrCodeUIView(){

    }

    public static void start(ILayout mLayout, String sessionId, GroupDescBean descBean) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SESSION_ID, sessionId);
        bundle.putSerializable(GROUP_DESC, descBean);
        mLayout.startIView(new GroupQrCodeUIView(), new UIParam().setBundle(bundle).setLaunchMode(UIParam.SINGLE_TOP));
    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);
        Bundle bundle = param.mBundle;
        if (bundle != null) {
            mSessionId = bundle.getString(KEY_SESSION_ID);
            mGroupDescBean = (GroupDescBean) bundle.getSerializable(GROUP_DESC);
        }

    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
//        super.onViewShowFirst(bundle);
        final HnGlideImageView imageView = mViewHolder.v(R.id.image_view);
        final ImageView qrView = mViewHolder.v(R.id.qr_code_view);

        if (needCreateQrCode()) {
            createQrCodeView(imageView, qrView);
        } else {
            RRealm.where(new Action1<Realm>() {
                @Override
                public void call(Realm realm) {
                    final RealmResults<QrCodeBean> realmResults = realm.where(QrCodeBean.class)
                            .equalTo("uid", mGroupDescBean.getYxGid()).findAll();
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
                                .showDialog(GroupQrCodeUIView.this);
                    }
                }
                return true;
            }
        });
    }

    @Override
    protected boolean needCreateQrCode() {
        return Hawk.get(KEY_NEED_CREATE_GROUP_QR, true);
    }

    private void loadGroupInfo() {
        add(RRetrofit.create(GroupChatService.class)
                .groupInfo(Param.buildMap("uid:" + UserCache.getUserAccount(), "yx_gid:" + mSessionId))
                .compose(Rx.transformer(GroupDescBean.class))
                .subscribe(new BaseSingleSubscriber<GroupDescBean>() {

                    @Override
                    public void onSucceed(final GroupDescBean bean) {
                        mGroupDescBean = bean;

                        RRealm.where(new Action1<Realm>() {
                            @Override
                            public void call(Realm realm) {
                                final RealmResults<QrCodeBean> realmResults = realm.where(QrCodeBean.class)
                                        .equalTo("uid",bean.getGid()).findAll();
                                if (realmResults.isEmpty()) {
                                    createQrCodeView(imageView, qrView);
                                } else {
                                    String path = realmResults.last().getPath();
                                    String avatar = realmResults.last().getAvatar();
                                    if (!new File(path).exists() || !new File(avatar).exists()) {
                                        createQrCodeView(imageView, qrView);
                                    } else {
                                        setQrCodeView(imageView, qrView, path, avatar);
                                    }
                                }
                            }
                        });

                    }
                }));
    }



    @Override
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
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
        return R.string.text_group_qrcode;
    }

    @Override
    protected void initOnShowContentLayout() {
//        super.initOnShowContentLayout();
        imageView = mViewHolder.v(R.id.image_view);
        //imageView.setImageUrl(UserCache.getUserAvatar());
        LinearLayout layout = mViewHolder.v(R.id.layout_status);
        layout.setVisibility(View.GONE);

        mViewHolder.tv(R.id.create_qr_tip).setTextColor(SkinHelper.getSkin().getThemeSubColor());

        qrView = mViewHolder.v(R.id.qr_code_view);
        userName = mViewHolder.v(R.id.username);
        mViewHolder.tv(R.id.tv_qrcode_tip).setText(R.string.text_group_qrcode_tip);

        userName.setText(mGroupDescBean.getTrueName());

//        loadGroupInfo();
    }

    @Override
    protected String getQrCodeContent() {
        String code = String.format("群名片:team=%s,%s,%s",mGroupDescBean.getGid(),UserCache.getUserAccount(),mGroupDescBean.getYxGid());
        return code;
    }

    @Override
    protected String getQRImgUrl() {
        return mGroupDescBean.getDefaultAvatar();
    }

//    private void createQrCodeView(final HnGlideImageView imageView, final ImageView qrView) {
//        Glide.with(mActivity)
//                .load(mGroupDescBean.getDefaultAvatar())
//                .asBitmap()
//                .into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                        Bitmap cornerBitmap = BmpUtil.getRoundedCornerBitmap(resource, 1000);
//                        imageView.setImageBitmap(cornerBitmap);
//                        final String avatar = mActivity.getCacheDir().getAbsolutePath() + File.separator + UUID.randomUUID().toString();
//                        AttachmentStore.saveBitmap(cornerBitmap, avatar, false);
//
//                        String code = String.format("群名片:team=%s,%s,%s",UserCache.getUserAccount(),mGroupDescBean.getGid(),mGroupDescBean.getYxGid());
//
//                        createQrCode(encode(code),
//                                (int) ResUtil.dpToPx(mActivity, 300),
//                                SkinHelper.getSkin().getThemeSubColor(),
//                                cornerBitmap)
//                                .subscribe(new Action1<Bitmap>() {
//                                    @Override
//                                    public void call(Bitmap bitmap) {
//                                        qrView.setImageBitmap(bitmap);
//                                        String qrFilePath = mActivity.getCacheDir().getAbsolutePath() + File.separator + UUID.randomUUID().toString();
//                                        AttachmentStore.saveBitmap(bitmap, qrFilePath, false);
//
//                                        RRealm.save(new QrCodeBean(mGroupDescBean.getYxGid(), qrFilePath, avatar));
//                                        Hawk.put(KEY_NEED_CREATE_GROUP_QR, false);
//
//                                    }
//                                });
//                    }
//                });
//    }
}

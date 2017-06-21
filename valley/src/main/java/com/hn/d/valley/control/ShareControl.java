package com.hn.d.valley.control;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.net.rsa.RSA;
import com.angcyo.uiview.net.rsa.Spm;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.utils.BmpUtil;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.file.AttachmentStore;
import com.angcyo.umeng.UM;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.service.SocialService;
import com.hn.d.valley.widget.HnGenderView;
import com.hn.d.valley.widget.HnGlideImageView;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;
import java.util.UUID;

import rx.functions.Action1;

import static com.hn.d.valley.main.me.setting.MyQrCodeUIView.createQrCode;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：分享控制
 * 创建人员：Robi
 * 创建时间：2017/05/23 18:12
 * 修改人员：Robi
 * 修改时间：2017/05/23 18:12
 * 修改备注：
 * Version: 1.0.0
 */
public class ShareControl {

    public static final String WAP_URL = "http://wap.klgwl.com";

    private ShareControl() {

    }

    public static ShareControl instance() {
        return Holder.instance;
    }

    /**
     * 分享名片
     * <p>
     * param shareType 分享类型【discuss-动态 news-资讯 user-名片 group-群 invite-邀请】
     * param item_id   item_id	否	int	数据id【动态id/资讯id/用户id/群id】
     * param site      分享的平台【微信好友，QQ,微博，朋友圈，QQ空间，手机短信】
     */
    public static void shareCardControl(final Activity activity,
                                        RBaseViewHolder holder,
                                        final String userIco,
                                        final String userId,
                                        final String shareTitle,
                                        final String shareDes) {
        holder.v(R.id.share_wx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUserCard(activity, SHARE_MEDIA.WEIXIN, userIco, userId, shareTitle, shareDes);
            }
        });
        holder.v(R.id.share_wxc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUserCard(activity, SHARE_MEDIA.WEIXIN_CIRCLE, userIco, userId, shareTitle, shareDes);
            }
        });
        holder.v(R.id.share_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUserCard(activity, SHARE_MEDIA.QQ, userIco, userId, shareTitle, shareDes);
            }
        });
        holder.v(R.id.share_qqz).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUserCard(activity, SHARE_MEDIA.QZONE, userIco, userId, shareTitle, shareDes);
            }
        });
    }

    /**
     * 分享动态
     */
    public static void shareDynamicControl(final Activity activity,
                                           RBaseViewHolder holder,
                                           final String userIco,
                                           final String itemId,
                                           final String shareTitle,
                                           final String shareDes) {
        shareDynamicControl(activity, holder, userIco, itemId, shareTitle, shareDes, null);
    }

    public static void shareDynamicControl(final Activity activity,
                                           RBaseViewHolder holder,
                                           final String userIco,
                                           final String itemId,
                                           final String shareTitle,
                                           final String shareDes,
                                           final UIIDialogImpl dialog) {
        holder.v(R.id.share_wx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDynamic(activity, SHARE_MEDIA.WEIXIN, userIco, itemId, shareTitle, shareDes);
                if (dialog != null) {
                    dialog.finishIView();
                }
            }
        });


        holder.v(R.id.share_wxc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDynamic(activity, SHARE_MEDIA.WEIXIN_CIRCLE, userIco, itemId, shareTitle, shareDes);
                if (dialog != null) {

                    dialog.finishIView();
                }

            }
        });
        holder.v(R.id.share_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDynamic(activity, SHARE_MEDIA.QQ, userIco, itemId, shareTitle, shareDes);
                if (dialog != null) {
                    dialog.finishIView();
                }
            }
        });
        holder.v(R.id.share_qqz).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDynamic(activity, SHARE_MEDIA.QZONE, userIco, itemId, shareTitle, shareDes);
                if (dialog != null) {
                    dialog.finishIView();
                }
            }
        });

    }

    /**
     * 热点资讯风向
     */
    public static void shareHotInfoControl(final Activity activity,
                                           RBaseViewHolder holder,
                                           final String userIco,
                                           final String url,
                                           final String shareTitle,
                                           final String shareDes,
                                           final UIIDialogImpl dialog) {
        holder.v(R.id.share_wx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareHotInfo(activity, SHARE_MEDIA.WEIXIN, userIco, url, shareTitle, shareDes);
                if (dialog != null) {
                    dialog.finishIView();
                }
            }
        });


        holder.v(R.id.share_wxc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareHotInfo(activity, SHARE_MEDIA.WEIXIN_CIRCLE, userIco, url, shareTitle, shareDes);
                if (dialog != null) {

                    dialog.finishIView();
                }

            }
        });
        holder.v(R.id.share_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareHotInfo(activity, SHARE_MEDIA.QQ, userIco, url, shareTitle, shareDes);
                if (dialog != null) {
                    dialog.finishIView();
                }
            }
        });
        holder.v(R.id.share_qqz).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareHotInfo(activity, SHARE_MEDIA.QZONE, userIco, url, shareTitle, shareDes);
                if (dialog != null) {
                    dialog.finishIView();
                }
            }
        });

    }

    private static void shareKlgDynamic(final ILayout layout, RBaseViewHolder holder) {


    }

    /**
     * 分享名片
     */
    public static void shareUserCard(final Activity activity,
                                     final SHARE_MEDIA shareMedia,
                                     final String userIco,
                                     final String userId,
                                     final String shareTitle,
                                     final String shareDes) {
        final String userCardSpm = createUserCardSpm(userId);
        final String cardUrl = createShareUserCardUrl(userId, userCardSpm);

        UM.shareWeb(activity, shareMedia, cardUrl, userIco, shareTitle, shareDes, "", new UM.UMListener() {
            @Override
            public void onResult(SHARE_MEDIA share_media) {
                RRetrofit.create(SocialService.class)
                        .share(Param.buildMap("type:user",
                                "item_id:" + userId,
                                "title:" + shareTitle,
                                "site:" + getShareMediaString(shareMedia),
                                "spm:" + userCardSpm,
                                "url:" + cardUrl
                        ))
                        .compose(Rx.transformer(String.class))
                        .subscribe(new BaseSingleSubscriber<String>() {
                            @Override
                            public void onSucceed(String bean) {
                                super.onSucceed(bean);
                            }
                        });
                T_.ok(activity.getString(R.string.share_success));
            }

        });
    }

    /**
     * 分享qq/微信好友 二维码
     */
    public static void shareQrcode(final Activity mActivity,
                                   final SHARE_MEDIA shareMedia
    ) {
        String userCardSpm = RSA.encode(createUserCardSpm(UserCache.getUserAccount()));
        final String h5_url = String.format("%s/user?to=%s&spm=%s", WAP_URL, UserCache.getUserAccount(), userCardSpm);
        final View rootView = mActivity.getLayoutInflater().inflate(R.layout.view_my_qr_code, null);

        final UserInfoBean userInfoBean = UserCache.instance().getUserInfoBean();
        final HnGlideImageView imageView = (HnGlideImageView) rootView.findViewById(R.id.image_view);
        HnGenderView genderView = (HnGenderView) rootView.findViewById(R.id.grade);
        TextView userName = (TextView) rootView.findViewById(R.id.username);
        final ImageView qrView = (ImageView) rootView.findViewById(R.id.qr_code_view);

        imageView.setAuth(userInfoBean.getIs_auth());
        genderView.setGender(userInfoBean.getSex(), userInfoBean.getGrade());
        userName.setText(userInfoBean.getUsername());

        Glide.with(mActivity)
                .load(userInfoBean.getAvatar())
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Bitmap cornerBitmap = BmpUtil.getRoundedCornerBitmap(resource, 1000);
                        imageView.setImageBitmap(cornerBitmap);
                        final String avatar = mActivity.getCacheDir().getAbsolutePath() + File.separator + UUID.randomUUID().toString();
                        AttachmentStore.saveBitmap(cornerBitmap, avatar, false);

                        createQrCode(h5_url,
                                (int) ResUtil.dpToPx(mActivity, 200),
                                Color.BLACK,
                                cornerBitmap)
                                .subscribe(new Action1<Bitmap>() {
                                    @Override
                                    public void call(Bitmap bitmap) {
                                        qrView.setImageBitmap(bitmap);
                                        onQrCodeCreateEnd(mActivity, convertViewToBitmap(rootView), shareMedia);
                                    }
                                });
                    }
                });

    }

    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                , View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        return view.getDrawingCache();
    }

    // 发送到qq / 微信
    private static void onQrCodeCreateEnd(final Activity activity, Bitmap bitmap, SHARE_MEDIA media) {
        UM.shareImage(activity, media, bitmap, new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {
                T_.ok(activity.getString(R.string.share_success));
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {

            }
        });
    }


    /**
     * 分享动态
     */
    public static void shareDynamic(final Activity activity,
                                    final SHARE_MEDIA shareMedia,
                                    final String userIco,
                                    final String itemId,
                                    final String shareTitle,
                                    final String shareDes) {
        final String spm = createDynamicSpm(itemId);
        final String url = createShareDynamicdUrl(itemId, spm);

        UM.shareWeb(activity, shareMedia, url, userIco, shareTitle, shareDes, "", new UM.UMListener() {

            @Override
            public void onResult(SHARE_MEDIA share_media) {
                RRetrofit.create(SocialService.class)
                        .share(Param.buildMap("type:discuss",
                                "item_id:" + itemId,
                                "title:" + shareTitle,
                                "site:" + getShareMediaString(shareMedia),
                                "spm:" + spm,
                                "url:" + url
                        ))
                        .compose(Rx.transformer(String.class))
                        .subscribe(new BaseSingleSubscriber<String>() {
                            @Override
                            public void onSucceed(String bean) {
                                super.onSucceed(bean);
                            }
                        });

                T_.ok(activity.getString(R.string.share_success));
            }
        });
    }

    public static void shareHotInfo(final Activity activity,
                                    final SHARE_MEDIA shareMedia,
                                    final String userIco,
                                    final String url,
                                    final String shareTitle,
                                    final String shareDes) {
        UM.shareWeb(activity, shareMedia, url, userIco, shareTitle, shareDes, "", new UM.UMListener() {

            @Override
            public void onResult(SHARE_MEDIA share_media) {
                T_.ok(activity.getString(R.string.share_success));
            }
        });
    }

    /**
     * 创建分享名片的Url
     */
    public static String createShareUserCardUrl(String userId) {
        //http://wap.klgwl.com/user?to=62255&spm=NaT2AxwnMdz2Rwf-dXNlcl82MjI1NV8xNDk1NTMyOTA3
        String spm = createUserCardSpm(userId);
        return createShareUserCardUrl(userId, spm);
    }

    public static String createShareUserCardUrl(String userId, String spm) {
        return "http://wap.klgwl.com/user?to=" + userId + "&spm=" + spm;
    }

    public static String createShareDynamicdUrl(String itemid, String spm) {
        return "http://wap.klgwl.com/discuss/detail?item_id=" + itemid + "&spm=" + spm;
    }

    @NonNull
    protected static String createUserCardSpm(String userId) {
        return Spm.create(UserCache.getUserAccount() + "_user_" + userId + "_" + System.currentTimeMillis() / 1000l);
    }

    @NonNull
    protected static String createDynamicSpm(String itemId) {
        return Spm.create(UserCache.getUserAccount() + "_discuss_" + itemId + "_" + System.currentTimeMillis() / 1000l);
    }

    public static String getShareMediaString(SHARE_MEDIA shareMedia) {
        //微信好友，QQ,微博，朋友圈，QQ空间，手机短信
        String result = "";
        switch (shareMedia) {
            case QQ:
                result = "QQ";
                break;
            case WEIXIN:
                result = "微信好友";
                break;
            case SINA:
                result = "微博";
                break;
            case WEIXIN_CIRCLE:
                result = "朋友圈";
                break;
            case QZONE:
                result = "QQ空间";
                break;
            default:
                result = "手机短信";
                break;
        }
        return result;
    }

    private static class Holder {
        static ShareControl instance = new ShareControl();
    }
}

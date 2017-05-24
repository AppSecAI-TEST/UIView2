package com.hn.d.valley.control;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.net.rsa.Spm;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.umeng.UM;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.service.SocialService;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

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

    private ShareControl() {
    }

    public static ShareControl instance() {
        return Holder.instance;
    }

    /**
     * 分享名片
     *
     * param shareType 分享类型【discuss-动态 news-资讯 user-名片 group-群 invite-邀请】
     * param item_id   item_id	否	int	数据id【动态id/资讯id/用户id/群id】
     * param site      分享的平台【微信好友，QQ,微博，朋友圈，QQ空间，手机短信】
     */
    public static void shareCardControl(final Activity activity,
                                        RBaseViewHolder holder,
                                        final String userId,
                                        final String shareTitle,
                                        final String shareDes) {
        holder.v(R.id.share_wx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUserCard(activity, SHARE_MEDIA.WEIXIN, userId, shareTitle, shareDes);
            }
        });
        holder.v(R.id.share_wxc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUserCard(activity, SHARE_MEDIA.WEIXIN_CIRCLE, userId, shareTitle, shareDes);
            }
        });
        holder.v(R.id.share_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUserCard(activity, SHARE_MEDIA.QQ, userId, shareTitle, shareDes);
            }
        });
        holder.v(R.id.share_qqz).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUserCard(activity, SHARE_MEDIA.QZONE, userId, shareTitle, shareDes);
            }
        });
    }

    public static void shareUserCard(final Activity activity,
                                     final SHARE_MEDIA shareMedia,
                                     final String userId,
                                     final String shareTitle,
                                     final String shareDes) {
        final String userCardSpm = createUserCardSpm(userId);
        final String cardUrl = createShareUserCardUrl(userId, userCardSpm);

        UM.shareWeb(activity, shareMedia, cardUrl, R.drawable.login_logo, shareTitle, shareDes, "", new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

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

    @NonNull
    protected static String createUserCardSpm(String userId) {
        return Spm.create(UserCache.getUserAccount() + "_user_" + userId + "_" + System.currentTimeMillis() / 1000l);
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

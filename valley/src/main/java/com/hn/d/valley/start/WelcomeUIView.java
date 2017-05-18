package com.hn.d.valley.start;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.umeng.UM;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/05/15 13:53
 * 修改人员：Robi
 * 修改时间：2017/05/15 13:53
 * 修改备注：
 * Version: 1.0.0
 */
public class WelcomeUIView extends BaseContentUIView {

    public static final UMShareListener SHARE_LISTENER = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {
            L.e("call: onStart([share_media])-> " + share_media);
        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            L.e("call: onResult([share_media])-> " + share_media);
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            L.e("call: onError([share_media, throwable])-> " + share_media);
            throwable.printStackTrace();
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            L.e("call: onCancel([share_media])-> " + share_media);
        }
    };

    @Override
    protected TitleBarPattern getTitleBar() {
        return null;
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_welcome);
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();

        //注册 登录
        mViewHolder.v(R.id.register_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(new RegisterUIView());
            }
        });
        mViewHolder.v(R.id.login_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceIView(new LoginUIView2(), false);
            }
        });
        mViewHolder.tv(R.id.register_view).setTextColor(SkinHelper.getThemeTextColorSelector());
        mViewHolder.tv(R.id.login_view).setTextColor(SkinHelper.getThemeTextColorSelector());

        //QQ 微信登录
        mViewHolder.v(R.id.qq_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UM.checkQQ(mActivity);
                UM.shareImage(mActivity, SHARE_MEDIA.QQ,
                        "http://klg-news.oss-cn-shenzhen.aliyuncs.com/3bb80ebaea4a45fb390d8bd14ef7e313.png",
                        R.drawable.login_logo,
                        SHARE_LISTENER);
            }
        });
        mViewHolder.v(R.id.weixin_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UM.checkWX(mActivity);
                UM.shareText(mActivity, SHARE_MEDIA.WEIXIN, "分享测试\n文本", SHARE_LISTENER);
            }
        });
    }
}

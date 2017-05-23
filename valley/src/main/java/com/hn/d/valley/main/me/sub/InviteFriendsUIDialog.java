package com.hn.d.valley.main.me.sub;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.umeng.UM;
import com.hn.d.valley.R;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/04/21 18:18
 * 修改人员：Robi
 * 修改时间：2017/04/21 18:18
 * 修改备注：
 * Version: 1.0.0
 */
public class InviteFriendsUIDialog extends UIIDialogImpl {
    @Override
    protected View inflateDialogView(RelativeLayout dialogRootLayout, LayoutInflater inflater) {
        return inflate(R.layout.dialog_invite_friends);
    }

    @Override
    protected void initDialogContentView() {
        super.initDialogContentView();
        mViewHolder.v(R.id.share_wx).setOnClickListener(new ClickListener() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                share(SHARE_MEDIA.WEIXIN);
            }
        });
        mViewHolder.v(R.id.share_wxc).setOnClickListener(new ClickListener() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                share(SHARE_MEDIA.WEIXIN_CIRCLE);
            }
        });
        mViewHolder.v(R.id.share_qq).setOnClickListener(new ClickListener() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                share(SHARE_MEDIA.QQ);
            }
        });
        mViewHolder.v(R.id.share_qqz).setOnClickListener(new ClickListener() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                share(SHARE_MEDIA.QZONE);
            }
        });
        mViewHolder.v(R.id.cancel_view).setOnClickListener(new ClickListener() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                share(SHARE_MEDIA.WEIXIN);
            }
        });

        ResUtil.setBgDrawable(mViewHolder.v(R.id.cancel_view), SkinHelper.getSkin().getThemeMaskBackgroundRoundSelector());
    }

    protected void share(SHARE_MEDIA shareMedia) {
        UM.shareWeb(mActivity,
                shareMedia,
                "http://wap.klgwl.com/Download",
                R.drawable.login_logo,
                getString(R.string.share_title),
                getString(R.string.app_name),
                "",
                new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {

                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {

                    }
                }
        );
    }

    class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finishDialog();
        }
    }
}

package com.hn.d.valley.main.me.sub;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.skin.SkinHelper;
import com.hn.d.valley.R;

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
        mViewHolder.v(R.id.share_wx).setOnClickListener(new ClickListener());
        mViewHolder.v(R.id.share_wxc).setOnClickListener(new ClickListener());
        mViewHolder.v(R.id.share_qq).setOnClickListener(new ClickListener());
        mViewHolder.v(R.id.share_qqz).setOnClickListener(new ClickListener());
        mViewHolder.v(R.id.cancel_view).setOnClickListener(new ClickListener());

        ResUtil.setBgDrawable(mViewHolder.v(R.id.cancel_view), SkinHelper.getSkin().getThemeMaskBackgroundRoundSelector());
    }

    class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finishDialog();
        }
    }
}

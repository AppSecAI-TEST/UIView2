package com.hn.d.valley.main.me.setting;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.uiview.dialog.UIDialog;
import com.hn.d.valley.R;
import com.hn.d.valley.activity.HnSplashActivity;
import com.hn.d.valley.base.BaseSubContentUIView;
import com.hn.d.valley.control.MainControl;

import butterknife.OnClick;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：设置界面
 * 创建人员：Robi
 * 创建时间：2017/02/14 17:05
 * 修改人员：Robi
 * 修改时间：2017/02/14 17:05
 * 修改备注：
 * Version: 1.0.0
 */
@Deprecated
public class SettingUIView extends BaseSubContentUIView {
    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_setting);
    }

    @Override
    protected String getTitleString() {
        return mActivity.getString(R.string.settting);
    }

    @OnClick({R.id.account_safe_layout, R.id.message_notify_layout, R.id.conceal_layout,
            R.id.currency_layout, R.id.feedback_layout, R.id.faq_layout, R.id.about_me_layout,
            R.id.user_agreement_layout, R.id.quit_login_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.account_safe_layout://帐号安全
                break;
            case R.id.message_notify_layout://消息通知
                break;
            case R.id.conceal_layout://隐私
                break;
            case R.id.currency_layout://通用
                break;
            case R.id.feedback_layout://意见反馈
                break;
            case R.id.faq_layout://常见问题
                break;
            case R.id.about_me_layout://关于我们
                break;
            case R.id.user_agreement_layout://用户协议
                break;
            case R.id.quit_login_layout://退出登录
                UIDialog.build()
                        .setDialogTitle(mActivity.getString(R.string.dialog_title_hint))
                        .setDialogContent(mActivity.getString(R.string.quit_login_hint))
                        .setOkListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MainControl.onLoginOut();
                                HnSplashActivity.launcher(mActivity, false);
                                mActivity.finish();
                            }
                        })
                        .showDialog(this);
                break;
        }
    }
}

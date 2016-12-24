package com.hn.d.valley.start;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.angcyo.library.utils.Anim;
import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.VerifyButton;
import com.hn.d.valley.BuildConfig;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.start.mvp.RegisterPresenter;
import com.hn.d.valley.start.mvp.Start;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：注册界面
 * 创建人员：Robi
 * 创建时间：2016/12/13 13:43
 * 修改人员：Robi
 * 修改时间：2016/12/13 13:43
 * 修改备注：
 * Version: 1.0.0
 */
public class RegisterUIView extends BaseUIView<RegisterPresenter> implements Start.IRegisterView {
    @BindView(R.id.phone_view)
    ExEditText mPhoneView;
    @BindView(R.id.verify_view)
    VerifyButton mVerifyView;
    @BindView(R.id.code_view)
    ExEditText mCodeView;

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_register);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.register)).setShowBackImageView(true);
    }

    @NonNull
    @Override
    protected UIBaseView.LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }

    @Override
    protected void initContentLayout() {
        super.initContentLayout();
        if (BuildConfig.DEBUG) {
            mCodeView.setText("888888");
        }
    }

    /**
     * 下一步
     */
    @OnClick(R.id.next_view)
    public void onNextClick() {
        if (!mPhoneView.isPhone()) {
            Anim.band(mPhoneView);
            return;
        }

        if (mCodeView.length() < mActivity.getResources().getInteger(R.integer.code_count)) {
            Anim.band(mCodeView);
            return;
        }

        startIView(new Register2UIView(this, mPhoneView.string(), mCodeView.string()));
    }

    /**
     * 用户协议
     */
    @OnClick(R.id.protocol_view)
    public void onProtocolClick() {

    }

    /**
     * 获取验证码
     */
    @OnClick(R.id.verify_view)
    public void onVerifyClick() {
        mVerifyView.run();
    }
}

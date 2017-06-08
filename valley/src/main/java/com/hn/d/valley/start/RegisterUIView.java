package com.hn.d.valley.start;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.library.utils.Anim;
import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.VerifyButton;
import com.hn.d.valley.BuildConfig;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.service.OtherService;
import com.hn.d.valley.skin.SkinUtils;
import com.hn.d.valley.start.mvp.RegisterPresenter;
import com.hn.d.valley.start.mvp.Start;
import com.hn.d.valley.x5.UseAgreementUIView;

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
    ExEditText mPhoneView;
    VerifyButton mVerifyView;
    ExEditText mCodeView;

    /**
     * 验证码
     */
    private String code = "";

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
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mPhoneView = v(R.id.phone_view);
        mVerifyView = v(R.id.verify_view);
        mCodeView = v(R.id.code_view);

        click(R.id.next_view, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextClick();
            }
        });
        click(R.id.protocol_view, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProtocolClick();
            }
        });
        click(R.id.verify_view, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onVerifyClick();
            }
        });

        if (BuildConfig.DEBUG) {
            mCodeView.setText("888888");
        }

        ResUtil.setBgDrawable(mViewHolder.v(R.id.next_view), LoginUIView.createLoginDrawable(mActivity));
        SkinUtils.setEditText(mViewHolder.eV(R.id.phone_view));
        SkinUtils.setEditText(mViewHolder.eV(R.id.code_view));
        mViewHolder.tv(R.id.verify_view).setTextColor(SkinHelper.getSkin().getThemeColor());
    }

    /**
     * 下一步
     */
    public void onNextClick() {
        if (!mPhoneView.isPhone()) {
            Anim.band(mPhoneView);
            return;
        }

        if (mCodeView.length() < mActivity.getResources().getInteger(R.integer.code_count)) {
            Anim.band(mCodeView);
            return;
        }
        if (BuildConfig.DEBUG) {
        } else {
            if (!TextUtils.equals(code, mCodeView.string())) {
                Anim.band(mCodeView);
                T_.show(mActivity.getString(R.string.code_error_tip));
                return;
            }
        }

        replaceIView(new Register2UIView(this, mPhoneView.string(), mCodeView.string()));
    }

    /**
     * 用户协议
     */
    public void onProtocolClick() {
        startIView(new UseAgreementUIView());
    }

    /**
     * 获取验证码
     */
    public void onVerifyClick() {
        if (!mPhoneView.isPhone()) {
            Anim.band(mPhoneView);
            return;
        }
        mVerifyView.run();
        add(RRetrofit.create(OtherService.class)
                .sendPhoneVerifyCode(Param.buildMap("phone:" + mPhoneView.string(), "type:register"))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onSucceed(String s) {
                        code = s;
                        T_.show(mActivity.getString(R.string.code_send_tip));
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        mVerifyView.endCountDown();
                    }
                })
        );
    }
}

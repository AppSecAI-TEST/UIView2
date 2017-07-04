package com.hn.d.valley.start;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.library.utils.Anim;
import com.angcyo.uiview.dialog.UIItemDialog;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.view.IView;
import com.angcyo.uiview.widget.ExEditText;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.Bean;
import com.hn.d.valley.base.constant.Action;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.oss.OssHelper;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.base.rx.BeforeSubscriber;
import com.hn.d.valley.base.rx.EmptyAction;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.LoginControl;
import com.hn.d.valley.library.fresco.DraweeViewUtil;
import com.hn.d.valley.skin.SkinUtils;
import com.hn.d.valley.start.mvp.Register2Presenter;
import com.hn.d.valley.start.mvp.Start;
import com.hn.d.valley.widget.HnLoading;
import com.jakewharton.rxbinding.view.RxView;
import com.lzy.imagepicker.ImagePickerHelper;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：注册界面 下一步
 * 创建人员：Robi
 * 创建时间：2016/12/13 13:43
 * 修改人员：Robi
 * 修改时间：2016/12/13 13:43
 * 修改备注：
 * Version: 1.0.0
 */
public class Register2UIView<B extends Bean<String>> extends BaseUIView<Start.IRegister2Presenter>
        implements Start.IRegister2View<String, B> {

    SimpleDraweeView mIcoView;
    ExEditText mNameView;
    TextView mSexView;
    ExEditText mPasswordView;

    String mIcoFilePath;
    String mIcoFilePathUrl;

    IView mRegisterIView;
    String phone, code;

    int sex = 1;//1:男  2;女
    TextView mFinishView;

    public Register2UIView(IView registerIView, String phone, String code) {
        mRegisterIView = registerIView;
        this.phone = phone;
        this.code = code;

        bindPresenter(new Register2Presenter());
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_register2);
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mIcoView = v(R.id.ico_view);
        mNameView = v(R.id.name_view);
        mSexView = v(R.id.sex_view);
        mPasswordView = v(R.id.password_view);
        mFinishView = v(R.id.finish_view);

        CompoundButton cb = v(R.id.show_password_checkbox);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onShowCheckbox(buttonView, isChecked);
            }
        });
        click(R.id.ico_view, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onIcoClick();
            }
        });

        v(R.id.sex_view).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onSexTouch(event);
            }
        });

        v(R.id.sex_view).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Register2UIView.this.onFocusChange(hasFocus);
            }
        });

        RxView.clicks(mFinishView)
                .debounce(Constant.DEBOUNCE_TIME, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Action.phone_register();
                        onFinishClick();
                    }
                });

        ResUtil.setBgDrawable(mFinishView, LoginUIView.createLoginDrawable(mActivity));
        SkinUtils.setEditText(mNameView);
        SkinUtils.setEditText(mNameView);
        SkinUtils.setEditText(mPasswordView);
        SkinUtils.setEditText(mSexView);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.register)).setShowBackImageView(true);
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }

    /**
     * 隐藏和显示密码
     */
    public void onShowCheckbox(CompoundButton checkbox, boolean show) {
        if (show) {
            mPasswordView.showPassword();
        } else {
            mPasswordView.hidePassword();
        }
    }

    /**
     * 头像
     */
    public void onIcoClick() {
        ImagePickerHelper.startImagePicker(mActivity, true, false, 1);
    }

    /**
     * 完成, 开始注册
     */
    public void onFinishClick() {

        if (TextUtils.isEmpty(mIcoFilePath)) {
            Anim.band(mIcoView);
            return;
        }

        if (TextUtils.isEmpty(mNameView.string())) {
            Anim.band(mNameView);
            return;
        }

        if (TextUtils.isEmpty(mSexView.getText())) {
            Anim.band(mSexView);
            return;
        }

        if (!mPasswordView.isPassword()) {
            Anim.band(mPasswordView);
            return;
        }

        if (TextUtils.isEmpty(mIcoFilePathUrl)) {
            mSubscriptions.clear();
            mSubscriptions.add(OssHelper.uploadAvatorImg(mIcoFilePath)
                    .doOnSubscribe(BeforeSubscriber.build(this))
                    .subscribe(new BaseSingleSubscriber<String>() {
                        @Override
                        public void onSucceed(String s) {
                            mIcoFilePathUrl = OssHelper.getAvatorUrl(s);
                            Hawk.put(phone, mIcoFilePathUrl);
                            mPresenter.register(mNameView.string(), mPasswordView.string(),
                                    phone, mIcoFilePathUrl, String.valueOf(sex), code);
                        }

                        @Override
                        public void onError(int code, String msg) {
                            T_.show(mActivity.getString(R.string.register_fial_tip) + code);
                        }
                    })
            );
        } else {
            mPresenter.register(RUtils.fixName(mNameView.string()), mPasswordView.string(),
                    phone, mIcoFilePathUrl, String.valueOf(sex), code);
        }
    }

    public boolean onSexTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            selectorSex();
        }
        return true;
    }

    public void onFocusChange(boolean focus) {
        if (focus) {
            selectorSex();
        }
    }

    private void selectorSex() {
        startIView(UIItemDialog.build()
                .addItem(mActivity.getString(R.string.man), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSexView.setText(R.string.man);
                        sex = 1;
                    }
                })
                .addItem(mActivity.getString(R.string.women), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSexView.setText(R.string.women);
                        sex = 2;
                    }
                }));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ArrayList<String> images = ImagePickerHelper.getImages(mActivity, requestCode, resultCode, data);
        if (images.isEmpty()) {
            return;
        }
        mIcoFilePath = images.get(0);
        mIcoFilePathUrl = "";
        DraweeViewUtil.setDraweeViewFile(mIcoView, mIcoFilePath);

        uploadFile(true);
    }

    private void uploadFile(final boolean background) {
        // 注册无uid 默认传 1
        UserCache.setUserAccount("1");
        mSubscriptions.add(OssHelper.uploadAvatorImg(mIcoFilePath)
                .doOnSubscribe(background ? EmptyAction.build() : BeforeSubscriber.build(this))
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onSucceed(String s) {
                        mIcoFilePathUrl = OssHelper.getAvatorUrl(s);
                        Hawk.put(phone, mIcoFilePathUrl);
                        if (!background) {
                            onFinishClick();
                        }
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        if (!background) {
                            onRequestFinish();
                            Register2UIView.this.onRequestError(code, msg);
                        }
                    }
                })
        );
    }

    @Override
    public void onRequestStart() {
        //UILoading.build().addDismissListener(this).show(mILayout);
        HnLoading.show(mILayout).addDismissListener(this);
    }

    @Override
    public void onRequestFinish() {
        HnLoading.hide();
    }

    @Override
    public void onRequestError(int code, @NonNull String msg) {
        super.onRequestError(code, msg);
        HnLoading.hide();
        T_.error(getString(R.string.register_error_tip));
    }

    @Override
    public void onRegisterSuccess(B bean) {
//        finishIView(mRegisterIView, false, true);
//        finishIView(this, false, true);
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(Constant.LOGIN_INFO, new LoginUserInfo(phone, mPasswordView.string(), mIcoFilePathUrl));
//        showIView(((UILayoutImpl) mILayout).getViewPatternWithClass(LoginUIView.class).mView, false, bundle);
        LoginControl.instance().setFirstRegister(true);
        // 开钱包账户
        LoginUIView2.login(mActivity, mParentILayout, mSubscriptions,
                phone, mPasswordView.string(), "", "", "", "", "");
    }
}

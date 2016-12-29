package com.hn.d.valley.start;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.library.facebook.DraweeViewUtil;
import com.angcyo.library.utils.Anim;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.dialog.UIItemDialog;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.widget.ExEditText;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.BuildConfig;
import com.hn.d.valley.R;
import com.hn.d.valley.activity.HnUIMainActivity;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.Bean;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.T_;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.AmapBean;
import com.hn.d.valley.bean.LoginBean;
import com.hn.d.valley.bean.LoginUserInfo;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.other.AmapUIView;
import com.hn.d.valley.nim.RNim;
import com.hn.d.valley.start.mvp.LoginPresenter;
import com.hn.d.valley.start.mvp.Start;
import com.hn.d.valley.utils.RAmap;
import com.hn.d.valley.widget.HnLoading;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.jakewharton.rxbinding.view.RxView;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.orhanobut.hawk.Hawk;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：登录界面
 * 创建人员：Robi
 * 创建时间：2016/12/12 18:14
 * 修改人员：Robi
 * 修改时间：2016/12/12 18:14
 * 修改备注：
 * Version: 1.0.0
 */
public class LoginUIView extends BaseUIView<Start.ILoginPresenter> implements Start.ILoginView<LoginBean, Bean<LoginBean>> {
    @BindView(R.id.ico_view)
    SimpleDraweeView mIcoView;
    @BindView(R.id.phone_view)
    ExEditText mPhoneView;
    @BindView(R.id.password_view)
    ExEditText mPasswordView;
    @BindView(R.id.login_view)
    TextView mLoginView;

    AbortableFuture loginFuture;

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_login);
        fixInsersTop();
    }

    @Override
    protected void initContentLayout() {
        super.initContentLayout();

        if (BuildConfig.DEBUG) {
            RxView.longClicks(mLoginView)
                    .debounce(Constant.DEBOUNCE_TIME, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            showLoadView();
                            RNim.debugLogin(new Action1<Boolean>() {
                                @Override
                                public void call(Boolean aBoolean) {
                                    hideLoadView();
                                    if (aBoolean) {
                                        jumpToMain();
                                    } else {
                                        T_.show("登录云信失败.");
                                    }
                                }
                            });
                        }
                    });
        }
        /**
         * 登录按钮
         */
        RxView.clicks(mLoginView)
                .debounce(16, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                        if (!mPhoneView.isPhone()) {
                            Anim.band(mPhoneView);
                            return;
                        }

                        if (!mPasswordView.isPassword()) {
                            Anim.band(mPasswordView);
                            return;
                        }

                        mPresenter.login(mPhoneView.string(), mPasswordView.string(), "", "", "", "", "");
                    }
                });

        UserCache.instance().getLoginBeanObservable()
                .subscribe(new Action1<LoginBean>() {
                    @Override
                    public void call(LoginBean loginBean) {
                        mPhoneView.setText(loginBean.getPhone());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (BuildConfig.DEBUG) {
                            mPhoneView.setText("18888888885");
                        }
                    }
                });

        if (BuildConfig.DEBUG) {
            mPasswordView.setText("123456");
        }
    }

    @Override
    public int getDefaultBackgroundColor() {
        return Color.TRANSPARENT;
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return null;
    }

    @Override
    public void onViewCreate() {
        super.onViewCreate();
        bindPresenter(new LoginPresenter());
    }

    @Override
    public void onViewHide() {
        super.onViewHide();
        RAmap.stopLocation();
    }

    @Subscribe()
    public void onEvent(AmapBean bean) {
        if (bean.result) {
            L.w(bean.getString());
            RAmap.stopLocation();
        } else {
            L.w("定位失败");
//            RealmResults<AmapBean> results = RRealm.where(AmapBean.class).findAll();
//            L.e("----------------------------------------------------");
//            for (AmapBean amap : results) {
//                L.w(amap.getString());
//            }
//            L.e("----------------------------------------------------");
        }
    }

    @OnTextChanged(R.id.phone_view)
    public void onPhoneTextChanged(Editable editable) {
        if (TextUtils.isEmpty(editable)) {
            mIcoView.setImageURI("");
        } else {
            String avatar = Hawk.get(editable.toString(), "");
            DraweeViewUtil.setDraweeViewHttp(mIcoView, avatar);
        }
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        if (bundle != null) {
            LoginUserInfo info = bundle.getParcelable(Constant.LOGIN_INFO);
            mPhoneView.setText(info.phone);
            mPasswordView.setText(info.pwd);
            showUserIco(info.icoUrl);
            mLoginView.callOnClick();
        }
        RAmap.startLocation();
    }

    private void showUserIco(String icoUrl) {
        DraweeViewUtil.setDraweeViewHttp(mIcoView, icoUrl);
    }

    @OnClick(R.id.ico_view)
    public void onIcoClick() {
        startIView(UIItemDialog.build()
                .addItem("中文", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Param.changeLang(1);
                        mActivity.recreate();
                    }
                })
                .addItem("English", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Param.changeLang(3);
                        mActivity.recreate();
                    }
                })
        );
    }

    /**
     * 隐藏和显示密码
     */
    @OnCheckedChanged(R.id.show_password_checkbox)
    public void onShowCheckbox(CompoundButton checkbox, boolean show) {
        if (show) {
            mPasswordView.showPassword();
        } else {
            mPasswordView.hidePassword();
        }
    }

    /**
     * 快速注册按钮
     */
    @OnClick(R.id.register_view)
    public void onRegisterClick() {
        startIView(new RegisterUIView());
    }

    /**
     * 忘记密码按钮
     */
    @OnClick(R.id.forget_view)
    public void onForgetClick() {
        //HnChatActivity.launcher(mActivity, "50033");
        //HnUIMainActivity.launcher(mActivity);
        com.hn.d.valley.main.HnMainActivity.launcher(mActivity);
    }

    /**
     * 微信登录按钮
     */
    @OnClick(R.id.weixin_view)
    public void onWeixinClick() {
        startIView(new AmapUIView());
//        HnUIMainActivity.launcher(mActivity);
    }

    /**
     * QQ登录按钮
     */
    @OnClick(R.id.qq_view)
    public void onQqClick() {
        startIView(UIDialog.build()
                .setDialogContent(mActivity.getString(R.string.account_exception))
                .setGravity(Gravity.CENTER_VERTICAL));
    }

    @Override
    public void onLoginSuccess(Bean<LoginBean> bean) {
        LoginBean loginBean = bean.data;
        L.i("登录成功:" + loginBean.getUsername());

        showUserIco(loginBean.getAvatar());

        //登录成功, 保存用户的头像
        Hawk.put(loginBean.getPhone(), loginBean.getAvatar());
        UserCache.instance().setLoginBean(loginBean);

        //2: 登录云信
        RNim.login(loginBean.getUid(), loginBean.getYx_token(),
                new RequestCallbackWrapper<LoginInfo>() {
                    @Override
                    public void onResult(int code, LoginInfo result, Throwable exception) {
                        if (code == ResponseCode.RES_SUCCESS) {
                            onRequestCancel();
                            jumpToMain();
                        } else {
                            T_.show("登录失败!");
                            onRequestCancel();
                        }
                    }
                });
    }

    @Override
    public void onRequestStart() {
        HnLoading.show(mILayout).addDismissListener(this);
    }

    @Override
    public void onRequestFinish() {
        //云信登录成功后, 再取消对话框
    }

    @Override
    public void onRequestCancel() {
        HnLoading.hide();
    }

    /**
     * 跳转至主页
     */
    private void jumpToMain() {
        //replaceIView(new MainUIView(500));
        HnUIMainActivity.launcher(mActivity);
//        HnMainActivity.launcher(mActivity);
        mActivity.finish();
    }

    @Override
    public Animation loadLayoutAnimation() {
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1f,
                Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, 0f);
        setDefaultConfig(translateAnimation);
        return translateAnimation;
    }

}

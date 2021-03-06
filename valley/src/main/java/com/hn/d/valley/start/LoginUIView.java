package com.hn.d.valley.start;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.angcyo.library.utils.Anim;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.dialog.UIItemDialog;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ExEditText;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.BuildConfig;
import com.hn.d.valley.R;
import com.hn.d.valley.activity.HnUIMainActivity;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.Bean;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.LoginUserInfo;
import com.hn.d.valley.bean.realm.AmapBean;
import com.hn.d.valley.bean.realm.LoginBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.library.fresco.DraweeViewUtil;
import com.hn.d.valley.main.me.setting.SetPasswordUIView;
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

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

//import rx.android.schedulers.AndroidSchedulers;

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
@Deprecated
public class LoginUIView extends BaseUIView<Start.ILoginPresenter> implements Start.ILoginView<LoginBean, Bean<LoginBean>> {
    SimpleDraweeView mIcoView;
    ExEditText mPhoneView;
    ExEditText mPasswordView;
    TextView mLoginView;

    AbortableFuture loginFuture;

    public static Drawable createLoginDrawable(Context context) {
//        return ResUtil.generateRoundRippleMaskDrawable(context.getResources().getDimensionPixelOffset(R.dimen.little_round_radius), Color.WHITE,
//                SkinHelper.getSkin().getThemeDarkColor(), SkinHelper.getSkin().getThemeColor());
        return SkinHelper.getSkin().getThemeMaskBackgroundRoundSelector();
    }

    @Override
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_login);
        mBaseRootLayout.setBackgroundResource(R.drawable.login_pic);
        fixInsertsTop();

        ResUtil.setBgDrawable(baseContentLayout.findViewById(R.id.login_view), createLoginDrawable(mActivity));
        ((TextView) baseContentLayout.findViewById(R.id.forget_view)).setTextColor(SkinHelper.getThemeTextColorSelector());
        ((TextView) baseContentLayout.findViewById(R.id.register_view)).setTextColor(SkinHelper.getSkin().getThemeSubColor());
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mIcoView = v(R.id.ico_view);
        mPhoneView = v(R.id.phone_view);
        mPasswordView = v(R.id.password_view);
        mLoginView = v(R.id.login_view);

        if (BuildConfig.DEBUG) {
            RxView.longClicks(mLoginView)
                    .debounce(Constant.DEBOUNCE_TIME, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            RNim.debugLogin(new Action1<Boolean>() {
                                @Override
                                public void call(Boolean aBoolean) {
//                                    if (aBoolean) {
//                                        jumpToMain();
//                                    } else {
//                                        T_.show("登录云信失败.");
//                                    }
                                }
                            });
                            jumpToMain();
                        }
                    });

            RxView.longClicks(mIcoView)
                    .map(new Func1<Void, Object>() {
                        @Override
                        public Object call(Void aVoid) {
                            T_.error("莫慌, 正在模拟Crash...");
                            return null;
                        }
                    })
                    .delay(100, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Object>() {
                        @Override
                        public void call(Object o) {
                            throw new NullPointerException("测试Bug...");
                        }
                    });
        }
        /**
         * 登录按钮
         */
        RxView.clicks(mLoginView)
//                .debounce(16, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
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
    public void onViewCreate(View rootView) {
        super.onViewCreate(rootView);
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

//    @OnTextChanged(R.id.phone_view)
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
        RAmap.startLocation(true);
    }

    private void showUserIco(String icoUrl) {
        DraweeViewUtil.setDraweeViewHttp(mIcoView, icoUrl);
    }

//    @OnClick(R.id.ico_view)
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
                .addItem("内网", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RRetrofit.BASE_URL = RRetrofit.DEBUG_URL;
                    }
                })
                .addItem("外网", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RRetrofit.BASE_URL = RRetrofit.RELEASE_URL;
                    }
                })
                .addItem("当前地址:" + RRetrofit.BASE_URL, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HnLoading.show(mILayout);
                    }
                })
        );
    }

    /**
     * 隐藏和显示密码
     */
//    @OnCheckedChanged(R.id.show_password_checkbox)
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
//    @OnClick(R.id.register_view)
    public void onRegisterClick() {
        startIView(new RegisterUIView());
    }

    /**
     * 忘记密码按钮
     */
//    @OnClick(R.id.forget_view)
    public void onForgetClick(View view) {
        //HnChatActivity.launcher(mActivity, "50033");
        //HnUIMainActivity.launcher(mActivity);
        //com.hn.d.valley.main.HnMainActivity.launcher(mActivity);
        //HnTestActivity.launcher(mActivity);
        //T_.show("测试");
        //mActivity.getWindow().setBackgroundDrawableResource(R.drawable.default_page);
        //startIView(new X5WebUIView("http://www.baidu.com"));
        startIView(new SetPasswordUIView(SetPasswordUIView.TYPE_FORGET_PW));
    }

    /**
     * 微信登录按钮
     */
//    @OnClick(R.id.weixin_view)
    public void onWeixinClick() {
        if (!BuildConfig.DEBUG) {
            return;
        }

        AmapBean amapBean = new AmapBean();
        amapBean.address = "测试位置";
        amapBean.latitude = 39.90923;
        amapBean.longitude = 116.397428;
        startIView(new AmapUIView(null, amapBean, UserCache.getUserAccount(), true));
//        HnUIMainActivity.launcher(mActivity);
    }

    /**
     * QQ登录按钮
     */
//    @OnClick(R.id.qq_view)
    public void onQqClick() {
        if (!BuildConfig.DEBUG) {
            return;
        }

//        startIView(UIDialog.build()
//                .setDialogContent(mActivity.getString(R.string.account_exception))
//                .setGravity(Gravity.CENTER_VERTICAL));

        //HnLoading.show(mILayout);
//        ImagePickerHelper.startImagePicker(mActivity, fals
//
// e, true, true, 50);
        //startIView(new RecommendUser2UIView());
        //startIView(new VideoRecordUIView(null));

//        UIDialog.build().setDialogTitle(mActivity.getString(R.string.tip))
//                .setDialogContent(mActivity.getString(R.string.dynamic_top_tip))
//                .setCancelText("").setOkText(mActivity.getString(R.string.known))
//                .setCanCanceledOnOutside(true)
//                .setCanTouchOnOutside(false)
//                .setCanDoubleCancel(true)
//                .showDialog(this);
        mParentILayout.startIView(new RecommendUser2UIView());
        UIDialog.build().setDialogTitle(mActivity.getString(R.string.tip))
                .setDialogContent(mActivity.getString(R.string.dynamic_top_tip))
                .setCancelText("").setOkText(mActivity.getString(R.string.known))
                .setCanCanceledOnOutside(false)
                .showDialog(this);
//        UIDialog.build().setDialogTitle(mActivity.getString(R.string.tip))
//                .setDialogContent(mActivity.getString(R.string.dynamic_top_tip))
//                .setCancelText("").setOkText(mActivity.getString(R.string.known))
//                .setCanCanceledOnOutside(false)
//                .showDialog(this);

        //startIView(new KLJUIView());

//        startIView(new UserDetailUIView2(UserCache.getUserAccount()));
        //startIView(new X5WebUIView("http://wap.klgwl.com/user/rank"));

        //startIView(new VipWebUIView());

//        RTextView textView = mViewHolder.v(R.id.test_view);
//        textView.setMaxLength(8);
//        textView.setHighlightWord("周杰伦");
    }

    @Override
    public void onLoginSuccess(Bean<LoginBean> bean) {
        final LoginBean loginBean = bean.data;
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
                        HnLoading.hide();
                        L.i(loginBean.getUid() + ":::" + loginBean.getYx_token());
                        if (code == ResponseCode.RES_SUCCESS) {
                            jumpToMain();
                        } else {
                            T_.show(getString(R.string.network_exception));
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
    public void onRequestError(int code, @NonNull String msg) {
        super.onRequestError(code, msg);
        HnLoading.hide();
    }

    /**
     * 跳转至主页
     */
    private void jumpToMain() {
        //replaceIView(new MainUIView(500));
        HnUIMainActivity.launcher(mActivity, true);
//        HnMainActivity.launcher(mActivity);
        mActivity.finish();
    }

    @Override
    public Animation loadLayoutAnimation() {
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1f,
                Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, 0f);
        setDefaultConfig(translateAnimation, false);
        return translateAnimation;
    }
}

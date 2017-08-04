package com.hn.d.valley.start;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.angcyo.github.utilcode.utils.PhoneUtils;
import com.angcyo.library.utils.Anim;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.dialog.UILoading;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.RSoftInputLayout;
import com.hn.d.valley.BuildConfig;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.realm.LoginBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.LoginControl;
import com.hn.d.valley.main.me.setting.SetPasswordUIView;
import com.hn.d.valley.nim.RNim;
import com.jakewharton.rxbinding.view.RxView;
import com.orhanobut.hawk.Hawk;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.hn.d.valley.start.LoginUIView2.KEY_LOGIN_PHONE;
import static com.hn.d.valley.start.LoginUIView2.jumpToMain;
import static com.hn.d.valley.start.LoginUIView2.login;

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

    public final UMShareListener SHARE_LISTENER = new UMShareListener() {
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
    public final UMAuthListener AUTH_LISTENER = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {
            L.e("call: onStart([share_media])-> ");
            UILoading.show2(mILayout).setLoadingTipText("正在授权...");
        }

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            L.e("call: onComplete([share_media, i, map])-> " + i);
            RUtils.logMap(map);
            UILoading.hide();
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            L.e("call: onError([share_media, i, throwable])-> " + i);
            throwable.printStackTrace();
            UILoading.hide();
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
            L.e("call: onCancel([share_media, i])-> " + i);
            UILoading.hide();
        }
    };
    private LoginControl.OnLoginListener mLoginListener = new LoginControl.OnLoginListener() {
        @Override
        public void onLoginStart() {
            UILoading.show2(mILayout)
                    .setLoadingTipText(getString(R.string.authing))
                    .addDismissListener(new UIIDialogImpl.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            LoginControl.instance().setCancel(true);
                        }
                    });
        }

        @Override
        public void onLoginSuccess(LoginBean bean) {
            UILoading.hide();
            LoginUIView2.onLoginSuccess(mActivity, bean);
        }

        @Override
        public void onLoginError(Throwable exception) {
            UILoading.hide();
            exception.printStackTrace();
            if (exception instanceof RException) {
                // 用户被封
                if (((RException) exception).getCode() == 1067) {
                    startIView(UIDialog.build()
                            .setDialogContent(getString(R.string.text_third_login_can_not_login))
                            .setOkListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startIView(UIDialog.build()
                                            .setDialogContent("拨打客服电话:0755-26777170")
                                            .setOkListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    PhoneUtils.call("0755-26777170");
                                                }
                                            })
                                            .setGravity(Gravity.CENTER));
                                }
                            })
                            .setGravity(Gravity.CENTER));
                }
            }
        }

        @Override
        public void onLoginCancel() {
            UILoading.hide();
        }
    };
    private ExEditText mPhoneView;
    private ExEditText mPasswordView;
    private TextView mLoginView;

    @Override
    protected TitleBarPattern getTitleBar() {
        return null;
    }

    @Override
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
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
        //已有账户
        mViewHolder.v(R.id.login_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //replaceIView(new LoginUIView2(), true);
//                mViewHolder.v(R.id.login_control_layout1).setVisibility(View.INVISIBLE);
//                mViewHolder.v(R.id.login_control_layout2).setVisibility(View.VISIBLE);
                animToLogin2();
            }
        });
        mViewHolder.tv(R.id.register_view).setTextColor(SkinHelper.getThemeTextColorSelector());
        mViewHolder.tv(R.id.login_view).setTextColor(SkinHelper.getThemeTextColorSelector());

        //QQ 微信登录
        mViewHolder.v(R.id.qq_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //UM.checkQQ(mActivity);
//                UM.shareImage(mActivity, SHARE_MEDIA.QQ,
//                        "http://klg-news.oss-cn-shenzhen.aliyuncs.com/3bb80ebaea4a45fb390d8bd14ef7e313.png",
//                        R.drawable.login_logo,
//                        SHARE_LISTENER);
                //UM.authVerify(mActivity, SHARE_MEDIA.QQ, AUTH_LISTENER);

                //UM.getPlatformInfo(mActivity, SHARE_MEDIA.QQ, AUTH_LISTENER);
                loginQQ();
            }
        });
        mViewHolder.v(R.id.weixin_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //UM.checkWX(mActivity);
//                UM.shareImage(mActivity, SHARE_MEDIA.WEIXIN, "http://klg-news.oss-cn-shenzhen.aliyuncs.com/3bb80ebaea4a45fb390d8bd14ef7e313.png",
//                        R.drawable.login_logo, SHARE_LISTENER);
                //UM.authVerify(mActivity, SHARE_MEDIA.WEIXIN, AUTH_LISTENER);
                //UM.getPlatformInfo(mActivity, SHARE_MEDIA.WEIXIN, AUTH_LISTENER);
                loginWX();
            }
        });

        initLogin2();
    }

    private void animToLogin2() {
        View control1 = mViewHolder.v(R.id.login_control_layout1);
        control1
                .animate()
                .translationX(-control1.getMeasuredWidth())
                .setDuration(300)
                .start();

        View control2 = mViewHolder.v(R.id.login_control_layout2);
        ViewCompat.setTranslationX(control2, control2.getMeasuredWidth());
        control2.setVisibility(View.VISIBLE);
        control2
                .animate()
                .translationX(0)
                .setDuration(300)
                .start();

        View logoView = mViewHolder.v(R.id.logo_view);
        logoView.animate()
                .translationY(-logoView.getBottom())
                .setDuration(300)
                .start();

        View tipView = mViewHolder.v(R.id.tip_view);
        tipView.animate()
                .translationY(-1.5f * tipView.getMeasuredHeight())
                .scaleX(2f)
                .scaleY(2f)
                .setDuration(300)
                .start();
    }

    private void animToLogin1() {
        View control1 = mViewHolder.v(R.id.login_control_layout1);
        control1
                .animate()
                .translationX(0)
                .setDuration(300)
                .start();

        View control2 = mViewHolder.v(R.id.login_control_layout2);
        control2
                .animate()
                .translationX(control2.getMeasuredWidth())
                .setDuration(300)
                .start();

        View logoView = mViewHolder.v(R.id.logo_view);
        logoView.animate()
                .translationY(0)
                .setDuration(300)
                .start();

        View tipView = mViewHolder.v(R.id.tip_view);
        tipView.animate()
                .translationY(0)
                .scaleX(1)
                .scaleY(1)
                .setDuration(300)
                .start();
    }

    private void initLogin2() {
        //键盘弹出, 隐藏欢迎
        RSoftInputLayout softInputLayout = mViewHolder.v(R.id.login_control_layout2);
        softInputLayout.addOnEmojiLayoutChangeListener(new RSoftInputLayout.OnEmojiLayoutChangeListener() {
            @Override
            public void onEmojiLayoutChange(boolean isEmojiShow, boolean isKeyboardShow, int height) {
                showOrHideTipView(!isKeyboardShow);
            }
        });

        mPhoneView = mViewHolder.v(R.id.phone_view);
        mPhoneView.setInputTipTextList(RUtils.split(Hawk.get(KEY_LOGIN_PHONE, "")));

        mPasswordView = mViewHolder.v(R.id.password_view);
        mLoginView = mViewHolder.v(R.id.login_view2);

        // 隐藏和显示密码
        mViewHolder.cV(R.id.show_password_checkbox).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mPasswordView.showPassword();
                } else {
                    mPasswordView.hidePassword();
                }
            }
        });

        //忘记密码
        mViewHolder.v(R.id.forget_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (BuildConfig.DEBUG) {
//                    startIView(new ChooseTagsUIView());
//                } else {
                startIView(new SetPasswordUIView(SetPasswordUIView.TYPE_FORGET_PW));
//                }
            }
        });
        //手机注册
        mViewHolder.v(R.id.register_view2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(new RegisterUIView());
            }
        });
        //第三方登录
        mViewHolder.v(R.id.other_login_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //replaceIView(new WelcomeUIView(), false);
                //mViewHolder.v(R.id.login_control_layout1).setVisibility(View.VISIBLE);
                //mViewHolder.v(R.id.login_control_layout2).setVisibility(View.INVISIBLE);
                animToLogin1();
            }
        });

        mViewHolder.tv(R.id.forget_view).setTextColor(SkinHelper.getThemeTextColorSelector());
        mViewHolder.tv(R.id.register_view).setTextColor(SkinHelper.getThemeTextColorSelector());
        mViewHolder.tv(R.id.other_login_view).setTextColor(SkinHelper.getThemeTextColorSelector());

        //登录
        //调试模式下, 长按直接登录
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
                            jumpToMain(mActivity);
                        }
                    });

            RxView.longClicks(mViewHolder.v(R.id.tip_view))
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

        //正常登录逻辑
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

                        login(mActivity, mParentILayout, mSubscriptions,
                                mPhoneView.string(), mPasswordView.string(),
                                "", "", "", "", "", "");
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

    protected void showOrHideTipView(boolean show) {
        mViewHolder.v(R.id.tip_view).setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        LoginControl.instance().setCancel(true);
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * QQ登录
     */
    private void loginQQ() {
        LoginControl.instance().loginQQ(mActivity, mLoginListener);
    }

    /**
     * 微信登录
     */
    private void loginWX() {
        LoginControl.instance().loginWX(mActivity, mLoginListener);
    }
}

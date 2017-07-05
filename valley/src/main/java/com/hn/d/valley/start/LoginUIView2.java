package com.hn.d.valley.start;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.library.utils.Anim;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.RApplication;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.net.rsa.RSA;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.RSoftInputLayout;
import com.hn.d.valley.BuildConfig;
import com.hn.d.valley.R;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.activity.HnUIMainActivity;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.receiver.JPushReceiver;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.LoginUserInfo;
import com.hn.d.valley.bean.realm.LoginBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.AutoLoginControl;
import com.hn.d.valley.control.LoginControl;
import com.hn.d.valley.main.me.setting.SetPasswordUIView;
import com.hn.d.valley.main.wallet.WalletHelper;
import com.hn.d.valley.nim.RNim;
import com.hn.d.valley.start.service.StartService;
import com.hn.d.valley.widget.HnLoading;
import com.jakewharton.rxbinding.view.RxView;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.orhanobut.hawk.Hawk;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：新的登录界面
 * 创建人员：Robi
 * 创建时间：2017/05/15 13:50
 * 修改人员：Robi
 * 修改时间：2017/05/15 13:50
 * 修改备注：
 * Version: 1.0.0
 */
public class LoginUIView2 extends BaseContentUIView {

    public static final String KEY_LOGIN_PHONE = "key_login_phone";
    private ExEditText mPhoneView;
    private ExEditText mPasswordView;
    private TextView mLoginView;

    /**
     * 跳转至主页
     */
    private static void jumpToMain(Activity activity) {
        //replaceIView(new MainUIView(500));
        HnUIMainActivity.launcher(activity, true);
//        HnMainActivity.launcher(mActivity);
        activity.finish();
    }

    public static void onLoginSuccess(final Activity activity, LoginBean loginBean) {
        L.i("登录成功:" + loginBean.getUsername());

        //showUserIco(loginBean.getAvatar());

        // 切换账号 清除钱包
        WalletHelper.getInstance().clean();

        //第一次登录开户
        if (LoginControl.instance().isFirstRegister()) {
            WalletHelper.getInstance().openAccount(loginBean.getUid());
        }

        //登录成功, 保存用户的头像
        Hawk.put(loginBean.getPhone(), loginBean.getAvatar());
        UserCache.instance().setLoginBean(loginBean);

        if (ExEditText.isPhone(loginBean.getPhone())) {
            //保存登录成功的手机号码
            String oldPhone = Hawk.get(KEY_LOGIN_PHONE, "");
            if (!oldPhone.contains(loginBean.getPhone())) {
                Hawk.put(KEY_LOGIN_PHONE, oldPhone + "," + loginBean.getPhone());
            }
        }

        //2: 登录云信
        RNim.login(loginBean.getUid(), loginBean.getYx_token(),
                new RequestCallbackWrapper<LoginInfo>() {
                    @Override
                    public void onResult(int code, LoginInfo result, Throwable exception) {
                        HnLoading.hide();

                        if (code == ResponseCode.RES_SUCCESS) {
                            jumpToMain(activity);
                        } else {
                            T_.show(activity.getResources().getString(R.string.network_exception));
                        }
                    }
                });
    }

    public static void login(final Activity activity, final ILayout iLayout, CompositeSubscription sub,
                             final String phone, final String pwd, final String open_id, final String open_type, final String open_nick,
                             final String open_avatar, final String open_sex, String code /**登录保护的验证码*/) {

        Map<String, String> map = new HashMap<>();
        if (TextUtils.isEmpty(phone)) {
            //第三方登录
            map.put("open_id", open_id);
            map.put("open_type", open_type);
            map.put("open_nick", open_nick);
            map.put("open_avatar", open_avatar);
            map.put("open_sex", open_sex);
        } else {
            //手机号登录
            map.put("phone", phone);
            String encode = RSA.encode(pwd);
            map.put("pwd", encode);

            if (!TextUtils.isEmpty(code)) {
                map.put("code", code);
            }

            AutoLoginControl.saveAutoLoginBean(AutoLoginControl.AutoLoginBean.LOGIN_PHONE,
                    "", phone, encode);
        }

        String jpushId;
        if (TextUtils.isEmpty(JPushReceiver.mRegistrationId)) {
            jpushId = JPushInterface.getRegistrationID(ValleyApp.getApp());
        } else {
            jpushId = JPushReceiver.mRegistrationId;
        }
        L.e("push_device_id-->" + jpushId);
        map.put("push_device_id", jpushId);

        map.put("os_version", Build.VERSION.RELEASE);
        map.put("phone_model", Build.MODEL);
        map.put("device_id", RApplication.getIMEI());

//        UISubscriber<LoginBean, Bean<LoginBean>, Start.ILoginView> subscriber =
//                new UISubscriber<LoginBean, Bean<LoginBean>, Start.ILoginView>(mBaseView) {
//                    @Override
//                    public void onSuccess(Bean<LoginBean> loginBeanBean) {
//                        super.onSuccess(loginBeanBean);
//                        mBaseView.onLoginSuccess(loginBeanBean);
//                    }
//                };
//

//
//        add(RRetrofit.create(StartService.class)
//                .userLogin2(Param.map(map))
//                .compose(Rx.transformer(LoginBean.class))

        HnLoading.show(iLayout);
        sub.add(RRetrofit.create(StartService.class)
                .userLogin2(Param.map(map))
                .compose(Rx.transformer(LoginBean.class))
                .subscribe(new BaseSingleSubscriber<LoginBean>() {
                    @Override
                    public void onSucceed(LoginBean bean) {
                        onLoginSuccess(activity, bean);
                    }

                    @Override
                    public void onError(int code, String msg) {

                    }

                    @Override
                    public void onEnd(boolean isError, int errorCode, boolean isNoNetwork, Throwable e) {
                        super.onEnd(isError, errorCode, isNoNetwork, e);
                        if (isError) {
                            HnLoading.hide();

                            boolean show = false;
                            String type = "";
                            if (errorCode == 1063) {
                                //登录保护
                                show = true;
                                type = "login_protect";
                            } else if (errorCode == 1029) {
                                //账户被锁
                                show = true;
                                type = "unlock";
                            }

                            if (show) {
                                final String finalType = type;
                                UIDialog.build()
                                        .setDialogContent("unlock".equalsIgnoreCase(type) ?
                                                activity.getString(R.string.unlock_tip) :
                                                activity.getString(R.string.login_protect_tip))
                                        .setOkClick(new UIDialog.OnDialogClick() {
                                            @Override
                                            public void onDialogClick(UIDialog dialog, View clickView) {
                                                iLayout.startIView(new LoginProtectCodeUIView(phone, pwd, open_id, open_type,
                                                        open_nick, open_avatar, open_sex, finalType));
                                            }
                                        })
                                        .showDialog(iLayout);
                            }
                        }
                    }
                }));
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return null;
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_login2);
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        if (bundle != null) {
            LoginUserInfo info = bundle.getParcelable(Constant.LOGIN_INFO);
//            mPhoneView.setText(info.phone);
//            mPasswordView.setText(info.pwd);
//            showUserIco(info.icoUrl);
//            mLoginView.callOnClick();
        }
//        RAmap.startLocation();
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        //键盘弹出, 隐藏欢迎
        RSoftInputLayout softInputLayout = mViewHolder.v(R.id.soft_input_layout);
        softInputLayout.addOnEmojiLayoutChangeListener(new RSoftInputLayout.OnEmojiLayoutChangeListener() {
            @Override
            public void onEmojiLayoutChange(boolean isEmojiShow, boolean isKeyboardShow, int height) {
                showOrHideTipView(!isKeyboardShow);
            }
        });

        mPhoneView = mViewHolder.v(R.id.phone_view);
        mPhoneView.setInputTipTextList(RUtils.split(Hawk.get(KEY_LOGIN_PHONE, "")));

        mPasswordView = mViewHolder.v(R.id.password_view);
        mLoginView = mViewHolder.v(R.id.login_view);

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
                startIView(new SetPasswordUIView(SetPasswordUIView.TYPE_FORGET_PW));
            }
        });
        //手机注册
        mViewHolder.v(R.id.register_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(new RegisterUIView());
            }
        });
        //第三方登录
        mViewHolder.v(R.id.other_login_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceIView(new WelcomeUIView(), false);
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
}

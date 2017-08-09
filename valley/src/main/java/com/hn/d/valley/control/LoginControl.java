package com.hn.d.valley.control;

import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.RApplication;
import com.angcyo.uiview.Root;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.T_;
import com.angcyo.umeng.UM;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.constant.Action;
import com.hn.d.valley.base.oss.OssHelper;
import com.hn.d.valley.base.receiver.JPushReceiver;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.realm.LoginBean;
import com.hn.d.valley.start.service.StartService;
import com.liulishuo.FDown;
import com.liulishuo.FDownListener;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

import static com.hn.d.valley.control.AutoLoginControl.AUTO_LOGIN;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/05/18 18:07
 * 修改人员：Robi
 * 修改时间：2017/05/18 18:07
 * 修改备注：
 * Version: 1.0.0
 */
public class LoginControl {


    static final int TYPE_QQ = 1;
    static final int TYPE_WX = 2;

    boolean isCancel = false;
    OnLoginListener mOnLoginListener;
    /**
     * 第三方类型【1-qq,2-微信】【第三方登录必填】
     */
    int loginType = TYPE_QQ;
    private UMAuthListener mAuthListener;
    private WeakReference<Activity> mActivity;
    private UMAuthListener mInfoListener;

    /**
     * 是否是首次注册恐龙谷, 如果是, 需要显示推荐信息
     */
    private boolean isFirstRegister = false;

    private LoginControl() {
        mAuthListener = new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                if (mOnLoginListener != null) {
                    mOnLoginListener.onLoginStart();
                }

                RUtils.saveToSDCard(AUTO_LOGIN, "mAuthListener:onStart" + share_media);
            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                String openid = map.get("openid");
                if (isCancel) {
                    if (mOnLoginListener != null) {
                        mOnLoginListener.onLoginCancel();
                    }
                    return;
                }
                RUtils.saveToSDCard(AUTO_LOGIN, "mAuthListener:onComplete " + openid);
                login(openid, "", "", "");
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                T_.error(throwable.getMessage());
                if (mOnLoginListener != null) {
                    mOnLoginListener.onLoginError(throwable);
                }
                RUtils.saveToSDCard(AUTO_LOGIN, "mAuthListener:onError " + i + " " + throwable);
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                if (mOnLoginListener != null) {
                    mOnLoginListener.onLoginCancel();
                }
            }
        };

        mInfoListener = new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, final Map<String, String> map) {
                if (isCancel) {
                    if (mOnLoginListener != null) {
                        mOnLoginListener.onLoginCancel();
                    }
                    return;
                }
                //性别【1-男，2-女，0-保密】【第三方登录必填】
                String gender = map.get("gender");
                String sex = "0";
                try {
                    sex = String.valueOf(Integer.parseInt(gender));
                } catch (Exception e) {
                    if ("男".equalsIgnoreCase(gender)) {
                        sex = "1";
                    } else if ("女".equalsIgnoreCase(gender)) {
                        sex = "2";
                    } else {
                        sex = "0";
                    }
                }

                if (loginType == TYPE_QQ) {
                    Action.qq_register();
                } else {
                    Action.wechat_register();
                }

                final String finalSex = sex;
                FDown.build(map.get("profile_image_url"))
                        .setFullPath(Root.getAppInternalFolder("image") + "/" + Root.createFileName(".jpg"))
                        .download(new FDownListener() {
                            @Override
                            public void onCompleted(BaseDownloadTask task) {
                                super.onCompleted(task);
                                OssHelper.uploadAvatorImg(task.getPath())
                                        .subscribe(new BaseSingleSubscriber<String>() {
                                            @Override
                                            public void onSucceed(String s) {
                                                login(map.get("openid"), map.get("name"), OssHelper.getAvatorUrl(s), finalSex);
                                            }

                                            @Override
                                            public void onError(int code, String msg) {

                                            }

                                            @Override
                                            public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                                                super.onEnd(isError, isNoNetwork, e);
                                                if (isError) {
                                                    if (mOnLoginListener != null) {
                                                        mOnLoginListener.onLoginError(e);
                                                    }
                                                }
                                            }
                                        });
                            }

                            @Override
                            public void onError(BaseDownloadTask task, Throwable e) {
                                super.onError(task, e);
                                if (mOnLoginListener != null) {
                                    mOnLoginListener.onLoginError(e);
                                }
                            }
                        });
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                if (isCancel) {
                    if (mOnLoginListener != null) {
                        mOnLoginListener.onLoginCancel();
                    }
                    return;
                }
                if (mOnLoginListener != null) {
                    mOnLoginListener.onLoginError(throwable);
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                if (mOnLoginListener != null) {
                    mOnLoginListener.onLoginCancel();
                }
            }
        };
    }

    public static LoginControl instance() {
        return Holder.instance;
    }

    public void login(String open_id, String open_nick,
                      String open_avatar, String open_sex) {

        Map<String, String> map = new HashMap<>();
        //第三方登录
        map.put("open_id", open_id);
        map.put("open_type", String.valueOf(loginType));
        map.put("open_nick", open_nick);
        map.put("open_avatar", open_avatar);
        map.put("open_sex", open_sex);

        AutoLoginControl.saveAutoLoginBean(loginType, open_id, "", "");

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

        RRetrofit.create(StartService.class)
                .userLogin2(Param.map(map))
                .compose(Rx.transformer(LoginBean.class))
                .subscribe(new BaseSingleSubscriber<LoginBean>() {
                    @Override
                    public void onSucceed(LoginBean bean) {
                        if (isCancel) {
                            if (mOnLoginListener != null) {
                                mOnLoginListener.onLoginCancel();
                            }
                            return;
                        }

                        //第一次登录钱包开户
//                        if (LoginControl.instance().isFirstRegister()) {
//                            WalletHelper.getInstance().openAccount(bean.getUid());
//                        }

                        if (mOnLoginListener != null) {
                            mOnLoginListener.onLoginSuccess(bean);
                        }
                    }

                    @Override
                    public void onError(int code, String msg) {
                        RUtils.saveToSDCard(AUTO_LOGIN, "loginControl:262:onError " + code + " " + msg);
                        // 用户账号被封
                        if (code == 1067) {

                        } else if (code == 1068) {

                        } else {
                            super.onError(code, msg);
                        }
                    }

                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        RUtils.saveToSDCard(AUTO_LOGIN, "loginControl:276:onEnd " + isError + " e:" + e);

                        if (isError) {
                            if (isCancel) {
                                if (mOnLoginListener != null) {
                                    mOnLoginListener.onLoginCancel();
                                }
                                return;
                            }
                            if (e.getCode() == 1068) {
                                isFirstRegister = true;
                                //还未使用第三方帐号登录
                                //需要先注册
                                if (loginType == TYPE_QQ) {
                                    getQQInfo();
                                } else if (loginType == TYPE_WX) {
                                    getWxInfo();
                                }
                            } else {
                                if (mOnLoginListener != null) {
                                    mOnLoginListener.onLoginError(new RException(e.getCode(), e.getMsg(), "no more"));
                                }
                            }
                        }
                    }
                });
    }

    public void loginQQ(Activity activity, OnLoginListener loginListener) {
        loginType = TYPE_QQ;
        isCancel = false;
        mOnLoginListener = loginListener;
        mActivity = new WeakReference<>(activity);
        UM.authVerify(activity, SHARE_MEDIA.QQ, mAuthListener);
    }

    public void loginWX(Activity activity, OnLoginListener loginListener) {
        loginType = TYPE_WX;
        isCancel = false;
        mOnLoginListener = loginListener;
        mActivity = new WeakReference<>(activity);
        UM.authVerify(activity, SHARE_MEDIA.WEIXIN, mAuthListener);
    }

    void getQQInfo() {
        if (mActivity == null) {
            return;
        }
        Activity activity = mActivity.get();
        if (activity == null) {
            return;
        }
        UM.getPlatformInfo(activity, SHARE_MEDIA.QQ, mInfoListener);
    }

    void getWxInfo() {
        if (mActivity == null) {
            return;
        }
        Activity activity = mActivity.get();
        if (activity == null) {
            return;
        }
        UM.getPlatformInfo(activity, SHARE_MEDIA.WEIXIN, mInfoListener);
    }

    public boolean isFirstRegister() {
        return isFirstRegister;
    }

    public void setFirstRegister(boolean firstRegister) {
        isFirstRegister = firstRegister;
    }

    /**
     * 取消登录
     */
    public void setCancel(boolean cancel) {
        isCancel = cancel;
    }

    public interface OnLoginListener {
        void onLoginStart();

        void onLoginSuccess(LoginBean bean);

        void onLoginError(Throwable exception);

        void onLoginCancel();
    }

    private static class Holder {
        static LoginControl instance = new LoginControl();
    }
}

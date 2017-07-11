package com.hn.d.valley.start;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.dialog.UILoading;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.RUtils;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.bean.realm.LoginBean;
import com.hn.d.valley.control.LoginControl;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

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
        }

        @Override
        public void onLoginCancel() {
            UILoading.hide();
        }
    };

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
        mViewHolder.v(R.id.login_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceIView(new LoginUIView2(), false);
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

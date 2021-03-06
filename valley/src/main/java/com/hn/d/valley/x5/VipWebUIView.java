package com.hn.d.valley.x5;

import com.angcyo.library.utils.L;
import com.hn.d.valley.cache.UserCache;
import com.tencent.smtt.sdk.WebView;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：会员中心界面
 * 创建人员：Robi
 * 创建时间：2017/04/20 17:43
 * 修改人员：Robi
 * 修改时间：2017/04/20 17:43
 * 修改备注：
 * Version: 1.0.0
 */
public class VipWebUIView extends X5WebUIView {

    boolean isLogin = false;
    private String mToken;

    Runnable loginRunnable = new Runnable() {
        @Override
        public void run() {
            login(mWebView);
        }
    };

    public VipWebUIView() {
        super("http://wap.klgwl.com/user/rank&to=" + UserCache.getUserAccount());
    }

    private static String createMethodParams(String... params) {
        StringBuilder builder = new StringBuilder("(");
        for (String p : params) {
            builder.append("'");
            builder.append(p);
            builder.append("'");
            builder.append(",");
        }
        return builder.substring(0, Math.max(0, builder.length() - 1)) + ")";
    }

    @Override
    protected void initWebView() {
        super.initWebView();
    }

//    @Override
//    protected void onLoadUrl() {
//        add(RRetrofit.create(UserService.class)
//                .getToken(Param.buildMap())
//                .compose(Rx.transformer(String.class))
//                .subscribe(new BaseSingleSubscriber<String>() {
//                    @Override
//                    public void onSucceed(String bean) {
//                        super.onSucceed(bean);
//                        mToken = bean;
//
//                        VipWebUIView.super.onLoadUrl();
//                        mWebView.addJavascriptInterface(new AndroidJs() {
//
//                            @JavascriptInterface
//                            public void onAndroid() {
//                                L.e("js call: onAndroid([])-> ");
//                                T_.show("onAndroid");
//                            }
//
//                            @JavascriptInterface
//                            public void onTest() {
//                                L.e("js call: onTest([])-> ");
//                                //T_.show("onTest");
//                                if (BuildConfig.SHOW_DEBUG) {
//                                    T_.ok("mWebView.reload...");
//                                }
//                                mWebView.reload();
//                            }
//
//                            @JavascriptInterface
//                            public void reload() {
//                                L.e("js call: reload([])-> ");
//                                //T_.show("onTest");
//                                //mWebView.reload();
//                                if (BuildConfig.SHOW_DEBUG) {
//                                    T_.ok("reload...");
//                                }
//                                removeCallbacks(loginRunnable);
//                                postDelayed(loginRunnable, 1000);
//                            }
//                        }, "android");
//                    }
//                }));
//    }

    @Override
    protected void onPageFinished(final WebView webView, String url) {
        super.onPageFinished(webView, url);
        //login(webView);
//        if (!isLogin) {
//            final String js = "javascript:app_login" + createMethodParams(UserCache.getUserAccount(), mToken, "onTest", "android");
//            L.e("call: run([])-> load Js : " + mToken + "\n" + js);
////            webView.loadUrl(js);
////
//            webView.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    L.e("call: run([])-> load Js :" + js);
//                    webView.loadUrl(js);
//                }
//            }, 1000);
//
//            isLogin = true;
//        }
    }

    private void login(WebView webView) {
        if (!isLogin) {
            final String js = "javascript:app_login" + createMethodParams(UserCache.getUserAccount(), mToken, "onTest", "android");
            L.e("call: run([])-> load Js : " + mToken + "\n" + js);
            webView.loadUrl(js);
            isLogin = true;
        }
    }

    @Override
    protected void onProgressChanged(final WebView webView, int progress) {
        super.onProgressChanged(webView, progress);
    }
}

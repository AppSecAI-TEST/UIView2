package com.hn.d.valley.x5;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.angcyo.uiview.widget.EmptyView;
import com.angcyo.uiview.widget.SimpleProgressBar;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：浏览网页的界面
 * 创建人员：Robi
 * 创建时间：2017/02/15 17:23
 * 修改人员：Robi
 * 修改时间：2017/02/15 17:23
 * 修改备注：
 * Version: 1.0.0
 */
public class X5WebUIView extends BaseContentUIView {

    String mTargetUrl;
    X5WebView mWebView;
    SimpleProgressBar mProgressBarView;
    EmptyView mEmptyView;

    public X5WebUIView(String targetUrl) {
        mTargetUrl = targetUrl;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setShowBackImageView(true)
                .setTitleString("")
                .setTitleBarBGColor(Color.TRANSPARENT)
                .setFloating(true);
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        //inflate(R.layout.view_x5_web);
        mEmptyView = new EmptyView(mActivity);
        int offset = mActivity.getResources().getDimensionPixelOffset(R.dimen.base_xhdpi);
        mEmptyView.setPadding(offset, offset, offset, offset);
        baseContentLayout.addView(mEmptyView, new ViewGroup.LayoutParams(-1, -1));
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        inflate(R.layout.view_x5_web);
        //mEmptyView = mViewHolder.v(R.id.empty_view);
        mWebView = mViewHolder.v(R.id.web_view);
        mProgressBarView = mViewHolder.v(R.id.progress_bar_view);
        RefreshLayout refreshLayout = mViewHolder.v(R.id.refresh_layout);
        refreshLayout.setRefreshDirection(RefreshLayout.TOP);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                webView.getSettings().setDefaultTextEncodingName("utf-8");
                webView.loadUrl(url);
                getUITitleBarContainer().evaluateBackgroundColorSelf(webView.getScrollY());
                return true;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int progress) {
                super.onProgressChanged(webView, progress);
                mProgressBarView.setProgress(progress);
                if (progress > 80) {
                    mEmptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedTitle(WebView webView, String title) {
                super.onReceivedTitle(webView, title);
                setTitleString(title);
            }
        });
        mWebView.loadUrl(mTargetUrl);

        mWebView.setOnScrollChangedCallback(new X5WebView.OnScrollChangedCallback() {
            @Override
            public void onScroll(int left, int top, int dx, int dy) {
                getUITitleBarContainer().evaluateBackgroundColorSelf(top);
            }
        });
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
            mActivity.getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        }
    }

    @Override
    public boolean onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
            return false;
        }
        return true;
    }

}

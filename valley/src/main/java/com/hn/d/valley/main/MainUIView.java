package com.hn.d.valley.main;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.container.UILayoutImpl;
import com.angcyo.uiview.github.tablayout.CommonTabLayout;
import com.angcyo.uiview.github.tablayout.TabEntity;
import com.angcyo.uiview.github.tablayout.listener.CustomTabEntity;
import com.angcyo.uiview.github.tablayout.listener.OnTabSelectListener;
import com.angcyo.uiview.model.TitleBarPattern;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.T_;
import com.hn.d.valley.main.found.FoundUIView;
import com.hn.d.valley.main.home.HomeUIView;
import com.hn.d.valley.main.me.MeUIView;
import com.hn.d.valley.main.message.MessageUIView;
import com.hn.d.valley.main.status.PostStatusUIDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：主页面
 * 创建人员：Robi
 * 创建时间：2016/12/15 15:51
 * 修改人员：Robi
 * 修改时间：2016/12/15 15:51
 * 修改备注：
 * Version: 1.0.0
 */
public class MainUIView extends BaseUIView {

    protected long startAnimTime = DEFAULT_ANIM_TIME;
    @BindView(R.id.bottom_nav_layout)
    CommonTabLayout mBottomNavLayout;
    @BindView(R.id.main_layout)
    UILayoutImpl mMainUILayout;
    /*首页*/
    HomeUIView mHomeUIView;
    /*发现*/
    FoundUIView mFoundUIView;
    /*消息*/
    MessageUIView mMessageUIView;
    /*我的*/
    MeUIView mMeUIView;
    long onBackTime = 0;
    private int lastPosition = 0;

    public MainUIView() {
    }

    public MainUIView(long startAnimTime) {
        this.startAnimTime = startAnimTime;
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_main);
    }

    @NonNull
    @Override
    protected UIBaseView.LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }

    @Override
    public void loadContentView(View rootView) {
        super.loadContentView(rootView);
        mMainUILayout.lock();
        initILayout();
        initTabLayout();
    }

    private void initILayout() {

    }

    private void initTabLayout() {
        ArrayList<CustomTabEntity> tabs = new ArrayList<>();
        tabs.add(new TabEntity(true, mActivity.getString(R.string.nav_home_text), R.drawable.home_s, R.drawable.home_n));
        tabs.add(new TabEntity(true, mActivity.getString(R.string.nav_found_text), R.drawable.found_s, R.drawable.found_n));
        tabs.add(new TabEntity(true, "", -1, -1));
        tabs.add(new TabEntity(true, mActivity.getString(R.string.nav_message_text), R.drawable.message_s, R.drawable.message_n));
        tabs.add(new TabEntity(true, mActivity.getString(R.string.nav_me_text), R.drawable.me_s, R.drawable.me_n));

        mBottomNavLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                boolean isRightToLeft = position < lastPosition;

                if (position == 0) {
                    //首页
                    if (mHomeUIView == null) {
                        mHomeUIView = new HomeUIView();
                        mHomeUIView.setIsRightJumpLeft(isRightToLeft);
                        mMainUILayout.startIView(mHomeUIView);
                    } else {
                        mHomeUIView.setIsRightJumpLeft(isRightToLeft);
                        mMainUILayout.showIView(mHomeUIView);
                    }
                } else if (position == 1) {
                    //发现
                    if (mFoundUIView == null) {
                        mFoundUIView = new FoundUIView();
                        mFoundUIView.setIsRightJumpLeft(isRightToLeft);
                        mMainUILayout.startIView(mFoundUIView);
                    } else {
                        mFoundUIView.setIsRightJumpLeft(isRightToLeft);
                        mMainUILayout.showIView(mFoundUIView);
                    }
                } else if (position == 3) {
                    //消息
                    if (mMessageUIView == null) {
                        mMessageUIView = new MessageUIView();
                        mMessageUIView.setIsRightJumpLeft(isRightToLeft);
                        mMainUILayout.startIView(mMessageUIView);
                    } else {
                        mMessageUIView.setIsRightJumpLeft(isRightToLeft);
                        mMainUILayout.showIView(mMessageUIView);
                    }
                } else if (position == 4) {
                    //我的
                    if (mMeUIView == null) {
                        mMeUIView = new MeUIView();
                        mMeUIView.setIsRightJumpLeft(isRightToLeft);
                        mMainUILayout.startIView(mMeUIView);
                    } else {
                        mMeUIView.setIsRightJumpLeft(isRightToLeft);
                        mMainUILayout.showIView(mMeUIView);
                    }
                }
                lastPosition = position;
            }

            @Override
            public void onTabReselect(int position) {
                L.e("");
            }
        });

        mBottomNavLayout.setTabData(tabs);
        mBottomNavLayout.showMsg(0, 0);
        mBottomNavLayout.showMsg(1, 10);
        mBottomNavLayout.showMsg(3, 999);
        mBottomNavLayout.showMsg(4, 99);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return null;
    }

    @Override
    public Animation loadLayoutAnimation() {
        return null;
    }

    @Override
    public Animation loadStartAnimation() {
        Animation animation = super.loadStartAnimation();
        animation.setDuration(startAnimTime);
        return animation;
    }

    @OnClick(R.id.nav_center_view)
    public void onPostStatusClick() {
        startIView(new PostStatusUIDialog());
    }

    @Override
    public boolean onBackPressed() {
        long timeMillis = System.currentTimeMillis();
        if (timeMillis - onBackTime < 1000) {
            return true;
        }
        onBackTime = timeMillis;
        T_.show("别按啦，恐龙君舍不得离开你！");
        return false;
    }
}

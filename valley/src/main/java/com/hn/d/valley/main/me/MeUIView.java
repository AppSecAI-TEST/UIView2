package com.hn.d.valley.main.me;

import android.animation.ArgbEvaluator;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.library.adapter.SingleFrescoImageAdapter;
import com.angcyo.library.facebook.DraweeViewUtil;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.angcyo.uiview.widget.SingleTextIndicator;
import com.angcyo.uiview.widget.TitleBarLayout;
import com.angcyo.uiview.widget.viewpager.DepthPageTransformer;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.T_;
import com.hn.d.valley.bean.LoginBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.UserControl;

import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/17 10:15
 * 修改人员：Robi
 * 修改时间：2016/12/17 10:15
 * 修改备注：
 * Version: 1.0.0
 */
public class MeUIView extends BaseUIView {
    @BindView(R.id.title_view)
    TextView mTitleView;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.single_text_indicator_view)
    SingleTextIndicator mSingleTextIndicatorView;
    @BindView(R.id.title_bar_layout)
    TitleBarLayout mTitleBarLayout;
    @BindView(R.id.user_ico_view)
    SimpleDraweeView mUserIcoView;

    @BindView(R.id.user_id_view)
    TextView mUserIdView;
    @BindView(R.id.follow_num_view)
    TextView mFollowNumView;
    @BindView(R.id.follower_num_view)
    TextView mFollowerNumView;

    @BindView(R.id.scroll_root_layout)
    NestedScrollView mScrollRootLayout;


    @BindView(R.id.my_status_layout)
    ItemInfoLayout mMyStatusLayout;
    @BindView(R.id.my_favor_layout)
    ItemInfoLayout mMyFavorLayout;
    @BindView(R.id.level_layout)
    ItemInfoLayout mLevelLayout;
    @BindView(R.id.coin_layout)
    ItemInfoLayout mCoinLayout;
    @BindView(R.id.person_auth_layout)
    ItemInfoLayout mPersonAuthLayout;
    @BindView(R.id.setting_layout)
    ItemInfoLayout mSettingLayout;


    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_main_me_layout);
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
    public int getDefaultBackgroundColor() {
        return mActivity.getResources().getColor(R.color.line_color);
    }

    @Override
    protected void initContentLayout() {
        super.initContentLayout();
        initScrollLayout();
        initViewPager();
        mSingleTextIndicatorView.setupViewPager(mViewPager);
        UserCache.instance().getLoginBeanObservable().subscribe(new Action1<LoginBean>() {
            @Override
            public void call(LoginBean loginBean) {
                //mFollowerNumView.setText(loginBean.get);
                mTitleView.setText(loginBean.getUsername());
                UserControl.setUserId(mUserIdView, loginBean.getUid());
                DraweeViewUtil.setDraweeViewHttp(mUserIcoView, loginBean.getAvatar());
                mPersonAuthLayout.setItemDarkText("未认证");
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                T_.show("用户信息异常!");
            }
        });
    }

    private void initScrollLayout() {
        mScrollRootLayout.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                mTitleBarLayout.setBackgroundColor((Integer) new ArgbEvaluator()
                        .evaluate(Math.min(1, 6 * scrollY * 0.1f / mTitleBarLayout.getMeasuredHeight()),
                                Color.TRANSPARENT, mActivity.getResources().getColor(R.color.colorPrimary)));
            }
        });
    }

    private void initViewPager() {
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        mViewPager.setAdapter(new SingleFrescoImageAdapter(R.drawable.login_pic) {
            @Override
            protected String getImageUrl(int position) {
                return UserCache.instance().getAvatar();
            }

            @Override
            public int getCount() {
                return 10;
            }
        });
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }

    /**
     * 编辑资料
     */
    @OnClick(R.id.editor_view)
    public void onEditorClick() {
        T_.show("编辑");
    }

    @OnClick({R.id.qr_code_view, R.id.follow_item_layout, R.id.follower_item_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.qr_code_view:
                break;
            case R.id.follow_item_layout:
                break;
            case R.id.follower_item_layout:
                break;
        }
    }

    @OnClick({R.id.my_status_layout, R.id.my_favor_layout, R.id.level_layout,
            R.id.coin_layout, R.id.person_auth_layout, R.id.setting_layout})
    public void onItemInfoClick(View view) {
        switch (view.getId()) {
            case R.id.my_status_layout://我的动态
                mMyStatusLayout.setItemDarkText("" + new Random().nextInt(100));
                break;
            case R.id.my_favor_layout://我的收藏
                mMyFavorLayout.setItemDarkText("" + new Random().nextInt(100));
                break;
            case R.id.level_layout://等级
                mLevelLayout.setItemDarkText("V" + new Random().nextInt(100));
                break;
            case R.id.coin_layout://龙币
                mCoinLayout.setItemDarkText("" + new Random().nextInt(100));
                mCoinLayout.setDarkDrawableRes(R.drawable.gift);
                break;
            case R.id.person_auth_layout://名人认证
                mPersonAuthLayout.setItemDarkText("");
                break;
            case R.id.setting_layout://设置
                mSettingLayout.setLeftDrawableRes(R.drawable.address_book_n);
                break;
        }
    }
}

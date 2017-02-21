package com.hn.d.valley.main.me;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.library.facebook.DraweeViewUtil;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RSubscriber;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.angcyo.uiview.widget.viewpager.TextIndicator;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.bean.realm.LoginBean;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.UserControl;
import com.hn.d.valley.main.me.setting.EditInfoUIView;
import com.hn.d.valley.main.me.setting.SettingUIView2;
import com.hn.d.valley.sub.other.FansRecyclerUIView;
import com.hn.d.valley.sub.other.FollowersRecyclerUIView;
import com.hn.d.valley.utils.PhotoPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action0;
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
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.single_text_indicator_view)
    TextIndicator mTextIndicatorView;
    @BindView(R.id.user_ico_view)
    SimpleDraweeView mUserIcoView;

    @BindView(R.id.user_id_view)
    TextView mUserIdView;

    @BindView(R.id.scroll_root_layout)
    NestedScrollView mScrollRootLayout;

    @BindView(R.id.person_auth_layout)
    ItemInfoLayout mPersonAuthLayout;
    @BindView(R.id.setting_layout)
    ItemInfoLayout mSettingLayout;
    @BindView(R.id.view_pager_placeholder_view)
    ImageView mViewPagerPlaceholderView;
    private ArrayList<String> mPhotos = new ArrayList<>();

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_main_me_layout);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setFloating(true).setTitleString("")
                .setTitleBarBGColor(Color.TRANSPARENT);
//                .addRightItem(TitleBarPattern.TitleBarItem.build(R.drawable.editor, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        /* 编辑资料 */
//                        //T_.show("编辑");
//                        mOtherILayout.startIView(new EditInfoUIView());
//                    }
//                }));
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
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        initScrollLayout();
        UserCache.instance().getLoginBeanObservable().subscribe(new Action1<LoginBean>() {
            @Override
            public void call(LoginBean loginBean) {
                //mFollowerNumView.setText(loginBean.get);
                setTitleString(loginBean.getUsername());
                UserControl.setUserId(mUserIdView, loginBean.getUid());
                DraweeViewUtil.setDraweeViewHttp(mUserIcoView, loginBean.getAvatar());
                mPersonAuthLayout.setItemDarkText(mActivity.getResources().getString(R.string.not_auth));
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                T_.show("用户信息异常!");
            }
        });
//        initMeUIView();
        mViewPagerPlaceholderView.setVisibility(View.VISIBLE);
    }

    private void initScrollLayout() {
        mScrollRootLayout.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                getUITitleBarContainer().evaluateBackgroundColor(scrollY);
            }
        });
    }

    private void initMeUIView() {
        final UserInfoBean userInfoBean = UserCache.instance().getUserInfoBean();
        setTitleString(userInfoBean.getUsername());
        UserControl.setUserId(mUserIdView, userInfoBean.getUid());
        DraweeViewUtil.setDraweeViewHttp(mUserIcoView, userInfoBean.getAvatar());
        if ("1".equalsIgnoreCase(userInfoBean.getIs_auth())) {
            mPersonAuthLayout.setItemDarkText(userInfoBean.getAuth_desc());
        } else {
            mPersonAuthLayout.setItemDarkText(mActivity.getResources().getString(R.string.not_auth));
        }

        mViewPagerPlaceholderView.setVisibility(View.GONE);
        mPhotos.clear();
        if (userInfoBean != null) {
            List<String> stringList = RUtils.split(userInfoBean.getPhotos());
            if (stringList.isEmpty()) {
                mPhotos.add(UserCache.instance().getAvatar());
            } else {
                mPhotos.addAll(stringList);
            }
        } else {
            mPhotos.add(UserCache.instance().getAvatar());
        }
        PhotoPager.init(mOtherILayout, mTextIndicatorView, mViewPager, mPhotos);

        //
        mViewHolder.fillView(UserInfoBean.class, userInfoBean, false, true);
        mPersonAuthLayout.setItemDarkText(UserControl.getAuthString(userInfoBean.getAuth_type()));
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }

    @Override
    public void onViewCreate() {
        super.onViewCreate();
    }

    @Override
    public void onViewShow(Bundle bundle) {
        if (System.currentTimeMillis() - mLastShowTime > 30 * 1000) {
            //30秒后, 重新拉取新的信息
            UserCache.instance()
                    .fetchUserInfo()
                    .subscribe(new RSubscriber<UserInfoBean>() {
                        @Override
                        public void onSucceed(UserInfoBean userInfoBean) {
                            if (userInfoBean != null) {
                                UserCache.instance().setUserInfoBean(userInfoBean);
                                initMeUIView();
                            }
                        }
                    });
        }
        super.onViewShow(bundle);
    }

    @OnClick({R.id.qr_code_view, R.id.follow_item_layout, R.id.follower_item_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.qr_code_view://二维码
                break;
            case R.id.follow_item_layout://关注
                mOtherILayout.startIView(new FollowersRecyclerUIView());
                break;
            case R.id.follower_item_layout://粉丝
                mOtherILayout.startIView(new FansRecyclerUIView());
                break;
        }
    }

    @OnClick({R.id.my_status_layout, R.id.my_favor_layout, R.id.level_layout,
            R.id.coin_layout, R.id.person_auth_layout, R.id.setting_layout})
    public void onItemInfoClick(View view) {
        switch (view.getId()) {
            case R.id.my_status_layout://我的动态
                break;
            case R.id.my_favor_layout://我的收藏
                break;
            case R.id.level_layout://等级
                break;
            case R.id.coin_layout://龙币
                break;
            case R.id.person_auth_layout://名人认证
                break;
            case R.id.setting_layout://设置
//                mOtherILayout.startIView(new SettingUIView());
                mOtherILayout.startIView(new SettingUIView2());
                break;
        }
    }

    @OnClick(R.id.user_ico_view)
    public void onUserIcoClick() {
        mOtherILayout.startIView(new EditInfoUIView(mPhotos, new Action0() {
            @Override
            public void call() {
                initMeUIView();
            }
        }));
    }
}

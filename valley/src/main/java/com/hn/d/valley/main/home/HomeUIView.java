package com.hn.d.valley.main.home;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.angcyo.uiview.container.UILayoutImpl;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.github.tablayout.CommonTabLayout;
import com.angcyo.uiview.github.tablayout.TabEntity;
import com.angcyo.uiview.github.tablayout.listener.CustomTabEntity;
import com.angcyo.uiview.github.tablayout.listener.OnTabSelectListener;
import com.angcyo.uiview.model.TitleBarPattern;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.bean.realm.Tag;
import com.hn.d.valley.main.home.circle.CircleUIView;
import com.hn.d.valley.main.home.nearby.NearbyUIView;
import com.hn.d.valley.main.home.recommend.RecommendUIView;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：首页
 * 创建人员：Robi
 * 创建时间：2016/12/16 9:44
 * 修改人员：Robi
 * 修改时间：2016/12/16 9:44
 * 修改备注：
 * Version: 1.0.0
 */
public class HomeUIView extends BaseUIView {

    @BindView(R.id.home_nav_layout)
    CommonTabLayout mHomeNavLayout;
    //    @BindView(R.id.view_pager)
//    UIViewPager mViewPager;
    @BindView(R.id.home_layout)
    UILayoutImpl mHomeLayout;
    private ViewPager.SimpleOnPageChangeListener mPageChangeListener;
    private RecommendUIView mRecommendUIView;
    private Tag currentTag;
    private NearbyUIView mNearbyUIView;
    private CircleUIView mCircleUIView;
    private int lastPosition = -1;

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_main_home_layout);
    }

    @Override
    public Animation loadLayoutAnimation() {
        return null;
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        initViewPager();
        initTabLayout();

        mHomeLayout.setEnableSwipeBack(false);
        /**默认显示第二页*/
//        mViewPager.setCurrentItem(1);
        mHomeNavLayout.setCurrentTab(1, false);
        if (mRecommendUIView == null) {
            mRecommendUIView = new RecommendUIView();
            mRecommendUIView.bindOtherILayout(mOtherILayout);
            mHomeLayout.startIView(mRecommendUIView, new UIParam(false));
        } else {
            mHomeLayout.showIView(mRecommendUIView, false);
        }
    }

    private void initViewPager() {
//        mViewPager.setPageTransformer(true, new FadeInOutPageTransformer());
//        mViewPager.setOffscreenPageLimit(2);
//        mViewPager.setAdapter(new UIPagerAdapter() {
//            @Override
//            protected IView getIView(int position) {
//                if (position == 1) {
//                    if (mRecommendUIView == null) {
//                        mRecommendUIView = new RecommendUIView();
//                    }
//                    return mRecommendUIView.bindOtherILayout(mOtherILayout);
//                }
//                if (position == 2) {
//                    if (mNearbyUIView == null) {
//                        mNearbyUIView = new NearbyUIView();
//                    }
//                    return mNearbyUIView.bindOtherILayout(mOtherILayout);
//                }
//                if (mCircleUIView == null) {
//                    mCircleUIView = new CircleUIView();
//                }
//                return mCircleUIView.bindOtherILayout(mOtherILayout);
//            }
//
//            @Override
//            public int getCount() {
//                return 3;
//            }
//        });
//        mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                mHomeNavLayout.setCurrentTab(position, false);
//            }
//        };
//        mViewPager.addOnPageChangeListener(mPageChangeListener);
    }

    private void initTabLayout() {
        ArrayList<CustomTabEntity> tabs = new ArrayList<>();
        tabs.add(new TabEntity(mActivity.getString(R.string.home_circle), -1, -1, false, false, false));
        tabs.add(new TabEntity(mActivity.getString(R.string.home_recommend), -1, -1, false, false, false));
        tabs.add(new TabEntity(mActivity.getString(R.string.home_nearby), -1, -1, false, false, false));

        mHomeNavLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                changeViewPager(position);
            }

            @Override
            public void onTabReselect(int position) {
//                if (position == 1) {
//                    //推荐 , 弹出标签
//                    new TagFilterUIDialog(new Action1<Tag>() {
//                        @Override
//                        public void call(Tag tag) {
//                            currentTag = tag;
//                            mRecommendUIView.setFilterTag(tag);
//                            mRecommendUIView.loadData();
//                        }
//                    }, currentTag == null ? TagsControl.allTag : currentTag).showDialog(mOtherILayout);
//                }
            }
        });

        mHomeNavLayout.setTabData(tabs);
    }

    private void changeViewPager(int position) {
//        mViewPager.removeOnPageChangeListener(mPageChangeListener);
//        mViewPager.setCurrentItem(position);
//        mViewPager.addOnPageChangeListener(mPageChangeListener);

        boolean isRightToLeft = position < lastPosition;
        if (position == 0) {
            if (mCircleUIView == null) {
                mCircleUIView = new CircleUIView();
                mCircleUIView.bindOtherILayout(mOtherILayout);
                mCircleUIView.setIsRightJumpLeft(isRightToLeft);
                mHomeLayout.startIView(mCircleUIView);
            } else {
                mCircleUIView.setIsRightJumpLeft(isRightToLeft);
                mHomeLayout.showIView(mCircleUIView);
            }
        } else if (position == 1) {
            if (mRecommendUIView == null) {
                mRecommendUIView = new RecommendUIView();
                mRecommendUIView.bindOtherILayout(mOtherILayout);
                mRecommendUIView.setIsRightJumpLeft(isRightToLeft);
                mHomeLayout.startIView(mRecommendUIView);
            } else {
                mRecommendUIView.setIsRightJumpLeft(isRightToLeft);
                mHomeLayout.showIView(mRecommendUIView);
            }
        } else if (position == 2) {
            if (mNearbyUIView == null) {
                mNearbyUIView = new NearbyUIView();
                mNearbyUIView.bindOtherILayout(mOtherILayout);
                mNearbyUIView.setIsRightJumpLeft(isRightToLeft);
                mHomeLayout.startIView(mNearbyUIView);
            } else {
                mNearbyUIView.setIsRightJumpLeft(isRightToLeft);
                mHomeLayout.showIView(mNearbyUIView);
            }
        }
        lastPosition = position;
    }

    /**
     * 滚动置顶
     */
    public void scrollToTop() {
//        int currentItem = mViewPager.getCurrentItem();
        int currentItem = mHomeNavLayout.getCurrentTab();
        if (currentItem == 0 && mCircleUIView != null) {
            mCircleUIView.scrollToTop();
        } else if (currentItem == 1 && mRecommendUIView != null) {

            mRecommendUIView.scrollToTop();
        } else if (currentItem == 2 && mNearbyUIView != null) {
            mNearbyUIView.scrollToTop();
        }
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return null;
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }

}

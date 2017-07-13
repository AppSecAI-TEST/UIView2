package com.hn.d.valley.main.found.sub;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.github.tablayout.SlidingTabLayout;
import com.angcyo.uiview.github.tablayout.listener.OnTabSelectListener;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.skin.ISkin;
import com.angcyo.uiview.view.IView;
import com.angcyo.uiview.widget.viewpager.FadeInOutPageTransformer;
import com.angcyo.uiview.widget.viewpager.UIPagerAdapter;
import com.angcyo.uiview.widget.viewpager.UIViewPager;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.control.HotTagsControl;
import com.hn.d.valley.service.NewsService;

import java.util.List;

import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：热点资讯界面
 * 创建人员：Robi
 * 创建时间：2017/01/17 09:19
 * 修改人员：Robi
 * 修改时间：2017/01/17 09:19
 * 修改备注：
 * Version: 1.0.0
 */
public class HotInformationUIView extends BaseContentUIView {

    List<String> mAllTypeList, mMyTypeList;
    SlidingTabLayout mSlidTabLayout;
    UIViewPager mViewPager;
    private ViewPager.SimpleOnPageChangeListener mPageChangeListener;

    @Override
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_hot_info);
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.hot_information)).setShowBackImageView(true);
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mSlidTabLayout = v(R.id.slid_tab_layout);
        mViewPager = v(R.id.view_pager);

        click(R.id.add_tag_view, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(new HotTagsManageUIView(mAllTypeList).setTagsAction1(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> list) {
                        mMyTypeList = list;
                        resetViewPagerAdapter();
                        mSlidTabLayout.setViewPager(mViewPager);
                        //mViewPager.setCurrentItem(0);
                        mSlidTabLayout.onPageSelected(0);
                    }
                }));
            }
        });

        initViewPager();
        initTabLayout();
        updateSkin();
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        showLoadView();
        add(RRetrofit
                .create(NewsService.class)
                .classifylist(Param.buildInfoMap())
                .compose(Rx.transformerList(String.class))
                .subscribe(new BaseSingleSubscriber<List<String>>() {

                    @Override
                    public void onSucceed(List<String> bean) {
                        if (bean == null || bean.isEmpty()) {
                            showEmptyLayout();
                        } else {
                            mAllTypeList = bean;
                            mMyTypeList = HotTagsControl.INSTANCE.getMyTags(mAllTypeList);
                            showContentLayout();
                        }
                    }

                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        hideLoadView();
                        if (isError) {
                            showNonetLayout(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onViewShowFirst(null);
                                }
                            });
                        }
                    }

                })
        );
    }

    private void initTabLayout() {
        mSlidTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                changeViewPager(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

        mSlidTabLayout.setViewPager(mViewPager);
    }

    private void changeViewPager(int position) {
        mViewPager.removeOnPageChangeListener(mPageChangeListener);
        mViewPager.setCurrentItem(position);
        mViewPager.addOnPageChangeListener(mPageChangeListener);
    }

    private void initViewPager() {
        mViewPager.setPageTransformer(true, new FadeInOutPageTransformer());
        mViewPager.setOffscreenPageLimit(1);
        mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mSlidTabLayout.setCurrentTab(position, false);
            }
        };
        mViewPager.addOnPageChangeListener(mPageChangeListener);

        resetViewPagerAdapter();
    }

    private void resetViewPagerAdapter() {
        mViewPager.setAdapter(new UIPagerAdapter() {
            @Override
            protected IView getIView(int position) {
                final HotInfoListUIView uiView = new HotInfoListUIView(mMyTypeList.get(position));
                uiView.bindParentILayout(mILayout);
                return uiView;
            }

            @Override
            public int getCount() {
                return mMyTypeList.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mMyTypeList.get(position);
            }
        });
    }

    @Override
    public void onSkinChanged(ISkin skin) {
        super.onSkinChanged(skin);
        mSlidTabLayout.setTextSelectColor(skin.getThemeSubColor());
        mSlidTabLayout.setIndicatorHeight(getDimensionPixelOffset(R.dimen.base_line));
        mSlidTabLayout.setIndicatorColor(skin.getThemeSubColor());
        mSlidTabLayout.setIndicatorStyle(SlidingTabLayout.STYLE_NORMAL);
        mSlidTabLayout.setIndicatorWidthEqualTitle(true);
    }
}

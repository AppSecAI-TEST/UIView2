package com.hn.d.valley.main.found.sub;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.uiview.github.tablayout.SlidingTabLayout;
import com.angcyo.uiview.github.tablayout.listener.OnTabSelectListener;
import com.angcyo.uiview.model.TitleBarPattern;
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
import com.hn.d.valley.service.NewsService;

import java.util.List;

import butterknife.BindView;

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

    List<String> mTypeList;
    @BindView(R.id.slid_tab_layout)
    SlidingTabLayout mSlidTabLayout;
    @BindView(R.id.view_pager)
    UIViewPager mViewPager;
    private ViewPager.SimpleOnPageChangeListener mPageChangeListener;

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
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
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        showNonetLayout(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onViewShowFirst(null);
                            }
                        });
                    }

                    @Override
                    public void onSucceed(List<String> bean) {
                        if (bean == null || bean.isEmpty()) {
                            showEmptyLayout();
                        } else {
                            mTypeList = bean;
                            showContentLayout();
                        }
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                        hideLoadView();
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

        mViewPager.setAdapter(new UIPagerAdapter() {
            @Override
            protected IView getIView(int position) {
                final HotInfoListUIView uiView = new HotInfoListUIView(mTypeList.get(position));
                return uiView;
            }

            @Override
            public int getCount() {
                return mTypeList.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTypeList.get(position);
            }
        });
    }

    @Override
    public void onSkinChanged(ISkin skin) {
        super.onSkinChanged(skin);
        mSlidTabLayout.setTextSelectColor(skin.getThemeSubColor());
    }
}

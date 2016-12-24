package com.hn.d.valley.main.home;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.github.tablayout.CommonTabLayout;
import com.angcyo.uiview.github.tablayout.TabEntity;
import com.angcyo.uiview.github.tablayout.listener.CustomTabEntity;
import com.angcyo.uiview.github.tablayout.listener.OnTabSelectListener;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.view.IView;
import com.angcyo.uiview.widget.UIPagerAdapter;
import com.angcyo.uiview.widget.UIViewPager;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
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
    @BindView(R.id.view_pager)
    UIViewPager mViewPager;
    private ViewPager.SimpleOnPageChangeListener mPageChangeListener;

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_main_home_layout);
    }

    @Override
    protected void initContentLayout() {
        super.initContentLayout();
        initViewPager();
        initTabLayout();
    }

    private void initViewPager() {
        mViewPager.setAdapter(new UIPagerAdapter() {
            @Override
            protected IView getIView(int position) {
                if (position == 1) {
                    return new RecommendUIView();
                }
                if (position == 2) {
                    return new NearbyUIView();
                }
                return new CircleUIView();
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
        mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mHomeNavLayout.setCurrentTab(position, false);
            }
        };
        mViewPager.addOnPageChangeListener(mPageChangeListener);
    }

    private void initTabLayout() {
        ArrayList<CustomTabEntity> tabs = new ArrayList<>();
        tabs.add(new TabEntity("圈子", -1, -1, false, false, false));
        tabs.add(new TabEntity("推荐", -1, -1, true, false, false));
        tabs.add(new TabEntity("附近", -1, -1, false, false, false));

        mHomeNavLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                changeViewPager(position);
            }

            @Override
            public void onTabReselect(int position) {
                L.e("");
            }
        });

        mHomeNavLayout.setTabData(tabs);
    }

    private void changeViewPager(int position) {
        mViewPager.removeOnPageChangeListener(mPageChangeListener);
        mViewPager.setCurrentItem(position);
        mViewPager.addOnPageChangeListener(mPageChangeListener);
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

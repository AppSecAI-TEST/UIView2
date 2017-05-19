package com.hn.d.valley.sub;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.angcyo.uiview.github.tablayout.SlidingTabLayout;
import com.angcyo.uiview.github.tablayout.listener.OnTabSelectListener;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.skin.ISkin;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.view.IView;
import com.angcyo.uiview.widget.viewpager.FadeInOutPageTransformer;
import com.angcyo.uiview.widget.viewpager.UIPagerAdapter;
import com.angcyo.uiview.widget.viewpager.UIViewPager;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.home.circle.CircleUIView;
import com.hn.d.valley.main.home.recommend.LoadStatusCallback;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/03/23 08:57
 * 修改人员：Robi
 * 修改时间：2017/03/23 08:57
 * 修改备注：
 * Version: 1.0.0
 */
public class MyStatusUIView extends BaseContentUIView {

    private UIViewPager mViewPager;
    private SlidingTabLayout mSlidTabLayout;
    private ViewPager.SimpleOnPageChangeListener mPageChangeListener;
    private StatusType[] mStatusTypes = new StatusType[]{StatusType.ALL, StatusType.IMAGE, StatusType.VIDEO, StatusType.AUDIO};

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setShowBackImageView(true).setTitleString(mActivity, R.string.me_dynamic_state);
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_my_status);
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mViewPager = mViewHolder.v(R.id.view_pager);
        mSlidTabLayout = mViewHolder.v(R.id.tab_layout);

        initViewPager();
        initTabLayout();

        updateSkin();
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
        mViewPager.setOffscreenPageLimit(3);
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
                CircleUIView circleUIView = new CircleUIView(UserCache.getUserAccount());
                circleUIView.bindOtherILayout(mOtherILayout);
                circleUIView.setInSubUIView(true);
                circleUIView.setStatusType(mStatusTypes[position]);
                circleUIView.setLoadStatusCallback(new LoadStatusCallback() {
                    @Override
                    public void onLoadStart() {
                        showLoadView();
                    }

                    @Override
                    public void onLoadEnd() {
                        hideLoadView();
                    }
                });
                return circleUIView;
            }

            @Override
            public int getCount() {
                return mStatusTypes.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return getString(mStatusTypes[position].getRes());
            }
        });
    }

    @Override
    public void onSkinChanged(ISkin skin) {
        super.onSkinChanged(skin);
        if (mSlidTabLayout != null) {
            mSlidTabLayout.setTextSelectColor(SkinHelper.getSkin().getThemeSubColor());
            mSlidTabLayout.setIndicatorColor(SkinHelper.getSkin().getThemeSubColor());
        }
    }

    public enum StatusType {
        ALL(0, R.string.all),
        IMAGE(1, R.string.image_text),
        VIDEO(3, R.string.video),
        AUDIO(4, R.string.voice);

        int id, res;

        StatusType(int id, int res) {
            this.id = id;
            this.res = res;
        }

        public int getRes() {
            return res;
        }

        public int getId() {
            return id;
        }
    }

}

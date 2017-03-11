package com.hn.d.valley.main.home.recommend;

import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.uiview.github.tablayout.SlidingTabLayout;
import com.angcyo.uiview.github.tablayout.listener.OnTabSelectListener;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.view.IView;
import com.angcyo.uiview.widget.viewpager.FadeInOutPageTransformer;
import com.angcyo.uiview.widget.viewpager.UIPagerAdapter;
import com.angcyo.uiview.widget.viewpager.UIViewPager;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.BaseRecyclerUIView;
import com.hn.d.valley.bean.realm.Tag;
import com.hn.d.valley.control.TagsControl;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：首页 下面的 推荐
 * 创建人员：Robi
 * 创建时间：2016/12/16 11:18
 * 修改人员：Robi
 * 修改时间：2016/12/16 11:18
 * 修改备注：
 * Version: 1.0.0
 */
public class RecommendUIView2 extends BaseContentUIView {

    @BindView(R.id.slid_tab_layout)
    SlidingTabLayout mSlidTabLayout;
    @BindView(R.id.view_pager)
    UIViewPager mViewPager;
    ArrayList<Tag> mAllTags, mMyTags;

    BaseRecyclerUIView mLastUIView;
    private ViewPager.SimpleOnPageChangeListener mPageChangeListener;

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_home_recommend_layout2);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return null;
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mAllTags = new ArrayList<>();
        mMyTags = new ArrayList<>();
        TagsControl.getTags(new Action1<List<Tag>>() {
            @Override
            public void call(List<Tag> tags) {
                mAllTags.add(0, TagsControl.allTag);
                mAllTags.addAll(tags);

                TagsControl.initMyTags(mAllTags, mMyTags);

                initViewPager();
                initTabLayout();

                //标签管理
                mViewHolder.v(R.id.image_view).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new TagsManageUIView(mAllTags, new Action1<List<Tag>>() {
                            @Override
                            public void call(List<Tag> tags) {
                                //1:得到当前的item位置
                                int currentItem = mViewPager.getCurrentItem();
                                //得到当前标签的id
                                String id = mMyTags.get(currentItem).getId();

                                //2:判断当前标签的id是否被删除
                                int index = -1;
                                int size = tags.size();
                                for (int i = 0; i < size; i++) {
                                    String id1 = tags.get(i).getId();
                                    if (TextUtils.equals(id, id1)) {
                                        index = i;
                                        break;
                                    }
                                }
                                //如果没有找到找到了
                                if (index == -1) {
                                    if (size > currentItem) {
                                        index = currentItem;
                                    } else {
                                        index = size - 1;
                                    }
                                }

                                mMyTags.clear();
                                mMyTags.addAll(tags);

                                resetViewPagerAdapter();
//                                mViewPager.getAdapter().notifyDataSetChanged();
                                initTabLayout();
                                mViewPager.setCurrentItem(index);
                            }
                        }));
                    }
                });
            }
        });
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
        resetViewPagerAdapter();
        mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mSlidTabLayout.setCurrentTab(position, false);
            }
        };
        mViewPager.addOnPageChangeListener(mPageChangeListener);
    }

    private void resetViewPagerAdapter() {
        mViewPager.setAdapter(new UIPagerAdapter() {
            @Override
            protected IView getIView(int position) {
                final RecommendUIView uiView = new RecommendUIView();
                uiView.bindOtherILayout(mOtherILayout);
                uiView.setFilterTag(mMyTags.get(position));
                if (position == mViewPager.getCurrentItem()) {
                    mLastUIView = uiView;
                }
                return uiView;
            }

            @Override
            public int getCount() {
                return mMyTags.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mMyTags.get(position).getName();
            }
        });
    }

    /**
     * 滚动到顶部
     */
    public void scrollToTop() {
        if (mLastUIView != null) {
            mLastUIView.scrollToTop();
        }
    }
}

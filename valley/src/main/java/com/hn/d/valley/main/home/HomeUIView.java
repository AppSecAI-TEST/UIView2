package com.hn.d.valley.main.home;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.angcyo.uiview.container.UILayoutImpl;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.dialog.UIItemDialog;
import com.angcyo.uiview.github.tablayout.SegmentTabLayout;
import com.angcyo.uiview.github.tablayout.listener.OnTabSelectListener;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.skin.ISkin;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.view.UIIViewImpl;
import com.angcyo.uiview.widget.RTitleCenterLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.iview.VideoRecordUIView;
import com.hn.d.valley.bean.realm.Tag;
import com.hn.d.valley.control.PublishControl;
import com.hn.d.valley.main.home.circle.CircleUIView;
import com.hn.d.valley.main.home.nearby.NearbyUIView;
import com.hn.d.valley.main.home.recommend.LoadStatusCallback;
import com.hn.d.valley.main.home.recommend.RecommendUIView2;
import com.hn.d.valley.sub.user.PublishDynamicUIView;
import com.lzy.imagepicker.ImagePickerHelper;

import butterknife.BindView;
import rx.functions.Action0;
import rx.functions.Action3;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：首页/恐龙谷
 * 创建人员：Robi
 * 创建时间：2016/12/16 9:44
 * 修改人员：Robi
 * 修改时间：2016/12/16 9:44
 * 修改备注：
 * Version: 1.0.0
 */
public class HomeUIView extends BaseUIView implements LoadStatusCallback {

    //    @BindView(R.id.home_nav_layout)
//    CommonTabLayout mHomeNavLayout;
    //    @BindView(R.id.view_pager)
//    UIViewPager mViewPager;
    @BindView(R.id.ui_layout)
    UILayoutImpl mHomeLayout;
    SegmentTabLayout mHomeNavLayout;
    private ViewPager.SimpleOnPageChangeListener mPageChangeListener;
    private RecommendUIView2 mRecommendUIView2;
    private Tag currentTag;
    private NearbyUIView mNearbyUIView;
    private CircleUIView mCircleUIView;
    private int lastPosition = -1;

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.ui_layout);
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
//        mHomeNavLayout.setCurrentTab(1, false);

        if (mRecommendUIView2 == null) {
            mRecommendUIView2 = new RecommendUIView2(this);
            mRecommendUIView2.bindOtherILayout(mOtherILayout);
            mHomeLayout.startIView(mRecommendUIView2, new UIParam(false));
        } else {
            mHomeLayout.showIView(mRecommendUIView2, false);
        }
        lastPosition = 1;
    }

    private void initViewPager() {
//        mViewPager.setPageTransformer(true, new FadeInOutPageTransformer());
//        mViewPager.setOffscreenPageLimit(2);
//        mViewPager.setAdapter(new UIPagerAdapter() {
//            @Override
//            protected IView getIView(int position) {
//                if (position == 1) {
//                    if (mRecommendUIView2 == null) {
//                        mRecommendUIView2 = new RecommendUIView();
//                    }
//                    return mRecommendUIView2.bindOtherILayout(mOtherILayout);
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
//        ArrayList<CustomTabEntity> tabs = new ArrayList<>();
//        tabs.add(new TabEntity(mActivity.getString(R.string.home_circle), -1, -1, false, false, false));
//        tabs.add(new TabEntity(mActivity.getString(R.string.home_recommend), -1, -1, false, false, false));
//        tabs.add(new TabEntity(mActivity.getString(R.string.home_nearby), -1, -1, false, false, false));
//
//        mHomeNavLayout.setTabData(tabs);
//
//        mHomeNavLayout.setOnTabSelectListener(new OnTabSelectListener() {
//            @Override
//            public void onTabSelect(int position) {
//                changeViewPager(position);
//            }
//
//            @Override
//            public void onTabReselect(int position) {
////                if (position == 1) {
////                    //推荐 , 弹出标签
////                    new TagFilterUIDialog(new Action1<Tag>() {
////                        @Override
////                        public void call(Tag tag) {
////                            currentTag = tag;
////                            mRecommendUIView2.setFilterTag(tag);
////                            mRecommendUIView2.loadData();
////                        }
////                    }, currentTag == null ? TagsControl.allTag : currentTag).showDialog(mOtherILayout);
////                }
//            }
//        });

        mHomeNavLayout.setTabData(new String[]{getString(R.string.circle_title), getString(R.string.square_title)});
        mHomeNavLayout.setCurrentTab(1);
        mHomeNavLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                changeViewPager(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        updateSkin();
    }

    private void changeViewPager(int position) {
//        mViewPager.removeOnPageChangeListener(mPageChangeListener);
//        mViewPager.setCurrentItem(position);
//        mViewPager.addOnPageChangeListener(mPageChangeListener);

        boolean isRightToLeft = position < lastPosition;
        if (position == 0) {
            if (mCircleUIView == null) {
                mCircleUIView = new CircleUIView(this);
                mCircleUIView.bindOtherILayout(mOtherILayout);
                mCircleUIView.setIsRightJumpLeft(isRightToLeft);
                mHomeLayout.startIView(mCircleUIView);
            } else {
                mCircleUIView.setIsRightJumpLeft(isRightToLeft);
                mHomeLayout.showIView(mCircleUIView);
            }
        } else if (position == 1) {
            if (mRecommendUIView2 == null) {
                mRecommendUIView2 = new RecommendUIView2(this);
                mRecommendUIView2.bindOtherILayout(mOtherILayout);
                mRecommendUIView2.setIsRightJumpLeft(isRightToLeft);
                mHomeLayout.startIView(mRecommendUIView2);
            } else {
                mRecommendUIView2.setIsRightJumpLeft(isRightToLeft);
                mHomeLayout.showIView(mRecommendUIView2);
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
        } else if (currentItem == 1 && mRecommendUIView2 != null) {
            mRecommendUIView2.scrollToTop();
        } else if (currentItem == 2 && mNearbyUIView != null) {
            mNearbyUIView.scrollToTop();
        }
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setTitleString("")
                .addRightItem(TitleBarPattern.buildImage(R.drawable.fabudongtai, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HomeUIView.this.onClick();
                    }
                }))
                .setOnInitTitleLayout(new TitleBarPattern.SingleTitleInit() {
                    @Override
                    public void onInitLayout(RTitleCenterLayout parent) {
                        mHomeNavLayout = (SegmentTabLayout) LayoutInflater.from(mActivity)
                                .inflate(R.layout.segment_tab_layout, parent)
                                .findViewById(R.id.tab_layout);
                        parent.setTitleView(mHomeNavLayout);
                    }
                });
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }

    /**
     * 发布了动态之后, 插入到第一条
     */
    public void onPublishStart() {
        if (mHomeNavLayout.getCurrentTab() == 0 && mCircleUIView != null) {
            mCircleUIView.onPublishStart();
        }
    }

    public void onClick() {
        //发布动态
        UIItemDialog.build()
                .addItem(getString(R.string.publish_image_tip), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImagePickerHelper.startImagePicker(mActivity, false, true, false, true, 9);
                    }
                })
                .addItem(getString(R.string.publish_video_tip), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new VideoRecordUIView(new Action3<UIIViewImpl,String,String>() {
                            @Override
                            public void call(UIIViewImpl iView , String path , String s) {

                                iView.replaceIView(new PublishDynamicUIView(new PublishDynamicUIView.VideoStatusInfo(path, s), new Action0() {
                                    @Override
                                    public void call() {
                                        //开始发布任务.
                                        PublishControl.instance().startPublish(new PublishControl.OnPublishListener() {
                                            @Override
                                            public void onPublishStart() {
                                                HomeUIView.this.onPublishStart();
                                            }

                                            @Override
                                            public void onPublishEnd() {
                                            }
                                        });
                                    }
                                }));
                            }
                        }));
                    }
                }).showDialog(mOtherILayout);
    }

    @Override
    public void onLoadStart() {
        showLoadView();
    }

    @Override
    public void onLoadEnd() {
        hideLoadView();
    }

    @Override
    public void onSkinChanged(ISkin skin) {
        super.onSkinChanged(skin);
        mHomeNavLayout.setTextSelectColor(SkinHelper.getSkin().getThemeColor());
    }
}

package com.hn.d.valley.sub.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.container.UILayoutImpl;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.github.tablayout.SegmentTabLayout;
import com.angcyo.uiview.github.tablayout.listener.OnTabSelectListener;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.widget.RTitleCenterLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.sub.other.FriendsRecommendUIView;

/**
 * Created by hewking on 2017/3/16.
 */
@Deprecated
public final class NewFriend2UIView extends BaseContentUIView {

    UILayoutImpl mContainerLayout;
    SegmentTabLayout tabLayout;

    private FriendsNewUIView mFollowersUIView;
    private FriendsRecommendUIView mFriendsRecommUIView;

    private int lastPosition = -1;

    public NewFriend2UIView() {
    }

    @Override
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_new_friend_2);
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mContainerLayout = v(R.id.main_new_friend);
    }

    private void initTablayout() {
        tabLayout.setTabData(new String[]{getString(R.string.new_friend), getString(R.string.frient_tip)});
        tabLayout.setCurrentTab(0);
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                changeViewPager(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    private void changeViewPager(int position) {
        boolean isRightToLeft = position < lastPosition;
        if (position == 0) {
            if (mFollowersUIView == null) {
                mFollowersUIView = new FriendsNewUIView();
                mFollowersUIView.bindParentILayout(mParentILayout);
                mFollowersUIView.setIsRightJumpLeft(isRightToLeft);
                mContainerLayout.startIView(mFollowersUIView);
            } else {
                mFollowersUIView.setIsRightJumpLeft(isRightToLeft);
                mContainerLayout.showIView(mFollowersUIView);
            }
        } else if (position == 1) {
            if (mFriendsRecommUIView == null) {
                mFriendsRecommUIView = new FriendsRecommendUIView(UserCache.getUserAccount());
                mFriendsRecommUIView.bindParentILayout(mParentILayout);
                mFriendsRecommUIView.setIsRightJumpLeft(isRightToLeft);
                mContainerLayout.startIView(mFriendsRecommUIView);
            } else {
                mFriendsRecommUIView.setIsRightJumpLeft(isRightToLeft);
                mContainerLayout.showIView(mFriendsRecommUIView);
            }
        }
        lastPosition = position;
    }


    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        if (mFollowersUIView == null) {
            mFollowersUIView = new FriendsNewUIView();
            mFollowersUIView.bindParentILayout(mParentILayout);
            mContainerLayout.startIView(mFollowersUIView, new UIParam(false));
        } else {
            mContainerLayout.showIView(mFollowersUIView, false);
        }

        lastPosition = 0;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setShowBackImageView(true)
                .setTitleString("")
                .setOnInitTitleLayout(new TitleBarPattern.SingleTitleInit() {
                    @Override
                    public void onInitLayout(RTitleCenterLayout parent) {
                        View.inflate(mActivity, R.layout.item_segment_layout, parent);
                        tabLayout = (SegmentTabLayout) parent.findViewById(R.id.tablayout_new_friend);
                        initTablayout();
                    }
                });
    }


}

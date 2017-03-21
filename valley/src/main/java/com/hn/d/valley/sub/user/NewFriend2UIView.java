package com.hn.d.valley.sub.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.angcyo.uiview.container.UILayoutImpl;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.github.tablayout.SegmentTabLayout;
import com.angcyo.uiview.github.tablayout.listener.OnTabSelectListener;
import com.angcyo.uiview.model.TitleBarPattern;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.sub.other.FollowersRecyclerUIView;
import com.hn.d.valley.sub.other.FriendsRecommendUIView;

import butterknife.BindView;

/**
 * Created by hewking on 2017/3/16.
 */
public final class NewFriend2UIView extends BaseContentUIView {


    @BindView(R.id.main_new_friend)
    UILayoutImpl mContainerLayout;
    @BindView(R.id.tablayout_new_friend)
    SegmentTabLayout tabLayout;

    private FriendsNewUIView mFollowersUIView;
    private FriendsRecommendUIView mFriendsRecommUIView;

    private int lastPosition = -1;

    public NewFriend2UIView() {}

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_new_friend_2);

    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        initTablayout();

    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);

        if (mFollowersUIView == null) {
            mFollowersUIView = new FriendsNewUIView();
            mFollowersUIView.bindOtherILayout(mOtherILayout);
            mContainerLayout.startIView(mFollowersUIView, new UIParam(false));
        } else {
            mContainerLayout.showIView(mFollowersUIView, false);
        }

        lastPosition = 0;
    }

    private void initTablayout() {
        tabLayout.setTabData(new String[]{"新的朋友","好友推荐"});
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
                // TODO: 2017/3/17 更改为新的朋友UIVIew
                mFollowersUIView = new FriendsNewUIView();
                mFollowersUIView.bindOtherILayout(mOtherILayout);
                mFollowersUIView.setIsRightJumpLeft(isRightToLeft);
                mContainerLayout.startIView(mFollowersUIView);
            } else {
                mFollowersUIView.setIsRightJumpLeft(isRightToLeft);
                mContainerLayout.showIView(mFollowersUIView);
            }
        } else if (position == 1) {
            if (mFriendsRecommUIView == null) {
                mFriendsRecommUIView = new FriendsRecommendUIView(UserCache.getUserAccount());
                mFriendsRecommUIView.bindOtherILayout(mOtherILayout);
                mFriendsRecommUIView.setIsRightJumpLeft(isRightToLeft);
                mContainerLayout.startIView(mFriendsRecommUIView);
            } else {
                mFriendsRecommUIView.setIsRightJumpLeft(isRightToLeft);
                mContainerLayout.showIView(mFriendsRecommUIView);
            }
        }
    }


    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return null;
    }


}

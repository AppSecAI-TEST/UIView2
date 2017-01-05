package com.hn.d.valley.sub;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.angcyo.uiview.control.PhotoPagerControl;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExBaseAdapter;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.utils.Utils;
import com.angcyo.uiview.widget.viewpager.TextIndicator;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.bean.SearchUserBean;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.widget.HnGenderView;

import java.util.ArrayList;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/05 17:43
 * 修改人员：Robi
 * 修改时间：2017/01/05 17:43
 * 修改备注：
 * Version: 1.0.0
 */
public class UserInfoUIView extends BaseContentUIView {

    SearchUserBean mSearchUserBean;
    private RRecyclerView mRecyclerView;
    private UserInfoAdapter mUserInfoAdapter;

    public UserInfoUIView(SearchUserBean searchUserBean) {
        mSearchUserBean = searchUserBean;
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        mRecyclerView = new RRecyclerView(mActivity);
        baseContentLayout.addView(mRecyclerView, new ViewGroup.LayoutParams(-1, -1));
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setFloating(true).setTitleString("")
                .setShowBackImageView(true)
                .setTitleBarBGColor(Color.TRANSPARENT)
                .addRightItem(TitleBarPattern.TitleBarItem.build()
                        .setRes(R.drawable.more)
                        .setListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }));
    }

    @Override
    protected void initContentLayout() {
        super.initContentLayout();
        mUserInfoAdapter = new UserInfoAdapter(mActivity);
        mUserInfoAdapter.setHeaderData(mSearchUserBean);
        mRecyclerView.setItemAnim(false);
        mRecyclerView.setAdapter(mUserInfoAdapter);
    }

    class UserInfoAdapter extends RExBaseAdapter<SearchUserBean, UserDiscussListBean.DataListBean, String> {

        public UserInfoAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            if (viewType == TYPE_HEADER) {
                return R.layout.item_search_user_top_layout;
            }
            return 0;
        }

        @Override
        protected void onBindHeaderView(RBaseViewHolder holder, int posInHeader, SearchUserBean hBean) {
            ArrayList<String> photos = new ArrayList<>();
            photos.add(hBean.getAvatar());
            photos.add(hBean.getAvatar());
            photos.add(hBean.getAvatar());
            photos.add(hBean.getAvatar());
            photos.addAll(Utils.split(hBean.getPhotos()));
            new PhotoPagerControl(((TextIndicator) holder.v(R.id.single_text_indicator_view)),
                    (ViewPager) holder.v(R.id.view_pager), photos);
            holder.fillView(hBean);
            HnGenderView hnGenderView = holder.v(R.id.grade);
            hnGenderView.setGender(hBean.getSex(), hBean.getGrade());

            holder.v(R.id.star).setVisibility(1 == hBean.getIs_star() ? View.VISIBLE : View.GONE);
            holder.v(R.id.auth).setVisibility("1".equalsIgnoreCase(hBean.getIs_auth()) ? View.VISIBLE : View.GONE);
        }
    }
}

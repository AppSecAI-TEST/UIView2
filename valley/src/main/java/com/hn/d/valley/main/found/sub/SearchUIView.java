package com.hn.d.valley.main.found.sub;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RBaseAdapter;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.rsen.PlaceholderView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseRecyclerUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.UserRecommendBean;
import com.hn.d.valley.main.me.UserDetailUIView;
import com.hn.d.valley.main.message.service.SearchService;
import com.hn.d.valley.widget.HnGlideImageView;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：搜一搜界面
 * 创建人员：Robi
 * 创建时间：2017/01/17 09:19
 * 修改人员：Robi
 * 修改时间：2017/01/17 09:19
 * 修改备注：
 * Version: 1.0.0
 */
public class SearchUIView extends BaseRecyclerUIView<SearchUIView.TopBean,
        SearchUIView.DataBean, SearchUIView.BottomBean> {

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setTitleHide(false)
                .setTitleString(mActivity.getString(R.string.search_title));
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }

    @Override
    protected boolean hasDecoration() {
        return false;
    }

    @Override
    protected boolean hasScrollListener() {
        return false;
    }

    @Override
    protected void initRefreshLayout() {
        super.initRefreshLayout();
        mRefreshLayout.setTopView(new PlaceholderView(mActivity));
        mRefreshLayout.setNotifyListener(false);
    }

    @Override
    protected RExBaseAdapter<TopBean, DataBean, BottomBean> initRExBaseAdapter() {
        return new RExBaseAdapter<TopBean, DataBean, BottomBean>(mActivity) {
            @Override
            protected int getHeaderItemType(int posInHeader) {
                return super.getHeaderItemType(posInHeader) + posInHeader;
            }

            @Override
            protected int getDataItemType(int posInData) {
                return super.getDataItemType(posInData);
            }

            @Override
            protected int getFooterItemType(int posInFooter) {
                return super.getFooterItemType(posInFooter);
            }

            @Override
            protected int getItemLayoutId(int viewType) {
                if (viewType == TYPE_HEADER) {
                    return R.layout.item_search_layout_top;
                }
                if (viewType == TYPE_HEADER + 1) {
                    return R.layout.item_search_layout_user_recommend;
                }
                return super.getItemLayoutId(viewType);
            }

            @Override
            protected void onBindFooterView(RBaseViewHolder holder, int posInFooter, BottomBean footerBean) {
                super.onBindFooterView(holder, posInFooter, footerBean);
            }

            @Override
            protected void onBindHeaderView(RBaseViewHolder holder, int posInHeader, TopBean headerBean) {
                if (posInHeader == 0) {
                    holder.v(R.id.search_layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                } else {
                    RRecyclerView recyclerView = holder.reV(R.id.recycler_view);
                    recyclerView.addItemDecoration(new RBaseItemDecoration((int) (density() * 10), Color.TRANSPARENT));
                    recyclerView.setAdapter(new UserRecommendAdapter(mActivity, headerBean.mUserRecommendBeen));
                }
            }

            @Override
            protected void onBindDataView(RBaseViewHolder holder, int posInData, DataBean dataBean) {
                super.onBindDataView(holder, posInData, dataBean);
            }
        };
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        mRExBaseAdapter.appendHeaderData(new TopBean());

        //((UILayoutImpl) mOtherILayout).setEnableSwipeBack(false);
        loadUserRecommend();
    }

    /**
     * 加载名人推荐
     */
    private void loadUserRecommend() {
        add(RRetrofit.create(SearchService.class, RRetrofit.CacheType.MAX_STALE)
                .userRecommend(Param.buildMap())
                .compose(Rx.transformerList(UserRecommendBean.class))
                .subscribe(new BaseSingleSubscriber<List<UserRecommendBean>>() {
                    @Override
                    public void onSucceed(List<UserRecommendBean> bean) {
                        super.onSucceed(bean);
                        TopBean topBean = new TopBean(bean);
                        mRExBaseAdapter.appendHeaderData(topBean);
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                    }
                }));
    }

    public static class TopBean {
        public List<UserRecommendBean> mUserRecommendBeen;

        public TopBean() {
        }

        public TopBean(List<UserRecommendBean> userRecommendBeen) {
            mUserRecommendBeen = userRecommendBeen;
        }
    }

    public static class DataBean {

    }

    public static class BottomBean {

    }

    /**
     * 名人推荐adapter
     */
    private class UserRecommendAdapter extends RBaseAdapter<UserRecommendBean> {

        public UserRecommendAdapter(Context context, List<UserRecommendBean> datas) {
            super(context, datas);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_search_layout_user_recommend_sub_item;
        }

        @Override
        protected void onBindView(RBaseViewHolder holder, int position, final UserRecommendBean bean) {
            holder.tv(R.id.username_view).setText(bean.getUsername());
            holder.tv(R.id.fans_count_view).setText(bean.getFans_count() + "");

            HnGlideImageView imageView = holder.v(R.id.image_view);
            imageView.setImageUrl(bean.getAvatar());

            holder.v(R.id.item_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startIView(new UserDetailUIView(bean.getUid()));
                }
            });
        }
    }

}

package com.hn.d.valley.main.me;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.adapter.RBaseAdapter;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.widget.IShowState;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.IndustriesBean;
import com.hn.d.valley.bean.realm.Tag;
import com.hn.d.valley.service.SiteService;
import com.hn.d.valley.widget.HnRefreshLayout;

import java.util.List;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：行业选择
 * 创建人员：Robi
 * 创建时间：2017/03/13 15:24
 * 修改人员：Robi
 * 修改时间：2017/03/13 15:24
 * 修改备注：
 * Version: 1.0.0
 */
public class IndustriesSelectorUIView extends UIIDialogImpl implements RefreshLayout.OnRefreshListener, RBaseAdapter.OnAdapterLoadMoreListener {

    protected CompositeSubscription mSubscriptions;

    private HnRefreshLayout mHnRefreshLayout;
    private RRecyclerView mRecyclerView;
    private RModelAdapter<Tag> mAdapter;
    private int page = 1;

    private Action1<Tag> mSelectorAction;

    public IndustriesSelectorUIView(Action1<Tag> selectorAction) {
        mSelectorAction = selectorAction;
    }

    @Override
    protected View inflateDialogView(RelativeLayout dialogRootLayout, LayoutInflater inflater) {
        return inflate(R.layout.view_industries_selector);
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        mSubscriptions.unsubscribe();
    }

    @Override
    public void onViewCreate(View rootView) {
        super.onViewCreate(rootView);
        mSubscriptions = new CompositeSubscription();
        mHnRefreshLayout = mViewHolder.v(R.id.refresh_layout);
        mHnRefreshLayout.setRefreshDirection(RefreshLayout.TOP);
        mRecyclerView = mViewHolder.v(R.id.recycler_view);
        mAdapter = new RModelAdapter<Tag>(mActivity) {

            @Override
            protected int getItemLayoutId(int viewType) {
                return R.layout.item_industries_radio;
            }

            @Override
            protected void onBindCommonView(RBaseViewHolder holder, final int position, Tag bean) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setSelectorPosition(position);
                    }
                });
                holder.tv(R.id.text_view).setText(bean.getName());
            }

            @Override
            protected void onBindModelView(int model, boolean isSelector, RBaseViewHolder holder, int position, Tag bean) {
                holder.v(R.id.image_view).setVisibility(isSelector ? View.VISIBLE : View.GONE);
            }

        };
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RBaseItemDecoration(getDimensionPixelOffset(R.dimen.base_line)));
        mAdapter.setShowState(IShowState.LOADING);
        mAdapter.addOnModelChangeListener(new RModelAdapter.SingleChangeListener() {
            @Override
            public void onSelectorChange(List<Integer> selectorList) {
                if (mSelectorAction != null) {
                    mSelectorAction.call(mAdapter.getAllDatas().get(selectorList.get(0)));
                }
                finishDialog();
            }
        });
        mAdapter.setModel(RModelAdapter.MODEL_SINGLE);
        mHnRefreshLayout.addOnRefreshListener(this);
        mAdapter.setOnLoadMoreListener(this);
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        loadData();
    }

    private void loadData() {
        mSubscriptions.add(RRetrofit
                .create(SiteService.class)
                .getIndustries(Param.buildMap("page:" + page))
                .compose(Rx.transformer(IndustriesBean.class))
                .subscribe(new BaseSingleSubscriber<IndustriesBean>() {
                    @Override
                    public void onSucceed(IndustriesBean bean) {
                        super.onSucceed(bean);
                        if (bean != null) {
                            if (bean.getData_list().isEmpty()) {
                                mAdapter.setNoMore(true);
                            } else {
                                if (page == 1) {
                                    mAdapter.resetData(bean.getData_list());
                                    mAdapter.setEnableLoadMore(true);
                                } else {
                                    mAdapter.appendData(bean.getData_list());
                                    mAdapter.setLoadMoreEnd();
                                }
                                mAdapter.setShowState(IShowState.NORMAL);
                            }
                        } else {
                            mAdapter.setShowState(IShowState.NONET);
                        }
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                        mHnRefreshLayout.setRefreshEnd();
                    }
                })
        );
    }

    @Override
    public void onRefresh(@RefreshLayout.Direction int direction) {
        page = 1;
        mAdapter.setLoadMoreEnd();
        loadData();
    }

    @Override
    public void onAdapterLodeMore(RBaseAdapter baseAdapter) {
        page++;
        loadData();
    }
}

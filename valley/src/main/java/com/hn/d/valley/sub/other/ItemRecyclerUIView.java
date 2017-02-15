package com.hn.d.valley.sub.other;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExBaseAdapter;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.widget.HnEmptyRefreshLayout;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：使用RecyclerView的方式, 搭建普通界面
 * 创建人员：Robi
 * 创建时间：2017/02/15 11:20
 * 修改人员：Robi
 * 修改时间：2017/02/15 11:20
 * 修改备注：
 * Version: 1.0.0
 */
public abstract class ItemRecyclerUIView<T> extends SingleRecyclerUIView<T> {

    @Override
    protected RExBaseAdapter<String, T, String> initRExBaseAdapter() {
        return new RExBaseAdapter<String, T, String>(mActivity, createItems()) {

            @Override
            protected int getDataItemType(int posInData) {
                return ItemRecyclerUIView.this.getDataItemType(posInData);
            }

            @Override
            protected void onBindDataView(RBaseViewHolder holder, int posInData, T dataBean) {
                ItemRecyclerUIView.this.onBindDataView(holder, posInData, dataBean);
            }

            @Override
            protected int getItemLayoutId(int viewType) {
                return ItemRecyclerUIView.this.getItemLayoutId(viewType);
            }
        };
    }

    protected int getDataItemType(int posInData) {
        return 1;
    }

    protected abstract void onBindDataView(RBaseViewHolder holder, int posInData, T dataBean);

    protected abstract int getItemLayoutId(int viewType);

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar();
    }

    @Override
    public int getDefaultBackgroundColor() {
        return mActivity.getResources().getColor(R.color.chat_bg_color);
    }

    @Override
    protected void inflateRecyclerRootLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        mRefreshLayout = new HnEmptyRefreshLayout(mActivity);
        mRecyclerView = new RRecyclerView(mActivity);
        mRefreshLayout.addView(mRecyclerView, new ViewGroup.LayoutParams(-1, -1));
        baseContentLayout.addView(mRefreshLayout, new ViewGroup.LayoutParams(-1, -1));
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }

    /**
     * 请在初始化RecycleView的时候, 添加自定义的分割线
     */
    @Override
    protected boolean hasDecoration() {
        return false;
    }

    @Override
    protected boolean hasScrollListener() {
        return false;
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
        mRExBaseAdapter.setEnableLoadMore(false);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    @Override
    protected void initRefreshLayout() {
        super.initRefreshLayout();
        mRefreshLayout.setRefreshDirection(RefreshLayout.BOTH);
    }

    protected abstract List<T> createItems();

    public static class ViewItemInfo {
        public String itemString;
        public View.OnClickListener itemClickListener;

        public ViewItemInfo() {
        }

        public ViewItemInfo(String itemString, View.OnClickListener itemClickListener) {
            this.itemString = itemString;
            this.itemClickListener = itemClickListener;
        }
    }
}

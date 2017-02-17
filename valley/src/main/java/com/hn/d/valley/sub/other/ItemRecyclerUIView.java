package com.hn.d.valley.sub.other;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExBaseAdapter;
import com.angcyo.uiview.recycler.RExItemDecoration;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.widget.HnEmptyRefreshLayout;

import java.util.ArrayList;
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

    protected List<T> mItemsList;

    @Override
    protected RExBaseAdapter<String, T, String> initRExBaseAdapter() {
        mItemsList = new ArrayList<>();
        createItems(mItemsList);
        return new RExBaseAdapter<String, T, String>(mActivity, mItemsList) {

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
        return posInData;
    }

    protected abstract void onBindDataView(RBaseViewHolder holder, int posInData, T dataBean);

    protected abstract int getItemLayoutId(int viewType);

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setShowBackImageView(true);
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
        mRecyclerView.addItemDecoration(new RExItemDecoration(new RExItemDecoration.ItemDecorationCallback() {
            @Override
            public Rect getItemOffsets(LinearLayoutManager layoutManager, int position) {
                Rect rect = new Rect(0, 0, 0, 0);
                T t = mItemsList.get(position);
                if (t instanceof ViewItemInfo) {
                    if (((ViewItemInfo) t).mCallback != null) {
                        ((ViewItemInfo) t).mCallback.setItemOffsets(rect);
                    }
                }
                return rect;
            }

            @Override
            public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {
                T t = mItemsList.get(position);
                if (t instanceof ViewItemInfo) {
                    if (((ViewItemInfo) t).mCallback != null) {
                        ((ViewItemInfo) t).mCallback.draw(canvas, paint, itemView, offsetRect, itemCount, position);
                    }
                }
            }
        }));
    }

    @Override
    protected void initRefreshLayout() {
        super.initRefreshLayout();
        mRefreshLayout.setRefreshDirection(RefreshLayout.BOTH);
    }

    protected abstract void createItems(List<T> items);

    public static class ViewItemInfo {
        public String itemString;
        public View.OnClickListener itemClickListener;
        public ItemCallback mCallback;

        public ViewItemInfo() {
        }

        public ViewItemInfo(ItemCallback callback) {
            mCallback = callback;
        }

        public ViewItemInfo(String itemString, View.OnClickListener itemClickListener) {
            this.itemString = itemString;
            this.itemClickListener = itemClickListener;
        }
    }

    public static abstract class ItemCallback {
        public abstract void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean);

        public void setItemOffsets(Rect rect) {
        }

        public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {
        }
    }
}

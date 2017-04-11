package com.hn.d.valley.main.message.chat;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/07 16:34
 * 修改人员：hewking
 * 修改时间：2017/04/07 16:34
 * 修改备注：
 * Version: 1.0.0
 */
public abstract class BaseFetchAdapter<K extends RBaseViewHolder> extends RecyclerView.Adapter<K> {

    protected List<IMMessage> mData;

    private boolean isScrolling = false;
    // basic
    protected Context mContext;
    protected int mLayoutResId;
    protected LayoutInflater mLayoutInflater;

    public BaseFetchAdapter(RecyclerView recyclerView, int layoutResId, List<IMMessage> data) {
        this.mData = data == null ? new ArrayList<IMMessage>() : data;
        if (layoutResId != 0) {
            this.mLayoutResId = layoutResId;
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                isScrolling = newState != RecyclerView.SCROLL_STATE_IDLE;
            }
        });
    }

    abstract int getDefItemViewType(int position);

    @Override
    public int getItemViewType(int position) {
        // fetch
        autoRequestFetchMoreData(position);
        // load
        autoRequestLoadMoreData(position);
        return getDefItemViewType(position);
    }

    private void autoRequestLoadMoreData(int position) {

    }

    private void autoRequestFetchMoreData(int position) {

    }


    @Override
    public K onCreateViewHolder(ViewGroup parent, int viewType) {

        this.mContext = parent.getContext();
        this.mLayoutInflater = LayoutInflater.from(mContext);

        K viewHolder = onCreateDefViewHolder(parent, viewType);


        return viewHolder;
    }

    protected K onCreateDefViewHolder(ViewGroup parent, int viewType) {
        return createBaseViewHolder(parent, viewType);
    }

    protected K createBaseViewHolder(ViewGroup parent, int layoutResId) {
        return createBaseViewHolder(getItemView(layoutResId, parent));
    }

    protected View getItemView(int layoutResId, ViewGroup parent) {
        return mLayoutInflater.inflate(layoutResId, parent, false);
    }

    protected K createBaseViewHolder(View view) {
        return (K) new RBaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(K holder, int position) {

        int viewType = holder.getItemViewType();
        convert(holder, mData.get(position), position, isScrolling);

    }

    protected abstract void convert(K helper, IMMessage item, int position, boolean isScrolling);

    public int getDataSize() {
        return mData == null ? 0 : mData.size();
    }

    public void remove(int position) {
        final IMMessage item = mData.get(position);
        mData.remove(position);
        notifyItemRemoved(position);
        onRemove(item);
    }

    protected void onRemove(IMMessage item) {

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public IMMessage getItem(int position) {
        return mData.get(position);
    }

    public void fetchMoreComplete(final RecyclerView recyclerView, int newDataSize) {

        notifyItemChanged(0);

        // 定位到insert新消息前的top消息位置。必须移动，否则还在顶部，会继续fetch!!!
        if (recyclerView != null) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                //获取第一个可见view的位置
                int firstItemPosition = linearManager.findFirstVisibleItemPosition();
                if (firstItemPosition == 0) {
                    // 最顶部可见的View已经是FetchMoreView了，那么add数据&局部刷新后，要进行定位到上次的最顶部消息。
                    // newDataSize -1  为加载成功向下 偏移量
                    recyclerView.scrollToPosition(newDataSize - 1);
                }
            } else {
                recyclerView.scrollToPosition(newDataSize);
            }
        }
    }

    public void fetchMoreFailed() {

    }

    public void loadMoreFail() {

    }
}

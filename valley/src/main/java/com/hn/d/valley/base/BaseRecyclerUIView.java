package com.hn.d.valley.base;

import android.graphics.Color;
import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseAdapter;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExBaseAdapter;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.angcyo.uiview.widget.viewpager.UIViewPager;
import com.hn.d.valley.R;
import com.hn.d.valley.widget.HnRefreshLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/06 13:42
 * 修改人员：Robi
 * 修改时间：2017/01/06 13:42
 * 修改备注：
 * Version: 1.0.0
 */
public abstract class BaseRecyclerUIView<H, T, F> extends BaseContentUIView
        implements RBaseAdapter.OnAdapterLoadMoreListener, RefreshLayout.OnRefreshListener {

    public static final int PAGE_SIZE = 20;//没次请求默认的数据条数
    protected HnRefreshLayout mRefreshLayout;
    protected RRecyclerView mRecyclerView;
    protected RExBaseAdapter<H, T, F> mRExBaseAdapter;
    protected int data_count;//服务返回的数据库中数据的条数,用来翻页
    protected int page = 1;//当前请求第几页
    protected int next = 1;//下一页
    protected long loadTime = 0;//加载数据的时间

    public static void initEmpty(final RBaseViewHolder viewHolder, boolean isEmpty, String tip) {
        if (viewHolder != null) {
            final View emptyRootLayout = viewHolder.v(R.id.default_pager_root_layout);
            final TextView emptyTipView = viewHolder.v(R.id.default_pager_tip_view);
            if (emptyRootLayout != null) {
                emptyRootLayout.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                emptyTipView.setText(tip);
            }
        }
    }

    @CallSuper
    @Override
    final protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflateRecyclerRootLayout(baseContentLayout, inflater);
        inflateOverlayLayout(baseContentLayout, inflater);
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        initRefreshLayout();
        initRecyclerView();

        if (getDefaultLayoutState() == LayoutState.CONTENT
                && !isDelayLoad()) {
            loadData();
        }
    }

    @CallSuper
    protected void initRecyclerView() {
        mRecyclerView = getRecyclerView();
        mRExBaseAdapter = initRExBaseAdapter();

        mRExBaseAdapter.setNoMore();//默认没有更多
        mRExBaseAdapter.setLoadMoreListener(this);

        if (hasDecoration()) {
            mRecyclerView.addItemDecoration(getItemDecoration());
        }
        mRecyclerView.setItemAnim(false);
        mRecyclerView.setAdapter(mRExBaseAdapter);

        if (hasScrollListener()) {
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (mUITitleBarContainer != null) {
                        mUITitleBarContainer.evaluateBackgroundColorSelf(recyclerView.computeVerticalScrollOffset());
                    }
                }
            });
        }
    }

    /**
     * 创建分割线
     */
    protected RBaseItemDecoration getItemDecoration() {
        return new RBaseItemDecoration(getItemDecorationHeight())
                .setDrawLastLine(hasLastDecoration());
    }

    /**
     * 分割线的高度
     */
    protected int getItemDecorationHeight() {
        return mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);
    }

    /**
     * 是否添加分割线
     */
    protected boolean hasDecoration() {
        return true;
    }

    protected boolean hasScrollListener() {
        return true;
    }

    /**
     * 最后一个Item是否具有分割线
     */
    protected boolean hasLastDecoration() {
        return true;
    }

    @CallSuper
    protected void initRefreshLayout() {
        mRefreshLayout = getRefreshLayout();

        mRefreshLayout.setRefreshDirection(RefreshLayout.TOP);
        mRefreshLayout.addRefreshListener(this);
    }

    /**
     * 可以重写此方法, 实现自定义的布局
     */
    protected void inflateRecyclerRootLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        mRefreshLayout = new HnRefreshLayout(mActivity);
        mRecyclerView = new RRecyclerView(mActivity);
        mRefreshLayout.addView(mRecyclerView, new ViewGroup.LayoutParams(-1, -1));
        baseContentLayout.addView(mRefreshLayout, new ViewGroup.LayoutParams(-1, -1));
    }

    /**
     * 如果只是想添加自定义的覆盖层, 可以只重写此方法
     */
    protected void inflateOverlayLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        //空数据
        inflate(R.layout.layout_default_pager);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setFloating(true)
                .setTitleString(getTitleString())
                .setTitleHide(true)
                .setShowBackImageView(true)
                .setTitleBarBGColor(Color.TRANSPARENT);
    }

    @Override
    public void onShowInPager(UIViewPager viewPager) {
        super.onShowInPager(viewPager);
        if (mIViewStatus == STATE_VIEW_SHOW && isDelayLoad() && delayTime()) {
            loadData();
        }
    }

    /**
     * 2次加载数据的时间间隔是否大于60秒
     */
    protected boolean delayTime() {
        if (System.currentTimeMillis() - loadTime > 60 * 1000) {
            return true;
        }
        return false;
    }

    /**
     * 是否延迟调用加载数据, 在ViewPager中可以返回true
     */
    protected boolean isDelayLoad() {
        return false;
    }

    protected abstract RExBaseAdapter<H, T, F> initRExBaseAdapter();

    public RExBaseAdapter<H, T, F> getRExBaseAdapter() {
        return mRExBaseAdapter;
    }

    /**
     * 重写此方法, 获取自定义布局中的RefreshLayout
     */
    public HnRefreshLayout getRefreshLayout() {
        return mRefreshLayout;
    }

    /**
     * 重写此方法, 获取自定义布局中的RRecyclerView
     */
    public RRecyclerView getRecyclerView() {
        return mRecyclerView;
    }


    protected String getTitleString() {
        return "";
    }

    /**
     * 是否有下一页
     */
    public boolean hasNext() {
        return data_count > page * PAGE_SIZE;
    }

    @Override
    public void onAdapterLodeMore(RBaseAdapter baseAdapter) {
        //加载更多
        loadMoreData();
    }

    @Override
    public void onRefresh(@RefreshLayout.Direction int direction) {
        if (direction == RefreshLayout.TOP) {
            //刷新
            loadData();
        } else if (direction == RefreshLayout.BOTTOM) {
            //加载更多
        }
    }

    @CallSuper
    public void loadData() {
        //mRExBaseAdapter.setNoMore();
        loadTime = System.currentTimeMillis();
        page = 1;
        onCancel();//刷新数据,取消之前的加载更多请求
        onUILoadData(getPage());
    }

    public void loadMoreData() {
        page++;
        onUILoadData(getPage());
    }

    /**
     * 获取当前页
     */
    public String getPage() {
        return String.valueOf(page);
    }

    /**
     * 重写此方法, 实现数据的刷新和加载更多
     */
    protected void onUILoadData(String page) {

    }

    /**
     * 重写此方法, 实现空数据
     */
    protected void onEmptyData(boolean isEmpty) {
        initEmpty(mViewHolder, isEmpty, getEmptyTipString());
    }

    protected String getEmptyTipString() {
        return mActivity.getResources().getString(getEmptyTipStringId());
    }

    protected int getEmptyTipStringId() {
        return R.string.default_empty_tip;
    }

    /**
     * 初始化控件状态
     */
    public void onUILoadDataFinish() {
        if (mRefreshLayout != null) {
            mRefreshLayout.setRefreshEnd();
        }
        //mRExBaseAdapter.setLoadMoreEnd();
        if (mRExBaseAdapter != null && mRExBaseAdapter.isEnableLoadMore()) {
            if (hasNext()) {
                mRExBaseAdapter.setLoadMoreEnd();
            } else {
                mRExBaseAdapter.setNoMore();
            }
        }
    }

    /**
     * 接口返回数据后,请调用此方法设置数据
     */
    public void onUILoadDataEnd(List<T> datas, int data_count) {
        //显示内容布局,因为一开始可能是一个加载布局
        showContentLayout();

        this.data_count = data_count;
        boolean isEmptyData;

        if (isDataEmpty(datas) && page <= 1) {
            //接口返回空数据
            isEmptyData = true;
        } else {
            isEmptyData = false;
        }

        if (isEmptyData) {
            mRExBaseAdapter.resetAllData(datas);
            if (mRExBaseAdapter.isEnableLoadMore()) {
                mRExBaseAdapter.setEnableLoadMore(false);
            }
        } else {
            if (page <= 1) {
                mRExBaseAdapter.resetAllData(datas);
            } else {
                mRExBaseAdapter.appendAllData(datas);
            }
        }

        onEmptyData(isEmptyData);
    }

    public void onUILoadDataEnd() {
        onUILoadDataEnd(null, data_count);
    }

    /**
     * 判断数据是否为空, 可以返回true, 用来接管空界面处理
     */
    protected boolean isDataEmpty(List<T> datas) {
        return (datas == null || datas.isEmpty())
                && mRExBaseAdapter.getHeaderCount() == 0
                && mRExBaseAdapter.getFooterCount() == 0;
    }

    /**
     * 返回一个带page参数的map
     */
    public Map<String, String> getPageMap() {
        Map<String, String> map = new HashMap<>();
        map.put("page", getPage());
        return map;
    }
}

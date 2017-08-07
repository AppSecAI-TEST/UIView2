package com.hn.d.valley.base;

import android.graphics.Color;
import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RBaseAdapter;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.rsen.RGestureDetector;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.RSoftInputLayout;
import com.angcyo.uiview.widget.viewpager.UIViewPager;
import com.hn.d.valley.R;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.UserDiscussListBean;
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

    protected RefreshLayout mRefreshLayout;
    protected RRecyclerView mRecyclerView;
    protected RExBaseAdapter<H, T, F> mRExBaseAdapter;
    @Deprecated
    protected int data_count;//服务返回的数据库中数据的条数,用来翻页
    protected int page = 1;//当前请求第几页
    protected int next = 1;//下一页
    protected long loadTime = 0;//加载数据的时间
    protected boolean hasNext = true;//是否有下一页
    protected String last_id = "", first_id = "";//动态的id, 第一个和最后一个
    protected RSoftInputLayout mRootSoftInputLayout;

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
    final protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        if (getUITitleBarContainer() != null) {
            //双击标题, 自动滚动到顶部
            RGestureDetector.onDoubleTap(getUITitleBarContainer(), new RGestureDetector.OnDoubleTapListener() {
                @Override
                public void onDoubleTap() {
                    scrollToTop();
                }
            });
        }
        inflateRecyclerRootLayout(baseContentLayout, inflater);
        inflateOverlayLayout(baseContentLayout, inflater);
    }

    /**
     * 自动滚动置顶
     */
    public void scrollToTop() {
        if (getRecyclerView() != null) {
            getRecyclerView().scrollTo(0, false);
        }
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        initRefreshLayout();
        initRecyclerView();

        requestLoadDataOnShowContent();
    }

    protected void requestLoadDataOnShowContent() {
        if (getDefaultLayoutState() == LayoutState.CONTENT
                && !isLoadInViewPager()) {
            loadData();
        }
    }

    @CallSuper
    protected void initRecyclerView() {
        mRecyclerView = getRecyclerView();
        mRExBaseAdapter = initRExBaseAdapter();
        if (mRecyclerView == null) {
            return;
        }
        if (mRExBaseAdapter != null) {
            mRExBaseAdapter.setEnableLoadMore(false);
            //mRExBaseAdapter.setNoMore();//默认没有更多
            mRExBaseAdapter.setOnLoadMoreListener(this);
        }

        if (hasDecoration()) {
            mRecyclerView.addItemDecoration(initItemDecoration());
        }
        mRecyclerView.setItemAnim(false);
        mRecyclerView.setAdapter(mRExBaseAdapter);

        initScrollListener();
    }

    protected void initScrollListener() {
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
    protected RecyclerView.ItemDecoration initItemDecoration() {
        return createBaseItemDecoration();
    }

    protected RBaseItemDecoration createBaseItemDecoration() {
        return new RBaseItemDecoration(getItemDecorationHeight(), getItemDecorationColor())
                .setDrawLastLine(hasLastDecoration());
    }

    protected int getItemDecorationColor() {
        return Color.parseColor("#E0E0E0");
    }

    /**
     * 分割线的高度
     */
    protected int getItemDecorationHeight() {
        return mActivity.getResources().getDimensionPixelSize(R.dimen.base_hdpi);
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
        mRefreshLayout.addOnRefreshListener(this);
    }

    /**
     * 可以重写此方法, 实现自定义的布局
     */
    protected void inflateRecyclerRootLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        mRootSoftInputLayout = new RSoftInputLayout(mActivity);
        mRefreshLayout = new HnRefreshLayout(mActivity);
        mRecyclerView = new RRecyclerView(mActivity);
        mRefreshLayout.addView(mRecyclerView, new ViewGroup.LayoutParams(-1, -1));

        //mRootSoftInputLayout.addView(mRefreshLayout);
        //mRootSoftInputLayout.addView(new View(mActivity));
        //baseContentLayout.addView(mRootSoftInputLayout, new ViewGroup.LayoutParams(-1, -1));
        baseContentLayout.addView(mRefreshLayout, new ViewGroup.LayoutParams(-1, -1));
    }

    /**
     * 如果只是想添加自定义的覆盖层, 可以只重写此方法
     */
    protected void inflateOverlayLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        //空数据
        inflate(R.layout.layout_default_pager);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setFloating(true)
                .setTitleHide(true)
                .setShowBackImageView(true)
                .setTitleBarBGColor(Color.TRANSPARENT);
    }

    @Override
    public void onShowInPager(UIViewPager viewPager) {
        super.onShowInPager(viewPager);
        if (mIViewStatus == IViewShowState.STATE_VIEW_SHOW && isLoadInViewPager() && delayTime()) {
            loadData();
        }
    }

    @Override
    public void onHideInPager(UIViewPager viewPager) {
        super.onHideInPager(viewPager);
        if (isLoadInViewPager()) {
            onCancel();
        }
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        onCancel();
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
    protected boolean isLoadInViewPager() {
        return false;
    }

    protected abstract RExBaseAdapter<H, T, F> initRExBaseAdapter();

    public RExBaseAdapter<H, T, F> getRExBaseAdapter() {
        return mRExBaseAdapter;
    }

    /**
     * 重写此方法, 获取自定义布局中的RefreshLayout
     */
    public RefreshLayout getRefreshLayout() {
        if (mRefreshLayout == null && mViewHolder != null) {
            mRefreshLayout = mViewHolder.v(R.id.refresh_layout);
        }
        return mRefreshLayout;
    }

    /**
     * 重写此方法, 获取自定义布局中的RRecyclerView
     */
    public RRecyclerView getRecyclerView() {
        if (mRecyclerView == null && mViewHolder != null) {
            mRecyclerView = mViewHolder.v(R.id.recycler_view);
        }
        return mRecyclerView;
    }


    protected String getTitleString() {
        return "";
    }

    protected int getTitleResource() {
        return View.NO_ID;
    }

    /**
     * 是否有下一页
     */
    public boolean hasNext() {
        //return data_count > page * PAGE_SIZE;
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
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

    /**
     * 重新加载数据, 清空分页加载信息
     */
    public void loadData() {
        //mRExBaseAdapter.setNoMore();
        loadTime = System.currentTimeMillis();
        page = 1;
        last_id = "";
        hasNext = true;
        onCancel();//刷新数据,取消之前的加载更多请求
        if (mOnUIViewListener != null) {
            mOnUIViewListener.onViewLoadData(String.valueOf(page));
        }
        onUILoadData(getPage());
    }

    public void loadMoreData() {
        page++;
        if (mOnUIViewListener != null) {
            mOnUIViewListener.onViewLoadData(String.valueOf(page));
        }
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
        hideLoadView();

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
     * 当第一页返回的数据, 大于等于默认的页面数量时, 激活加载更多功能
     */
    public void onUILoadDataFinish(int dataSize) {
        if (1 == page && dataSize >= Constant.DEFAULT_PAGE_DATA_COUNT) {
            mRExBaseAdapter.setEnableLoadMore(true);
        }
        onUILoadDataFinish();
    }

    /**
     * 接口返回数据后,请调用此方法设置数据,
     * 请使用{@link #onUILoadDataEnd(List)}方法代替
     *
     * @param data_count 已经取消采用此字段判断是否加载更多了.
     */
    @Deprecated
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
            if (mOnUIViewListener != null) {
                mOnUIViewListener.onViewLoadDataSuccess();
            }
        }

        if (datas == null || datas.isEmpty()) {
            hasNext = false;
            if (mRExBaseAdapter.isEnableLoadMore()) {
                mRExBaseAdapter.setNoMore(true);
            }
        } else if (datas.size() >= Constant.DEFAULT_PAGE_DATA_COUNT && hasNext) {
            if (!mRExBaseAdapter.isEnableLoadMore()) {
                mRExBaseAdapter.setLoadMoreEnd();
            }
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

        onUILoadDataFinish();

        onEmptyData(isEmptyData);
    }

    public void onUILoadDataEnd(List<T> datas) {
        onUILoadDataEnd(datas, 0);
    }

    public void onUILoadDataEnd() {
        onUILoadDataEnd(null, data_count);
    }

    public void initConfigId(UserDiscussListBean listBean) {
        initConfigId(listBean, true);
    }

    public void initConfigId(UserDiscussListBean listBean, boolean showToast) {
        List<UserDiscussListBean.DataListBean> dataList = listBean.getData_list();
        if (dataList != null && !dataList.isEmpty()) {
            if (page <= 1) {
                first_id = dataList.get(0).getDiscuss_id();

                int dataCount = listBean.getData_count();
                if (showToast && dataCount != 0) {
                    T_.show(mActivity.getString(R.string.how_count_status_format, dataCount));
                }
            }
            last_id = dataList.get(dataList.size() - 1).getDiscuss_id();
        }
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

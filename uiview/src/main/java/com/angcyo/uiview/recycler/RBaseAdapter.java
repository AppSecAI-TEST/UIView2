package com.angcyo.uiview.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.angcyo.uiview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angcyo on 16-01-18-018.
 */
public abstract class RBaseAdapter<T> extends RecyclerView.Adapter<RBaseViewHolder> {

    protected List<T> mAllDatas;
    protected Context mContext;
    /**
     * 是否激活加载更多
     */
    protected boolean mEnableLoadMore = false;
    protected ILoadMore mLoadMore;
    protected OnAdapterLoadMoreListener mLoadMoreListener;
    /**
     * 当前加载状态
     */
    int mLoadState = ILoadMore.NORMAL;

    public RBaseAdapter(Context context) {
        mAllDatas = new ArrayList<>();
        this.mContext = context;
    }

    public RBaseAdapter(Context context, List<T> datas) {
        this.mAllDatas = datas == null ? new ArrayList<T>() : datas;
        this.mContext = context;
    }

    public RBaseAdapter setLoadMoreListener(OnAdapterLoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
        return this;
    }

    /**
     * 返回是否激活加载更多
     */
    public boolean isEnableLoadMore() {
        return mEnableLoadMore;
    }

    /**
     * 启用加载更多功能
     */
    public void setEnableLoadMore(boolean enableLoadMore) {
        boolean loadMore = mEnableLoadMore;
        mEnableLoadMore = enableLoadMore;

        if (enableLoadMore && !loadMore) {
            notifyItemInserted(getLastPosition());
        } else if (!enableLoadMore && loadMore) {
            notifyItemRemoved(getLastPosition());
        }
    }

    //--------------标准的方法-------------//

    @Override
    public int getItemViewType(int position) {
        if (mEnableLoadMore && isLast(position)) {
            return 666;
        }
        return getItemType(position);
    }

    @Override
    public RBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item;
        if (mEnableLoadMore && viewType == 666) {
            item = LayoutInflater.from(mContext)
                    .inflate(R.layout.base_item_load_more_layout, parent, false);
            mLoadMore = (ILoadMore) item;
        } else {
            int itemLayoutId = getItemLayoutId(viewType);
            if (itemLayoutId == 0) {
                item = createContentView(parent, viewType);
            } else {
                item = LayoutInflater.from(mContext).inflate(itemLayoutId, parent, false);
            }
        }
        return new RBaseViewHolder(item, viewType);
    }

    @Override
    public void onBindViewHolder(RBaseViewHolder holder, int position) {
        if (mEnableLoadMore && isLast(position)) {
            onBindLoadMore();
        } else {
            onBindView(holder, position, mAllDatas.size() > position ? mAllDatas.get(position) : null);
        }
    }


    @Override
    public void onViewAttachedToWindow(RBaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
//        L.w("onViewAttachedToWindow");
    }

    @Override
    public void onViewDetachedFromWindow(RBaseViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
//        L.w("onViewDetachedFromWindow");
    }

    private void onBindLoadMore() {
        if (mLoadState == ILoadMore.NORMAL
                || mLoadState == ILoadMore.LOAD_ERROR) {
            mLoadState = ILoadMore.LOAD_MORE;
            onLoadMore();
            if (mLoadMoreListener != null) {
                mLoadMoreListener.onAdapterLodeMore(this);
            }
        }
        updateLoadMoreView();
    }

    private void updateLoadMoreView() {
        if (mLoadMore != null) {
            mLoadMore.setLoadState(mLoadState);
        }
    }

    /**
     * 重写此方法, 实现加载更多功能
     */
    protected void onLoadMore() {

    }

    /**
     * 结束加载更多的标识, 方便下一次回调
     */
    public void setLoadMoreEnd() {
        mLoadState = ILoadMore.NORMAL;
        setEnableLoadMore(true);
        updateLoadMoreView();
    }

    public void setLoadError() {
        mLoadState = ILoadMore.LOAD_ERROR;
        setEnableLoadMore(true);
        updateLoadMoreView();
    }

    public void setNoMore() {
        mLoadState = ILoadMore.NO_MORE;
        setEnableLoadMore(true);
//        updateLoadMoreView();//不需要及时刷新
    }

    private boolean isLast(int position) {
        return position == getLastPosition();
    }

    private int getLastPosition() {
        return getItemCount() - 1;
    }

    /**
     * 根据position返回Item的类型.
     */
    public int getItemType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        int size = mAllDatas == null ? 0 : mAllDatas.size();
        if (mEnableLoadMore) {
            size += 1;
        }
        return size;
    }

    //--------------需要实现的方法------------//

    /**
     * 当 {@link #getItemLayoutId(int)} 返回0的时候, 会调用此方法
     */
    protected View createContentView(ViewGroup parent, int viewType) {
        return null;
    }

    protected abstract int getItemLayoutId(int viewType);

    protected abstract void onBindView(RBaseViewHolder holder, int position, T bean);

    //---------------滚动事件的处理--------------------//

    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
    }

    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    }

    //----------------Item 数据的操作-----------------//

    /**
     * 在最后的位置插入数据
     */
    public void addLastItem(T bean) {
        if (mAllDatas == null) {
            mAllDatas = new ArrayList<>();
        }
        int startPosition = mAllDatas.size();
        mAllDatas.add(bean);
        notifyItemInserted(startPosition);
        notifyItemRangeChanged(startPosition, getItemCount());
    }

    /**
     * 解决九宫格添加图片后,添加按钮消失的bug
     */
    public void addLastItemSafe(T bean) {
        if (mAllDatas == null) {
            mAllDatas = new ArrayList<>();
        }

        int startPosition = mAllDatas.size();
        mAllDatas.add(bean);
        int itemCount = getItemCount();
        if (itemCount > startPosition + 1) {
            notifyItemInserted(startPosition);
            notifyItemRangeChanged(startPosition, getItemCount());
        } else {
            notifyItemChanged(itemCount - 1);//
        }
    }

    public void addFirstItem(T bean) {
        if (mAllDatas == null) {
            mAllDatas = new ArrayList<>();
        }
        mAllDatas.add(0, bean);
        notifyItemInserted(0);
        notifyItemRangeChanged(0, getItemCount());
    }

    /**
     * delete item with object
     */
    public void deleteItem(T bean) {
        if (mAllDatas != null) {
            int size = getItemCount();
            int indexOf = mAllDatas.indexOf(bean);
            if (indexOf > -1) {
                if (onDeleteItem(bean)) {
                    mAllDatas.remove(bean);
                    notifyItemRemoved(indexOf);
                    notifyItemRangeChanged(indexOf, size - indexOf);
                }
            }
        }
    }

    public void deleteItem(int position) {
        if (mAllDatas != null) {
            int size = getItemCount();
            if (size > position) {
                mAllDatas.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, size - position);
            }
        }
    }

    /**
     * 是否可以删除bean
     */
    protected boolean onDeleteItem(T bean) {
        return true;
    }

    public void removeFirstItem() {
        mAllDatas.remove(0);
        notifyItemRemoved(0);
        notifyItemRangeChanged(0, getItemCount());
    }

    public void removeLastItem() {
        int last = mAllDatas.size() - 1;
        mAllDatas.remove(last);
        notifyItemRemoved(last);
        notifyItemRangeChanged(last, getItemCount());
    }

    /**
     * 重置数据
     */
    public void resetData(List<T> datas) {
        if (datas == null) {
            this.mAllDatas = new ArrayList<>();
        } else {
            this.mAllDatas = datas;
        }
        notifyItemRangeChanged(0, getItemCount());
    }

    /**
     * 追加数据
     */
    public void appendData(List<T> datas) {
        if (datas == null || datas.size() == 0) {
            return;
        }
        if (this.mAllDatas == null) {
            this.mAllDatas = new ArrayList<>();
        }
        int startPosition = this.mAllDatas.size();
        this.mAllDatas.addAll(datas);
        notifyItemRangeInserted(startPosition, datas.size());
        notifyItemRangeChanged(startPosition, getItemCount());
    }

    public List<T> getAllDatas() {
        return mAllDatas;
    }

    public interface OnAdapterLoadMoreListener {
        void onAdapterLodeMore(RBaseAdapter baseAdapter);
    }
}

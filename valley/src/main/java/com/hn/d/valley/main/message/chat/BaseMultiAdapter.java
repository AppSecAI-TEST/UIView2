package com.hn.d.valley.main.message.chat;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public abstract class BaseMultiAdapter<K extends RBaseViewHolder> extends BaseFetchAdapter<K> {


    /**
     * viewType->layoutResId
     */
    private SparseArray<Integer> layouts;

    /**
     * viewType->view holder class
     */
    private SparseArray<Class<? extends MsgViewHolderBase>> holderClasses;

    /**
     * viewType->view holder instance
     */
    private Map<Integer, Map<String, MsgViewHolderBase>> multiTypeViewHolders;

    public RBaseViewHolder mViewHolder;
    UIBaseView mUIBaseView;

    public BaseMultiAdapter(RecyclerView recyclerView, List<IMMessage> data,RBaseViewHolder viewHolder, UIBaseView uIBaseView) {
        super(recyclerView, 0, data);

        this.mViewHolder = viewHolder;
        this.mUIBaseView = uIBaseView;

    }


    protected abstract int getViewType(IMMessage item);


    protected abstract String getItemKey(IMMessage item);

    @Override
    protected int getDefItemViewType(int position) {
        return getViewType(mData.get(position));
    }

    @Override
    protected void convert(K baseHolder, IMMessage item, int position, boolean isScrolling) {
        final String key = getItemKey(item);
        final int viewType = baseHolder.getItemViewType();

        MsgViewHolderBase h = multiTypeViewHolders.get(viewType).get(key);
        if (h == null) {
            // build
            try {
                Class<? extends MsgViewHolderBase> cls = holderClasses.get(viewType);
                Constructor c = cls.getDeclaredConstructors()[0]; // 第一个显式的构造函数
                c.setAccessible(true);
                h = (MsgViewHolderBase) c.newInstance(new Object[]{this});
                h.setUIBaseView(mUIBaseView);
                h.setViewHolder(mViewHolder);
                multiTypeViewHolders.get(viewType).put(key, h);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // convert
        if (h != null) {
            h.convert(baseHolder, item, position, isScrolling);
        }
    }

    /**
     * add viewType->layoutResId, viewType->ViewHolder.class
     *
     * @param type            view type
     * @param layoutResId
     * @param viewHolderClass
     */
    protected void addItemType(int type, @LayoutRes int layoutResId, Class<? extends MsgViewHolderBase> viewHolderClass) {
        // layouts
        if (layouts == null) {
            layouts = new SparseArray<>();
        }
        layouts.put(type, layoutResId);

        // view holder class
        if (holderClasses == null) {
            holderClasses = new SparseArray<>();
        }
        holderClasses.put(type, viewHolderClass);

        // view holder
        if (multiTypeViewHolders == null) {
            multiTypeViewHolders = new HashMap<>();
        }
        multiTypeViewHolders.put(type, new HashMap<String, MsgViewHolderBase>());

    }

    @Override
    protected K onCreateDefViewHolder(ViewGroup parent, int viewType) {
        return createBaseViewHolder(parent, getLayoutId(viewType));
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    private int getLayoutId(int viewType) {
        return layouts.get(viewType);
    }

    public void resetData(List<IMMessage> datas) {

        mData.clear();
        mData.addAll(datas);
        notifyDataSetChanged();

    }

    /**
     * 在最后的位置插入数据
     */
    public void addLastItem(IMMessage bean) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        int startPosition = mData.size();
        mData.add(bean);
        notifyItemInserted(startPosition);
        notifyItemRangeChanged(startPosition, getItemCount());
    }

    /**
     * 追加数据
     */
    public void appendData(List<IMMessage> datas) {
        if (datas == null || datas.size() == 0) {
            return;
        }
        if (this.mData == null) {
            this.mData = new ArrayList<>();
        }
        int startPosition = this.mData.size();
        this.mData.addAll(datas);
        notifyItemRangeInserted(startPosition, datas.size());
        notifyItemRangeChanged(startPosition, getItemCount());
    }

    public List<IMMessage> getAllDatas() {
        return mData;
    }

    public void fetchMoreComplete(RecyclerView recyclerView ,List<IMMessage> messages) {

        addFrontData(messages);

        fetchMoreComplete(recyclerView,messages.size());

    }

    public void addFrontData(List<IMMessage> data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        mData.addAll(0, data);
        notifyItemRangeInserted(0, data.size()); // add到FetchMoreView之下，保持FetchMoreView在顶部
    }
}

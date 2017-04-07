package com.hn.d.valley.main.message.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

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
public class BaseFetchAdapter<K extends BaseViewHolder> extends RecyclerView.Adapter<K> {


    @Override
    public K onCreateViewHolder(ViewGroup parent, int viewType) {

        K viewHolder = onCreateDefViewHolder(parent,viewType);



        return viewHolder;
    }

    protected K onCreateDefViewHolder(ViewGroup parent, int viewType) {
        return createBaseViewHolder(parent, viewType);
    }

    protected K createBaseViewHolder(View view,int viewType) {
        return (K) new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(K holder, int position) {



    }

    @Override
    public int getItemCount() {
        return 0;
    }
}

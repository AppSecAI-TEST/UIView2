package com.hn.d.valley.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.angcyo.uiview.github.luban.Luban;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.hn.d.valley.R;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/02/20 14:14
 * 修改人员：Robi
 * 修改时间：2017/02/20 14:14
 * 修改备注：
 * Version: 1.0.0
 */
public class HnAddImageAdapter extends HnGlideImageAdapter {

    public static final int TYPE_ADD_ITEM = 1111;
    public static final int TYPE_NODATA_ITEM = 1112;

    /**
     * 是否是添加模式
     */
    boolean isAddModel = true;
    /**
     * 激活删除模式
     */
    boolean isDeleteModel = false;
    OnAddListener mAddListener;
    int maxItemCount = 6;
    int itemHeight = 0;

    public HnAddImageAdapter(Context context) {
        super(context);
    }

    public HnAddImageAdapter(Context context, List<Luban.ImageItem> datas) {
        super(context, datas);
    }

    @Override
    public int getItemCount() {
        int count = super.getItemCount();
//        return Math.min(maxItemCount, isAddModel ? count + 1 : count);
        if (getAllDatas().size() == 0) {
            return 1;
        }
        return count;
    }

    @Override
    public int getItemType(int position) {
        int size = getAllDatas().size();
//        if (isAddModel && size != maxItemCount && isLast(position)) {
//            return TYPE_ADD_ITEM;
//        }

        if (size == 0) {
            return TYPE_NODATA_ITEM;
        }

        return super.getItemType(position);
    }

    public boolean isMax(int position) {
        return maxItemCount - 1 == position;
    }

    @Override
    protected int getItemLayoutId(int viewType) {

        if (viewType == TYPE_NODATA_ITEM) {
            return R.layout.item_profile_wallpaper_desc;
        }

        return super.getItemLayoutId(viewType);
    }

    @Override
    protected void onBindView(RBaseViewHolder holder, final int position, final Luban.ImageItem bean) {

        if (TYPE_NODATA_ITEM == getItemType(position)) {

            return;
        }

        if (itemHeight != 0) {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.height = itemHeight;
            holder.itemView.setLayoutParams(layoutParams);
        }

        ImageView imageView = holder.imgV(R.id.image_view);
        ImageView deleteView = holder.imgV(R.id.delete_view);

//        if (isAddModel && bean == null && isLast(position)) {
//            imageView.setImageResource(R.drawable.add_bianjizhiliao);
//            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//            imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mAddListener != null) {
//                        mAddListener.onAddClick(v, position, bean);
//                    }
//                }
//            });
//            deleteView.setVisibility(View.GONE);
//        } else {
            super.onBindView(holder, position, bean);
            deleteView.setVisibility(isDeleteModel ? View.VISIBLE : View.GONE);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mAddListener != null) {
                        mAddListener.onItemClick(v, position, bean);
                    }
                }
            });
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mAddListener != null) {
                        mAddListener.onItemLongClick(v, position, bean);
                    }
                    return true;
                }
            });
            deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mAddListener != null) {
                        mAddListener.onDeleteClick(v, position, bean);
                    }
                }
            });
//        }
    }

    public void setAddListener(OnAddListener addListener) {
        mAddListener = addListener;
    }

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
    }

    public void setAddModel(boolean addModel) {
        isAddModel = addModel;
        notifyDataSetChanged();
    }

    public void setDeleteModel(RecyclerView recyclerView, boolean deleteModel) {
        isDeleteModel = deleteModel;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            for (int i = 0; i < layoutManager.getChildCount(); i++) {
                RBaseViewHolder vh = (RBaseViewHolder) recyclerView.findViewHolderForAdapterPosition(firstVisibleItemPosition + i);
                if (vh != null) {
                    final View view = vh.v(R.id.delete_view);
                    view.setVisibility(deleteModel && vh.getItemViewType() != TYPE_ADD_ITEM ?
                            View.VISIBLE : View.GONE);
                }
            }
        }
    }

    public void setDeleteModel(RecyclerView recyclerView) {
        setDeleteModel(recyclerView, !isDeleteModel);
    }

    public void setMaxItemCount(int maxItemCount) {
        this.maxItemCount = maxItemCount;
    }

    public interface OnAddListener {
        void onAddClick(View view, int position, Luban.ImageItem item);

        void onItemClick(View view, int position, Luban.ImageItem item);

        void onItemLongClick(View view, int position, Luban.ImageItem item);

        void onDeleteClick(View view, int position, Luban.ImageItem item);
    }
}

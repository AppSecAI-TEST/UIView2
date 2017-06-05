package com.hn.d.valley.sub.adapter;

import android.content.Context;
import android.view.View;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RBaseAdapter;
import com.angcyo.uiview.widget.RImageView;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.hn.d.valley.R;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/04/10 16:38
 * 修改人员：Robi
 * 修改时间：2017/04/10 16:38
 * 修改备注：
 * Version: 1.0.0
 */
public class ImageAdapter extends RBaseAdapter<String> {
    ImageAdapterConfig mImageAdapterConfig;

    public ImageAdapter(Context context, ImageAdapterConfig adapterConfig) {
        super(context, adapterConfig.getDatas());
        mImageAdapterConfig = adapterConfig;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return mImageAdapterConfig.getItemLayoutId(viewType);
    }

    @Override
    public int getItemCount() {
        return mImageAdapterConfig.getItemCount();
    }

    @Override
    public int getItemType(int position) {
        return mImageAdapterConfig.getItemType(position);
    }

    @Override
    protected void onBindView(final RBaseViewHolder holder, final int position, final String bean) {
        if (!mImageAdapterConfig.onBindView(holder, position, bean)) {
            final RImageView imageView = holder.v(R.id.image_view);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mImageAdapterConfig.onItemClick(holder, imageView, position, bean);
                }
            });

            DrawableRequestBuilder<String> builder = Glide.with(mContext)
                    .load(bean)
                    .centerCrop();
            builder.placeholder(R.drawable.zhanweitu_1);
            builder.into(imageView);
        }
    }

    public static abstract class ImageAdapterConfig {
        public abstract List<String> getDatas();

        public int getItemLayoutId(int viewType) {
            return R.layout.item_single_image_view;
        }

        public int getItemCount() {
            int size = getDatas() == null ? 0 : getDatas().size();
            return size;
        }

        public int getItemType(int position) {
            return 1;
        }

        public boolean onBindView(RBaseViewHolder holder, int position, String bean) {
            return false;
        }

        public void onItemClick(RBaseViewHolder holder, RImageView view, int position, String bean) {

        }
    }
}

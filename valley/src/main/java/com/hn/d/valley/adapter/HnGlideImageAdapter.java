package com.hn.d.valley.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.angcyo.uiview.github.luban.Luban;
import com.angcyo.uiview.recycler.RBaseAdapter;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.bumptech.glide.Glide;
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
public class HnGlideImageAdapter extends RBaseAdapter<Luban.ImageItem> {

    public HnGlideImageAdapter(Context context) {
        super(context);
    }

    public HnGlideImageAdapter(Context context, List<Luban.ImageItem> datas) {
        super(context, datas);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_image_layout;
    }

    @Override
    protected void onBindView(RBaseViewHolder holder, int position, final Luban.ImageItem bean) {
        if (bean == null) {
            return;
        }
        final ImageView imageView = holder.imgV(R.id.image_view);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.post(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(bean.url)) {
                    Glide.with(mContext)
                            .load(bean.thumbPath)
                            .override(imageView.getMeasuredWidth(), imageView.getMeasuredHeight())
                            .placeholder(R.drawable.zhanweitu_1)
                            .into(imageView);
                } else {
                    Glide.with(mContext)
                            .load(bean.url)
                            .override(imageView.getMeasuredWidth(), imageView.getMeasuredHeight())
                            .placeholder(R.drawable.zhanweitu_1)
                            .into(imageView);
                }
            }
        });
    }
}

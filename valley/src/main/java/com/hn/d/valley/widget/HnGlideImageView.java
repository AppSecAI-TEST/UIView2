package com.hn.d.valley.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.angcyo.library.glide.GlideCircleTransform;
import com.bumptech.glide.Glide;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/02/22 16:15
 * 修改人员：Robi
 * 修改时间：2017/02/22 16:15
 * 修改备注：
 * Version: 1.0.0
 */
public class HnGlideImageView extends ImageView {
    public HnGlideImageView(Context context) {
        super(context);
    }

    public HnGlideImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        setImageResource(R.drawable.zhanweitu_1);
    }

    public void setImageUrl(final String url) {
        CharSequence description = getContentDescription();
        if (description != null && description.toString().contains("circle")) {
            if (getMeasuredWidth() == 0 || getMeasuredHeight() == 0) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getContext())
                                .load(url)
                                //.override(getMeasuredWidth(), getMeasuredHeight())
                                .transform(new GlideCircleTransform(getContext()))
                                .into(HnGlideImageView.this);
                    }
                });
            }
        } else {
            if (getMeasuredWidth() == 0 || getMeasuredHeight() == 0) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getContext())
                                .load(url)
                                //.override(getMeasuredWidth(), getMeasuredHeight())
                                .into(HnGlideImageView.this);
                    }
                });
            }
        }
    }
}

package com.hn.d.valley.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.angcyo.library.glide.GlideCircleTransform;
import com.angcyo.uiview.utils.BmpUtil;
import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hn.d.valley.base.oss.OssHelper;

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

    public void setImageThumbUrl(final String url) {
        CharSequence description = getContentDescription();
        if (description != null && description.toString().contains("circle")) {
            post(new Runnable() {
                @Override
                public void run() {
                    DrawableRequestBuilder<String> builder = Glide.with(getContext())
                            .load(OssHelper.getImageThumb(url, getMeasuredWidth(), getMeasuredHeight()))
                            .transform(new GlideCircleTransform(getContext()));
                    if (getDrawable() != null) {
                        builder.placeholder(getDrawable());
                    }
                    builder.into(HnGlideImageView.this);
                }
            });
        } else if (description != null && description.toString().contains("circle2")) {
            post(new Runnable() {
                @Override
                public void run() {
                    BitmapTypeRequest<String> builder = Glide.with(getContext())
                            .load(OssHelper.getImageThumb(url, getMeasuredWidth(), getMeasuredHeight()))
                            .asBitmap();
                    if (getDrawable() != null) {
                        builder.placeholder(getDrawable());
                    }
                    builder.into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            setImageBitmap(BmpUtil.getRoundedCornerBitmap(resource, Math.max(getMeasuredWidth(), getMeasuredHeight())));
                        }
                    });
                }
            });
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    DrawableRequestBuilder<String> builder = Glide.with(getContext())
                            .load(OssHelper.getImageThumb(url, getMeasuredWidth(), getMeasuredHeight()));
                    if (getDrawable() != null) {
                        builder.placeholder(getDrawable());
                    }
                    builder.into(HnGlideImageView.this);
                }
            });
        }
    }

    public void setImageUrl(final String url) {
        CharSequence description = getContentDescription();
        if (description != null && description.toString().contains("circle")) {
            post(new Runnable() {
                @Override
                public void run() {
                    DrawableRequestBuilder<String> builder = Glide.with(getContext())
                            .load(url)
                            .transform(new GlideCircleTransform(getContext()));
                    if (getDrawable() != null) {
                        builder.placeholder(getDrawable());
                    }
                    builder.into(HnGlideImageView.this);
                }
            });
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    DrawableRequestBuilder<String> builder = Glide.with(getContext())
                            .load(url);
                    if (getDrawable() != null) {
                        builder.placeholder(getDrawable());
                    }
                    builder.into(HnGlideImageView.this);
                }
            });
        }
    }
}

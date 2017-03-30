package com.hn.d.valley.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.angcyo.library.glide.GlideCircleTransform;
import com.angcyo.uiview.utils.BmpUtil;
import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hn.d.valley.R;
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
public class HnGlideImageView extends AppCompatImageView {
    Drawable authDrawable;
    boolean isAuth;
    boolean isAttached;

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
        isAttached = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttached = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isAuth) {
            if (authDrawable == null) {
                authDrawable = ContextCompat.getDrawable(getContext(), R.drawable.renzheng_icon);
            }
            int left = getMeasuredWidth() - getPaddingRight() -/* (getMeasuredWidth() - getPaddingLeft() - getPaddingRight()) / 4 - */authDrawable.getIntrinsicWidth();
            int top = getMeasuredHeight() - getPaddingBottom() - /*(getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) / 4 -*/ authDrawable.getIntrinsicHeight();
            authDrawable.setBounds(left, top, left + authDrawable.getIntrinsicWidth(), top + authDrawable.getIntrinsicHeight());
            authDrawable.draw(canvas);
        }
    }

    /**
     * 设置是否显示认证图片
     */
    public void setAuth(boolean auth) {
        isAuth = auth;
        if (isAttached) {
            postInvalidate();
        }
    }

    public void setImageThumbUrl(final String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        CharSequence description = getContentDescription();
        if (description != null && description.toString().contains("circle2")) {
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
        } else if (description != null && description.toString().contains("circle")) {
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
        if (TextUtils.isEmpty(url)) {
            return;
        }

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

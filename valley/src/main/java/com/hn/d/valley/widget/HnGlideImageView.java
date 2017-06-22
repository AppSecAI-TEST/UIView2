package com.hn.d.valley.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.angcyo.library.glide.GlideCircleTransform;
import com.angcyo.uiview.utils.BmpUtil;
import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hn.d.valley.R;
import com.hn.d.valley.base.oss.OssHelper;

import java.io.File;
import java.io.Serializable;

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
    boolean isAuth;//是否显示认证图标
    boolean isAttached;
    Paint mPaint;
    private boolean showBorder;//是否显示白色边框

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
    public void draw(Canvas canvas) {
        try {
            super.draw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (showBorder) {
            int width = getResources().getDimensionPixelOffset(R.dimen.base_line);
            if (mPaint == null) {
                mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mPaint.setColor(Color.WHITE);
                mPaint.setStrokeWidth(width);
                mPaint.setStyle(Paint.Style.STROKE);
            }
            float cx, cy, radius;
            radius = Math.min((getMeasuredWidth() - getPaddingRight() - getPaddingLeft()) / 2,
                    (getMeasuredHeight() - getPaddingBottom() - getPaddingTop()) / 2);
            cx = getPaddingLeft() + radius;
            cy = getPaddingTop() + radius;
            canvas.drawCircle(cx, cy, radius, mPaint);
        }
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

    public void setAuth(String is_auth) {
        setAuth("1".equalsIgnoreCase(is_auth));
    }

    public void setShowBorder(boolean showBorder) {
        this.showBorder = showBorder;
        if (isAttached) {
            postInvalidate();
        }
    }

    public void setImageThumbUrl(final String url) {
        setImageThumbUrl(url, true);
    }

    public void setImageThumbUrl(final String url, final boolean anim) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        CharSequence description = getContentDescription();
        if (description != null && description.toString().contains("circle2")) {
            post(new Runnable() {
                @Override
                public void run() {
                    if (isActivityDestroy()) {
                        return;
                    }
                    BitmapTypeRequest<String> builder = Glide.with(getContext())
                            .load(OssHelper.getImageThumb(url, getMeasuredWidth(), getMeasuredHeight()))
                            .asBitmap();
                    if (!anim) {
                        builder.dontAnimate();
                    }
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
                    if (isActivityDestroy()) {
                        return;
                    }
                    DrawableRequestBuilder<String> builder = Glide.with(getContext())
                            .load(OssHelper.getImageThumb(url, getMeasuredWidth(), getMeasuredHeight()))
                            .transform(new GlideCircleTransform(getContext()));
                    if (!anim) {
                        builder.dontAnimate();
                    }
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
                    if (isActivityDestroy()) {
                        return;
                    }
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
        setImageUrl(url, true);
    }

    public void setImageUrl(final String url, final boolean thumb) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        CharSequence description = getContentDescription();
        if (description != null && description.toString().contains("circle")) {
            post(new Runnable() {
                @Override
                public void run() {
                    if (isActivityDestroy()) {
                        return;
                    }
                    File file = new File(url);
                    DrawableRequestBuilder<? extends Serializable> builder = Glide.with(getContext())
                            .load(file.exists() ? file : url)
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
                    if (isActivityDestroy()) {
                        return;
                    }
                    File file = new File(url);
                    DrawableTypeRequest<? extends Serializable> builder = Glide.with(getContext())
                            .load(file.exists() ? file :
                                    (thumb ? OssHelper.getImageThumb(url, getMeasuredWidth(), getMeasuredHeight()) : url));
                    if (getDrawable() != null) {
                        builder.placeholder(getDrawable());
                    }
                    builder.into(HnGlideImageView.this);
                }
            });
        }
    }

    public Drawable copyDrawable() {
        Drawable result = null, drawable = getDrawable();
        if (drawable != null) {
            Drawable.ConstantState constantState = drawable.mutate().getConstantState();
            if (constantState != null) {
                result = constantState.newDrawable();
            }
        }
        return result;
    }

    private boolean isActivityDestroy() {
        Context context = getContext();
        if (context instanceof Activity) {
            return ((Activity) context).isDestroyed();
        }
        return false;
    }
}

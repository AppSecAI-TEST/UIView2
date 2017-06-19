package com.hn.d.valley.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.hn.d.valley.R;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/27 16:35
 * 修改人员：Robi
 * 修改时间：2016/12/27 16:35
 * 修改备注：
 * Version: 1.0.0
 */
public class HnRefreshLayout extends RefreshLayout {
    public HnRefreshLayout(Context context) {
        super(context);
    }

    public HnRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initRefreshView() {
        super.initRefreshView();
        if (!isInEditMode()) {
            setTopView(new HnTopView(getContext()));
        }
    }

    private class HnTopView extends View implements RefreshLayout.OnTopViewMoveListener {

        public HnTopView(Context context) {
            super(context);
            setBackgroundResource(R.drawable.refresh_animation_list2);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int size = (int) ResUtil.dpToPx(getResources(), 60);
            setMeasuredDimension(size, size);
        }

        @Override
        public void setBackgroundDrawable(Drawable background) {
            super.setBackgroundDrawable(background);
        }

        @Override
        public void setBackground(Drawable background) {
            super.setBackground(background);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
        }

        @Override
        public void draw(Canvas canvas) {
            Drawable background = getBackground();
            if (background != null) {
                int cx = getMeasuredWidth() / 2;
                int cy = getMeasuredHeight() / 2;
                int w = background.getIntrinsicWidth() / 2;
                int h = background.getIntrinsicHeight() / 2;
                background.setBounds(cx - w, cy - h, cx + w, cy + h);
            }
            super.draw(canvas);
        }

        @Override
        public void onTopMoveTo(View view, int top, int maxHeight, @State int state) {
            if (state == TOP) {
                Drawable background = getBackground();
                if (background instanceof AnimationDrawable) {
                    ((AnimationDrawable) background).start();
                }
            } else {
                Drawable background = getBackground();
                if (background instanceof AnimationDrawable) {
                    ((AnimationDrawable) background).stop();
                }
            }
        }
    }
}

package com.hn.d.valley.base.inner;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.angcyo.uiview.container.SwipeBackLayout;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/28 16:43
 * 修改人员：Robi
 * 修改时间：2016/12/28 16:43
 * 修改备注：
 * Version: 1.0.0
 */
public class RRootLayout extends SwipeBackLayout {
    protected OnSwipeChangeListener mOnSwipeListener;
    /**
     * 如果只剩下最后一个View, 是否激活滑动删除
     */
    private boolean enableRootSwipe = true;
    private float mTranslationOffsetX;

    public RRootLayout(Context context) {
        super(context);
    }

    public RRootLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RRootLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RRootLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected boolean canTryCaptureView(View child) {
        int count = getChildCount();
        if (count > 1) {
            return child == getChildAt(count - 1);
        }
        return enableRootSwipe;
    }

    @Override
    protected void onRequestClose() {
        super.onRequestClose();
        //需要关闭
        if (mOnSwipeListener != null) {
            mOnSwipeListener.onSwipeClose();
        }
    }

    @Override
    protected void onRequestOpened() {
        super.onRequestOpened();
        if (mOnSwipeListener != null) {
            mOnSwipeListener.onSwipeStart();
        }
        translation(0);
    }

    @Override
    protected void onSlideChange(float percent) {
        super.onSlideChange(percent);
        if (mOnSwipeListener != null) {
            mOnSwipeListener.onSwipeTo(percent);
        }
        translation(percent);
    }

    @Override
    protected void onStateDragging() {
        super.onStateDragging();

        if (mOnSwipeListener != null) {
            mOnSwipeListener.onSwipeStartDragging();
        }

        //开始偏移时, 偏移的距离
        final View view = getLastSwipeView();
        if (view != null) {
            mTranslationOffsetX = getMeasuredWidth() * 0.3f;
            view.setTranslationX(-mTranslationOffsetX);
        }
    }

    private void translation(float percent) {
        final View view = getLastSwipeView();
        if (view != null) {
            view.setVisibility(VISIBLE);
            view.setTranslationX(-mTranslationOffsetX * percent);
        }
    }

    private View getLastSwipeView() {
        int childCount = getChildCount();
        if (childCount > 1) {
            return getChildAt(childCount - 2);
        }
        return null;
    }

    public void setOnSwipeListener(OnSwipeChangeListener onSwipeListener) {
        mOnSwipeListener = onSwipeListener;
    }

    public interface OnSwipeChangeListener {
        //触发滑动返回
        void onSwipeClose();

        //恢复默认状态
        void onSwipeStart();

        //开始滑动
        void onSwipeStartDragging();

        //滑动比例
        void onSwipeTo(float percent);
    }
}

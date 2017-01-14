package com.hn.d.valley.base.iview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.resources.AnimUtil;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.view.UIIViewImpl;
import com.lzy.imagepicker.adapter.ImagePageAdapter;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.view.ViewPagerFixed;

import java.util.ArrayList;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/04 15:22
 * 修改人员：Robi
 * 修改时间：2017/01/04 15:22
 * 修改备注：
 * Version: 1.0.0
 */
public class ImagePagerUIView extends UIIViewImpl {

    //动画开始 坐标x,y 和宽高
    protected int mStartX, mStartY, mStartW, mStartH;
    private ArrayList<ImageItem> mImageItems;
    private ViewPagerFixed mMViewPager;
    private PinchCircleIndicator mMCircleIndicator;
    private boolean isToFinish;
    private RelativeLayout mMRootLayout;
    private int startPosition = 0;
    private ValueAnimator mValueAnimator;

    private ImagePagerUIView(ArrayList<ImageItem> imageItems, int startPosition) {
        mImageItems = imageItems;
        this.startPosition = startPosition;
    }

    public static void start(ILayout iLayout, final View view, ArrayList<ImageItem> imageItems, int startPosition) {
        if (iLayout == null) {
            return;
        }

        int[] rt = new int[2];
        view.getLocationOnScreen(rt);

        float w = view.getMeasuredWidth();
        float h = view.getMeasuredHeight();

        float x = rt[0] + w / 2;
        float y = rt[1] + h / 2;

        final ImagePagerUIView imagePagerUIView = new ImagePagerUIView(imageItems, startPosition);
        imagePagerUIView.mStartX = rt[0];
        imagePagerUIView.mStartY = rt[1];
        imagePagerUIView.mStartW = (int) w;
        imagePagerUIView.mStartH = (int) h;
        iLayout.startIView(imagePagerUIView, new UIParam(false).setHideLastIView(false));
    }

    @Override
    public void onViewCreate() {
        super.onViewCreate();
        ImagePageAdapter mAdapter = new ImagePageAdapter(mActivity, mImageItems);
        mMViewPager.setAdapter(mAdapter);
        mMCircleIndicator.setViewPager(mMViewPager);
        mAdapter.setPhotoViewClickListener(new ImagePageAdapter.PhotoViewClickListener() {
            @Override
            public void OnPhotoTapListener(View view, float v, float v1) {
                animToFinish();
            }
        });
        mMViewPager.setCurrentItem(startPosition);
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        startAnimation();
        final View decorView = mActivity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
            mValueAnimator = null;
        }
        final View decorView = mActivity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    protected View inflateBaseView(FrameLayout container, LayoutInflater inflater) {
        mMRootLayout = new RelativeLayout(mActivity);
        mMViewPager = new ViewPagerFixed(mActivity);
        mMCircleIndicator = new PinchCircleIndicator(mActivity);

        RelativeLayout.LayoutParams indicatorParams = new RelativeLayout.LayoutParams(-2, -2);
        indicatorParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        indicatorParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        indicatorParams.setMargins(0, 0, 0, (int) ResUtil.dpToPx(mActivity.getResources(), 50));

        mMRootLayout.addView(mMViewPager, new ViewGroup.LayoutParams(-1, -1));
        mMRootLayout.addView(mMCircleIndicator, indicatorParams);

        container.addView(mMRootLayout, new ViewGroup.LayoutParams(-1, -1));
        return container;
    }

    private void animToFinish() {
        if (isToFinish) {
            return;
        }
        isToFinish = true;
        mMCircleIndicator.setVisibility(View.GONE);
        mMCircleIndicator.setAlpha(0);
        AnimUtil.startArgb(mMRootLayout, Color.BLACK, Color.TRANSPARENT, UIIViewImpl.DEFAULT_ANIM_TIME);
        ViewCompat.animate(mMViewPager).alpha(0).scaleX(0.2f).scaleY(0.2f)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(UIIViewImpl.DEFAULT_ANIM_TIME)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        isToFinish = false;
                        finishIView(ImagePagerUIView.this, false);
                    }
                }).start();
    }

    private void startAnimation() {
        mMCircleIndicator.setVisibility(View.GONE);
        mValueAnimator = AnimUtil.startArgb(mMRootLayout, Color.TRANSPARENT, Color.BLACK, UIIViewImpl.DEFAULT_ANIM_TIME);
        final int screenWidth = ResUtil.getScreenWidth(mActivity);
        final int screenHeight = ResUtil.getScreenHeight(mActivity);
        mMViewPager.setX(mStartX + mStartW / 2 - screenWidth / 2);
        mMViewPager.setY(mStartY + mStartH / 2 - screenHeight / 2);
        mMViewPager.setScaleX((mStartW + 0f) / screenWidth);
        mMViewPager.setScaleY((mStartH + 0f) / screenHeight);
        mMViewPager.animate().x(0).y(0).scaleX(1).scaleY(1).setInterpolator(new DecelerateInterpolator()).setDuration(UIIViewImpl.DEFAULT_ANIM_TIME).start();
        mMViewPager.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mImageItems.size() > 1) {
                    mMCircleIndicator.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

}

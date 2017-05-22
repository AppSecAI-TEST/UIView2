package com.hn.d.valley.base.iview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.angcyo.library.widget.DragPhotoView;
import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.resources.AnimUtil;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.UI;
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
    private int mLastTranColor = Color.BLACK;

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
        iLayout.startIView(imagePagerUIView, new UIParam(true).setHideLastIView(true /*false*/));
    }

    @Override
    public void onViewCreate(View rootView) {
        super.onViewCreate(rootView);
        ImagePageAdapter mAdapter = new ImagePageAdapter(mActivity, mImageItems);
        mMViewPager.setAdapter(mAdapter);
        mAdapter.setOnExitListener(new DragPhotoView.OnExitListener() {
            @Override
            public void onMoveTo(DragPhotoView view, float w, float h, float translateX, float translateY) {
                showLastViewPattern();
                onMoveExitCancelTo(view, w, h, translateX, translateY);
            }

            @Override
            public void onExit(DragPhotoView view, float translateX, float translateY, float w, float h) {
                animToFinish();
            }

            @Override
            public void onMoveExitCancel(DragPhotoView view) {
                if (!isToFinish) {
                    hideLastViewPattern();
                }
            }

            @Override
            public void onMoveExitCancelTo(DragPhotoView view, float w, float h, float translateX, float translateY) {
                mLastTranColor = SkinHelper.getTranColor(Color.BLACK, 255 - (int) (255 * (translateY * 2 / h)));
                mMRootLayout.setBackgroundColor(mLastTranColor);
            }
        });
        mAdapter.setPhotoViewClickListener(new ImagePageAdapter.PhotoViewClickListener() {
            @Override
            public void OnPhotoTapListener(View view, float v, float v1) {
                animToFinish();
            }
        });
        mMCircleIndicator.setViewPager(mMViewPager);
        mMViewPager.setCurrentItem(startPosition);
    }

    @Override
    public boolean onBackPressed() {
        animToFinish();
        finishIView(this, new UIParam(true, true, false));
        return false;
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        startAnimation();
        //startAnimation2();
        fullscreen(true, true);
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
            mValueAnimator = null;
        }
        fullscreen(false, true);
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

        mMRootLayout.setClickable(true);
        container.addView(mMRootLayout, new ViewGroup.LayoutParams(-1, -1));
        return container;
    }

    private void animToFinish() {
        if (isToFinish) {
            return;
        }
        showLastViewPattern();

        isToFinish = true;
        mMCircleIndicator.setVisibility(View.GONE);
        mMCircleIndicator.setAlpha(0);
        AnimUtil.startArgb(mMRootLayout, mLastTranColor, Color.TRANSPARENT, UIIViewImpl.DEFAULT_ANIM_TIME);
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

    private void showLastViewPattern() {
        try {
            getILayout().getViewPatternAtLast(1).mView.setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }
    }

    private void hideLastViewPattern() {
        try {
            getILayout().getViewPatternAtLast(1).mView.setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }

    @Deprecated
    private void startAnimation() {
        mMCircleIndicator.setVisibility(View.GONE);
        mValueAnimator = AnimUtil.startArgb(mMRootLayout, Color.TRANSPARENT, Color.BLACK, UIIViewImpl.DEFAULT_ANIM_TIME);
        final int screenWidth = ResUtil.getScreenWidth(mActivity);
        final int screenHeight = ResUtil.getScreenHeight(mActivity);
        mMViewPager.setX(mStartX + mStartW / 2 - screenWidth / 2);
        mMViewPager.setY(mStartY + mStartH / 2 - screenHeight / 2);
        mMViewPager.setScaleX((mStartW + 0f) / screenWidth);
        mMViewPager.setScaleY((mStartH + 0f) / screenHeight);
        final ViewPropertyAnimator viewPropertyAnimator = mMViewPager.animate().x(0).y(0)
                .scaleX(1).scaleY(1)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(UIIViewImpl.DEFAULT_ANIM_TIME);
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

        mMViewPager.post(new Runnable() {
            @Override
            public void run() {
                viewPropertyAnimator.start();
            }
        });
    }

    private void startAnimation2() {
        mMCircleIndicator.setVisibility(View.GONE);
        mValueAnimator = AnimUtil.startArgb(mMRootLayout, Color.TRANSPARENT, Color.BLACK, UIIViewImpl.DEFAULT_ANIM_TIME);
        final int screenWidth = ResUtil.getScreenWidth(mActivity);
        final int screenHeight = ResUtil.getScreenHeight(mActivity);

        ViewCompat.setX(mMViewPager, mStartX);
        ViewCompat.setY(mMViewPager, mStartY);

        UI.setView(mMViewPager, mStartW, mStartH);

        ViewCompat.animate(mMViewPager)
                .setDuration(1000)
                .x(screenWidth / 2 - mStartW / 2)
                .y(screenHeight / 2 - mStartH / 2)
                .scaleX(screenWidth * 1.f / mStartW)
                .scaleY(screenHeight * 1.f / mStartH)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        if (mImageItems.size() > 1) {
                            mMCircleIndicator.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .start();
    }

    @Override
    public Animation loadStartAnimation() {
        return UIBaseView.createClipEnterAnim(1f);
    }

    @Override
    public Animation loadFinishAnimation() {
        return UIBaseView.createClipExitAnim(1f);
    }

    @Override
    public Animation loadOtherEnterAnimation() {
        return UIBaseView.createClipEnterAnim(1f);
    }

    @Override
    public Animation loadOtherExitAnimation() {
        return UIBaseView.createClipExitAnim(1f);
    }
}

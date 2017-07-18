package com.hn.d.valley.base.iview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
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
import com.angcyo.uiview.Root;
import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.dialog.UIBottomItemDialog;
import com.angcyo.uiview.github.utilcode.utils.FileUtils;
import com.angcyo.uiview.resources.AnimUtil;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.UI;
import com.angcyo.uiview.view.UIIViewImpl;
import com.angcyo.uiview.viewgroup.RRelativeLayout;
import com.angcyo.uiview.widget.viewpager.TextIndicator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.github.chrisbanes.photoview.PhotoView;
import com.hn.d.valley.R;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.adapter.ImagePageAdapter;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.view.ViewPagerFixed;

import java.io.File;
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
    IndicatorStyle mIndicatorStyle = IndicatorStyle.CIRCLE;
    ImagePageAdapter.PhotoViewLongClickListener photoViewLongClickListener = null;
    private ArrayList<ImageItem> mImageItems;
    private ViewPagerFixed mMViewPager;
    private PinchCircleIndicator mMCircleIndicator;
    private boolean isToFinish;
    private RRelativeLayout mMRootLayout;
    private int startPosition = 0;
    private ValueAnimator mValueAnimator;
    private int mLastTranColor = Color.BLACK;
    private IViewConfigCallback mIViewConfigCallback = new IViewConfigCallback() {
    };
    private ImagePageAdapter mImagePageAdapter;
    private TextIndicator mTextIndicator;

    private ImagePagerUIView(ArrayList<ImageItem> imageItems, int startPosition) {
        mImageItems = imageItems;
        this.startPosition = startPosition;
    }

    public static ImagePagerUIView start(ILayout iLayout, final View view, ArrayList<ImageItem> imageItems, int startPosition) {
        final ImagePagerUIView imagePagerUIView = new ImagePagerUIView(imageItems, startPosition);
        if (iLayout == null) {
            return imagePagerUIView;
        }

        int[] rt = new int[2];
        view.getLocationOnScreen(rt);

        float w = view.getMeasuredWidth();
        float h = view.getMeasuredHeight();

        float x = rt[0] + w / 2;
        float y = rt[1] + h / 2;

        imagePagerUIView.mStartX = rt[0];
        imagePagerUIView.mStartY = rt[1];
        imagePagerUIView.mStartW = (int) w;
        imagePagerUIView.mStartH = (int) h;
        iLayout.startIView(imagePagerUIView, new UIParam(true).setHideLastIView(true /*false*/));

        return imagePagerUIView;
    }

    public ImagePagerUIView setIViewConfigCallback(IViewConfigCallback IViewConfigCallback) {
        mIViewConfigCallback = IViewConfigCallback;
        mIViewConfigCallback.mImagePagerUIView = this;
        return this;
    }

    public ImagePagerUIView setIndicatorStyle(IndicatorStyle indicatorStyle) {
        mIndicatorStyle = indicatorStyle;
        return this;
    }

    @Override
    public void onViewCreate(View rootView) {
        super.onViewCreate(rootView);
        mImagePageAdapter = new ImagePageAdapter(mActivity, mImageItems);
        mMViewPager.setAdapter(mImagePageAdapter);
        mImagePageAdapter.setOnExitListener(new DragPhotoView.OnExitListener() {
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
                int alpha = 255 - (int) (255 * (translateY * 2 / h));
                alpha = Math.max(0, alpha);
                alpha = Math.min(255, alpha);
                mLastTranColor = SkinHelper.getTranColor(Color.BLACK, alpha);
                mMRootLayout.setBackgroundColor(mLastTranColor);
            }
        });
        mImagePageAdapter.setPhotoViewClickListener(new ImagePageAdapter.PhotoViewClickListener() {
            @Override
            public void OnPhotoTapListener(View view, float v, float v1) {
                animToFinish();
            }
        });
        setPhotoViewLongClickListener(photoViewLongClickListener);
        mMCircleIndicator.setViewPager(mMViewPager);
        mTextIndicator.setupViewPager(mMViewPager);
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
        mMRootLayout = new RRelativeLayout(mActivity);
        mMViewPager = new ViewPagerFixed(mActivity);
        mMCircleIndicator = new PinchCircleIndicator(mActivity);
        mTextIndicator = new TextIndicator(mActivity);

        RelativeLayout.LayoutParams indicatorParams = new RelativeLayout.LayoutParams(-2, -2);
        indicatorParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        indicatorParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        indicatorParams.setMargins(0, 0, 0, (int) ResUtil.dpToPx(mActivity.getResources(), 50));

        mMRootLayout.addView(mMViewPager, new ViewGroup.LayoutParams(-1, -1));
        mMRootLayout.addView(mMCircleIndicator, indicatorParams);

        indicatorParams = new RelativeLayout.LayoutParams(-2, -2);
        indicatorParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        indicatorParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        int marginTop = getDimensionPixelOffset(R.dimen.base_xhdpi);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            marginTop += getDimensionPixelOffset(R.dimen.status_bar_height);
        }
        indicatorParams.setMargins(0, marginTop, 0, 0);
        mTextIndicator.setTextColor(Color.WHITE);
        mTextIndicator.setAutoHide(false);
        mMRootLayout.addView(mTextIndicator, indicatorParams);

        mMRootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animToFinish();
            }
        });
        container.addView(mMRootLayout, new ViewGroup.LayoutParams(-1, -1));

        mIViewConfigCallback.onInflateBaseView(mMRootLayout, inflater);
        return container;
    }

    private void animToFinish() {
        if (isToFinish) {
            return;
        }
        showLastViewPattern();

        isToFinish = true;
        hideIndicator();
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
//        try {
//            getILayout().getViewPatternAtLast(1).mView.setVisibility(View.GONE);
//        } catch (Exception e) {
//        }
    }

    @Deprecated
    private void startAnimation() {
        hideIndicator();
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
                    showIndicator();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mIViewConfigCallback.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        mIViewConfigCallback.onViewShow(bundle);
    }

    public ImagePageAdapter getImagePageAdapter() {
        return mImagePageAdapter;
    }

    private void showIndicator() {
        hideIndicator();
        switch (mIndicatorStyle) {
            case CIRCLE:
                mMCircleIndicator.setVisibility(View.VISIBLE);
                break;
            case TEXT:
                mTextIndicator.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void hideIndicator() {
        mMCircleIndicator.setVisibility(View.INVISIBLE);
        mTextIndicator.setVisibility(View.INVISIBLE);
    }

    public void setPhotoViewLongClickListener(ImagePageAdapter.PhotoViewLongClickListener listener) {
        photoViewLongClickListener = listener;
        if (mImagePageAdapter != null) {
            mImagePageAdapter.setPhotoViewLongClickListener(photoViewLongClickListener);
        }
    }

    /**
     * 指示器的样式
     */
    public enum IndicatorStyle {
        NONE, TEXT, CIRCLE
    }

    /**
     * 定制界面的Config
     */
    public static abstract class IViewConfigCallback {
        public ImagePagerUIView mImagePagerUIView;

        public void onInflateBaseView(final RelativeLayout containRootLayout, final LayoutInflater inflater) {

        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {

        }

        public void onViewShow(Bundle bundle) {

        }
    }

    /**
     * 用来保存图片的监听事件
     */
    public static class SavePhotoLongClickListener implements ImagePageAdapter.PhotoViewLongClickListener {
        ILayout mILayout;

        public SavePhotoLongClickListener(ILayout ILayout) {
            mILayout = ILayout;
        }

        @Override
        public void onLongClickListener(PhotoView photoView, int position, final ImageItem item) {
            if (item != null && item.canSave) {
                UIBottomItemDialog.build()
                        .addItem(mILayout.getLayout().getContext().getString(R.string.save_image), createSaveClickListener(item))
                        .showDialog(mILayout);
            }
        }

        @NonNull
        protected View.OnClickListener createSaveClickListener(final ImageItem item) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(item.path) && new File(item.path).exists()) {
                        saveImageFile(new File(item.path));
                    } else {
                        Glide.with(mILayout.getLayout().getContext().getApplicationContext())
                                .load(item.url)
                                .downloadOnly(new SimpleTarget<File>() {
                                    @Override
                                    public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                                        saveImageFile(resource);
                                    }
                                });
                    }
                }
            };
        }

        protected void saveImageFile(File file) {
            File toFile = new File(Root.getAppExternalFolder("images"), Root.createFileName(".jpeg"));
            if (FileUtils.copyFile(file, toFile)) {
                ImagePicker.galleryAddPic(mILayout.getLayout().getContext(), toFile);
                T_.ok(mILayout.getLayout().getContext().getString(R.string.save_to_phone_format, toFile.getAbsolutePath()));
            } else {
                T_.error(mILayout.getLayout().getContext().getString(R.string.save_error));
            }
        }
    }

}

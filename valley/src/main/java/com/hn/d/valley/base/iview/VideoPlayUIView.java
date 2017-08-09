package com.hn.d.valley.base.iview;

import android.animation.Animator;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.angcyo.github.utilcode.utils.FileUtils;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.Root;
import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.dialog.UIBottomItemDialog;
import com.angcyo.uiview.resources.AnimUtil;
import com.angcyo.uiview.resources.RAnimListener;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.view.UIIViewImpl;
import com.bumptech.glide.Glide;
import com.hn.d.valley.R;
import com.hn.d.valley.main.message.chat.viewholder.MsgViewHolderRedPacket;
import com.liulishuo.FDown;
import com.liulishuo.FDownListener;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.lzy.imagepicker.ImagePicker;
import com.m3b.rblibrary.RBMediaController;
import com.m3b.rblibrary.ShapeImageView;

import java.io.File;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：视频播放
 * 创建人员：Robi
 * 创建时间：2017/03/01 16:18
 * 修改人员：Robi
 * 修改时间：2017/03/01 16:18
 * 修改备注：
 * Version: 1.0.0
 */
public class VideoPlayUIView extends UIIViewImpl {

    String path, thumbImagePath;

    Drawable thumbDrawable;

    int[] thumbSize;

    RelayVideoLongClickListener mRelayVideoLongClickListener;

    RBMediaController mMediaController;
    RBMediaController.OnLongPress mOnLongPress = new RBMediaController.OnLongPress() {
        @Override
        public void onLongPress(String videoUrl) {
            L.e("call: onLongPress([videoUrl])-> " + videoUrl + " " + path);

            if (mRelayVideoLongClickListener != null) {
                mRelayVideoLongClickListener.onLongPress(videoUrl, thumbImagePath);
            }
        }
    };
    boolean canSave = false;
    boolean isPlayStart = false;
    String discuss_id = "";
    private boolean mIsLive;
    /**
     * 红包id
     */
    private String hotPackageId;
    private Rect mViewLocation = new Rect();
    private View mPreviewImageView;

    public VideoPlayUIView(String path) {
        this.path = path;
    }

    public VideoPlayUIView(String thumbImagePath, String path) {
        this.thumbImagePath = thumbImagePath;
        this.path = path;
    }

    public VideoPlayUIView(String path, Drawable thumbDrawable, int[] thumbSize) {
        this.path = path;
        this.thumbDrawable = thumbDrawable;
        this.thumbSize = thumbSize;
    }

    public VideoPlayUIView(String path, String thumbImagePath, Drawable thumbDrawable, int[] thumbSize) {
        this.path = path;
        this.thumbDrawable = thumbDrawable;
        this.thumbSize = thumbSize;
        this.thumbImagePath = thumbImagePath;
    }

    public VideoPlayUIView setDiscuss_id(String discuss_id) {
        this.discuss_id = discuss_id;
        return this;
    }

    @Override
    protected View inflateBaseView(FrameLayout container, LayoutInflater inflater) {
        return LayoutInflater.from(mActivity).inflate(R.layout.view_video_play_layout, container);
    }

    @Override
    public void loadContentView(View rootView) {
        super.loadContentView(rootView);
        initPlayer();
    }

    /**
     * 初始化播放器
     */
    private void initPlayer() {
        mMediaController = mViewHolder.v(R.id.view_player);

        if (mOnLongPress != null) {
            mMediaController.setOnLongPress(mOnLongPress);
        }

        mMediaController.setScaleType("fitParent");
        if (mIsLive) {
            mMediaController.setLive(true);//设置该地址是直播的地址, 主播状态下, 不会显示播放控制按钮
        }
        mMediaController.setScaleType("fitParent");
        ImageView previewImageView = mMediaController.getPreviewImageView();
        mPreviewImageView = previewImageView;
        mMediaController
                .onPrepared(new RBMediaController.OnPreparedListener() {
                    @Override
                    public void onPrepared() {
                        /**
                         * 监听视频是否已经准备完成开始播放
                         */
                        isPlayStart = true;
                        L.e("onPrepared: ");
                    }
                })
                .onComplete(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         * 监听视频是否已经播放完成了
                         */
                        L.e("run: ");

                        if (!TextUtils.isEmpty(hotPackageId)) {
                            //弹出抢红包对话框
                            MsgViewHolderRedPacket.checkRedPacketStatus(VideoPlayUIView.this, Long.valueOf(hotPackageId), discuss_id);
                        }
                    }
                })
                .onInfo(new RBMediaController.OnInfoListener() {
                    @Override
                    public void onInfo(int what, int extra) {
                        /**
                         * 监听视频的相关信息。
                         */
                        L.e("onInfo: ");
                        if (what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            mPreviewImageView.setVisibility(View.GONE);
                        }
                    }
                })
                .onError(new RBMediaController.OnErrorListener() {
                    @Override
                    public void onError(int what, int extra) {
                        /**
                         * 监听视频播放失败的回调
                         *
                         */
                        L.e("Error happened, errorCode = " + what);
                        L.e("onError: ");
                    }
                });
        //.play(path);//开始播放视频

        if (previewImageView != null) {
            if (thumbSize != null && thumbSize.length == 2 && thumbSize[0] > 0 && thumbSize[1] > 0) {
                //UI.setView(previewImageView, thumbSize[0], thumbSize[1]);
            }

            if (thumbDrawable != null) {
                if (thumbDrawable instanceof GradientDrawable && previewImageView instanceof ShapeImageView) {
                    ((ShapeImageView) previewImageView).setShapeDrawable(thumbDrawable);
                } else {
                    previewImageView.setImageDrawable(thumbDrawable);
                }
            } else if (!TextUtils.isEmpty(thumbImagePath)) {
                Glide.with(mActivity)
                        .load(thumbImagePath)
                        .fitCenter()
                        .into(previewImageView);
            }
        }
    }

    @Override
    public void onViewHide() {
        super.onViewHide();
        if (mMediaController != null) {
            mMediaController.onPause();
        }
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        //mMediaController.play(path);//开始播放视频
        if (!TextUtils.isEmpty(hotPackageId)) {
            mViewHolder.v(R.id.hot_package_view).setVisibility(View.VISIBLE);
            mViewHolder.click(R.id.hot_package_view, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    T_.show("看完视频,领取红包.");
                }
            });
        }
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        if (mMediaController != null) {
            mMediaController.onResume();
        }
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        fullscreen(true, true);
        mRootView.setKeepScreenOn(true);
        animToMax();
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        if (mMediaController != null) {
            mMediaController.hideAll();
            mMediaController.onDestroy();
        }
        fullscreen(false, true);
        mRootView.setKeepScreenOn(false);
    }

    @Override
    public boolean onBackPressed() {
        if (mMediaController != null && mMediaController.onBackPressed()) {
            return false;
        }
        mViewHolder.v(R.id.hot_package_view).setVisibility(View.GONE);
        animToMin();
        finishIView(this, new UIParam(true, true, false));
        return false;
    }

    /**
     * 退出动画
     */
    private void animToMin() {
        mViewLocation = AnimUtil.ensureRect(mViewLocation);

        int[] widthHeight;
        if (isPlayStart()) {
            if (thumbSize != null && thumbSize.length == 2 && thumbSize[0] > 0 && thumbSize[1] > 0) {
                widthHeight = RUtils.getCenterRectWidthHeight(new RectF(0, 0, thumbSize[0], thumbSize[1]),
                        new RectF(0, 0, mPreviewImageView.getMeasuredWidth(), mPreviewImageView.getMeasuredHeight()));
            } else {
                widthHeight = new int[]{ScreenUtil.screenWidth, ScreenUtil.screenHeight};
            }
        } else {
            widthHeight = RUtils.getCenterRectWidthHeight(new RectF(mViewLocation.left, mViewLocation.top, mViewLocation.right, mViewLocation.bottom),
                    new RectF(0, 0, mPreviewImageView.getMeasuredWidth(), mPreviewImageView.getMeasuredHeight()));
        }

        AnimUtil.startToMinAnim(mViewLocation,
                mMediaController.getChildAt(0), new Point(ScreenUtil.screenWidth / 2, ScreenUtil.screenHeight / 2),
                new Point(mViewLocation.centerX(), mViewLocation.centerY()),
                widthHeight[0], widthHeight[1],
                new RAnimListener() {
                    @Override
                    public void onAnimationProgress(Animator animation, float progress) {
                        super.onAnimationProgress(animation, progress);
                        mMediaController.setBackgroundColor(AnimUtil.evaluateColor(progress, Color.BLACK, Color.TRANSPARENT));
//                        mMViewPager.setAlpha(0.6f + 1 - progress);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        mMediaController.hideAll();
                    }
                });
    }

    /**
     * 进入动画
     */
    private void animToMax() {
//        hideIndicator();
//        Rect viewLocation = mImageItems.get(startPosition).mViewLocation;
        mViewLocation = AnimUtil.ensureRect(mViewLocation);

        if (mPreviewImageView == null) {
            mPreviewImageView = mMediaController;
        }

        if (mPreviewImageView.getMeasuredWidth() == 0 || mPreviewImageView.getMeasuredHeight() == 0) {
            mPreviewImageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mPreviewImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                    startAnimInner();
                    return false;
                }
            });
        } else {
            startAnimInner();
        }
    }

    private void startAnimInner() {
//        mMViewPager.setBackgroundColor(Color.RED);
        if (mPreviewImageView == null) {
            mPreviewImageView = mMediaController;
        }

        int[] widthHeight = RUtils.getCenterRectWidthHeight(new RectF(mViewLocation.left, mViewLocation.top, mViewLocation.right, mViewLocation.bottom),
                new RectF(0, 0, mPreviewImageView.getMeasuredWidth(), mPreviewImageView.getMeasuredHeight()));

        AnimUtil.startToMaxAnim(mViewLocation,
                mPreviewImageView,
                new Point(mViewLocation.centerX(), mViewLocation.centerY()),
                new Point(ScreenUtil.screenWidth / 2, ScreenUtil.screenHeight / 2),
                widthHeight[0],
                widthHeight[1], 60, new RAnimListener() {
                    @Override
                    public void onAnimationProgress(Animator animation, float progress) {
                        super.onAnimationProgress(animation, progress);
                        mMediaController.setBackgroundColor(AnimUtil.evaluateColor(progress, Color.TRANSPARENT, Color.BLACK));
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mMediaController.play(path);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                    }

                    @Override
                    public void onDelayBeforeStart(Animator animation) {
                        super.onDelayBeforeStart(animation);
//                        mPreviewImageView.setVisibility(View.VISIBLE);
                    }
                });
    }

    /**
     * 视频是否可以保存
     */
    public VideoPlayUIView setCanSave(boolean canSave) {
        this.canSave = canSave;
        return this;
    }

    public VideoPlayUIView setRelayVideoLongClickListener(RelayVideoLongClickListener relayVideoLongClickListener) {
        mRelayVideoLongClickListener = relayVideoLongClickListener;
        return this;
    }

    public VideoPlayUIView setOnLongPress(RBMediaController.OnLongPress onLongPress) {
        mOnLongPress = onLongPress;
        return this;
    }

    public VideoPlayUIView setHotPackageId(String hotPackageId) {
        this.hotPackageId = hotPackageId;
        return this;
    }

    public VideoPlayUIView setViewLocation(Rect viewLocation) {
        mViewLocation = viewLocation;
        return this;
    }

    public VideoPlayUIView resetViewLocation(View view) {
        view.getGlobalVisibleRect(mViewLocation);
        return this;
    }

    public boolean isPlayStart() {
        return isPlayStart;
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
    public boolean needTransitionExitAnim() {
        return true;
    }

    @Override
    public boolean needTransitionStartAnim() {
        return true;
    }

    /**
     * 用来保存视频的监听事件
     */
    public static class SaveVideoLongClickListener implements RBMediaController.OnLongPress {
        ILayout mILayout;

        public SaveVideoLongClickListener(ILayout ILayout) {
            mILayout = ILayout;
        }

        protected void saveVideoFile(File file) {
            File toFile = new File(Root.getAppExternalFolder("videos"), Root.createFileName(".mp4"));
            if (FileUtils.copyFile(file, toFile)) {
                ImagePicker.galleryAddPic(mILayout.getLayout().getContext(), toFile);
                T_.ok(mILayout.getLayout().getContext().getString(R.string.save_to_phone_format, toFile.getAbsolutePath()));
            } else {
                T_.error(mILayout.getLayout().getContext().getString(R.string.save_error));
            }
        }

        protected void saveVideoUrl(String url) {
            String path = Root.getAppExternalFolder("videos") + "/" + Root.createFileName(".mp4");
            L.e("call: saveVideoUrl([url])-> " + path);
            FDown.build(url).setFullPath(path).download(new FDownListener() {
                @Override
                public void onStarted(BaseDownloadTask task) {
                    super.onStarted(task);
                    T_.info("正在保存视频...");
                }

                @Override
                public void onCompleted(BaseDownloadTask task) {
                    super.onCompleted(task);
                    T_.info("视频保存至:" + task.getPath());
                }
            });
        }

        @Override
        public void onLongPress(String videoUrl) {
            final File file = new File(videoUrl);
            if (file.exists()) {
                UIBottomItemDialog.build()
                        .addItem(mILayout.getLayout().getContext().getString(R.string.save_video), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                saveVideoFile(file);
                            }
                        })
                        .showDialog(mILayout);
            }
        }
    }
}

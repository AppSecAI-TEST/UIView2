package com.hn.d.valley.base.iview;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.angcyo.github.utilcode.utils.FileUtils;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.Root;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.dialog.UIBottomItemDialog;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.UI;
import com.angcyo.uiview.view.UIIViewImpl;
import com.hn.d.valley.R;
import com.hn.d.valley.main.message.chat.viewholder.MsgViewHolderRedPacket;
import com.liulishuo.FDown;
import com.liulishuo.FDownListener;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.lzy.imagepicker.ImagePicker;
import com.m3b.rblibrary.RBMediaController;

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
                mRelayVideoLongClickListener.onLongPress(videoUrl,thumbImagePath);
            }
        }
    };
    boolean canSave = false;
    private boolean mIsLive;

    /**
     * 红包id
     */
    private String hotPackageId;

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
        final ImageView previewImageView = mMediaController.getPreviewImageView();
        mMediaController
                .onPrepared(new RBMediaController.OnPreparedListener() {
                    @Override
                    public void onPrepared() {
                        /**
                         * 监听视频是否已经准备完成开始播放
                         */
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
                            MsgViewHolderRedPacket.checkRedPacketStatus(VideoPlayUIView.this,Long.valueOf(hotPackageId));
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
                            previewImageView.setVisibility(View.GONE);
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
                })
                .play(path);//开始播放视频

//        if (!TextUtils.isEmpty(thumbImagePath) && previewImageView != null) {
//            Glide.with(mActivity)
//                    .load(thumbImagePath)
//                    .into(previewImageView);
//        }
        if (thumbDrawable != null && previewImageView != null) {
            UI.setView(previewImageView, thumbSize[0], thumbSize[1]);
            previewImageView.setImageDrawable(thumbDrawable);
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
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        if (mMediaController != null) {
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
        return super.onBackPressed();
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

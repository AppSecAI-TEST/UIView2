package com.hn.d.valley.base.iview;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.utils.UI;
import com.angcyo.uiview.view.UIIViewImpl;
import com.hn.d.valley.R;
import com.m3b.rblibrary.RBMediaController;

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

    RBMediaController mMediaController;
    private boolean mIsLive;

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
        fullscreen(true);
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        if (mMediaController != null) {
            mMediaController.onDestroy();
        }
        fullscreen(false);
    }

    @Override
    public boolean onBackPressed() {
        if (mMediaController != null && mMediaController.onBackPressed()) {
            return false;
        }
        return super.onBackPressed();
    }
}

package com.hn.d.valley.base.iview;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.angcyo.uiview.view.UIIViewImpl;
import com.hn.d.valley.R;
import com.m3b.rblibrary.RBMediaController;

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

    String path;

    RBMediaController mMediaController;
    private boolean mIsLive;

    public VideoPlayUIView(String path) {
        this.path = path;
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
        if (mIsLive) {
            mMediaController.setLive(true);//设置该地址是直播的地址, 主播状态下, 不会显示播放控制按钮
        }
        mMediaController
                .onPrepared(new RBMediaController.OnPreparedListener() {
                    @Override
                    public void onPrepared() {
                        /**
                         * 监听视频是否已经准备完成开始播放
                         */
                    }
                })
                .onComplete(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         * 监听视频是否已经播放完成了
                         */
                    }
                })
                .onInfo(new RBMediaController.OnInfoListener() {
                    @Override
                    public void onInfo(int what, int extra) {
                        /**
                         * 监听视频的相关信息。
                         */

                    }
                })
                .onError(new RBMediaController.OnErrorListener() {
                    @Override
                    public void onError(int what, int extra) {
                        /**
                         * 监听视频播放失败的回调
                         *
                         */
                        Log.e("####", "Error happened, errorCode = " + what);

                    }
                })
                .play(path);//开始播放视频
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
    public void onViewUnload() {
        super.onViewUnload();
        if (mMediaController != null) {
            mMediaController.onDestroy();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (mMediaController != null && mMediaController.onBackPressed()) {
            return false;
        }
        return super.onBackPressed();
    }
}

package com.hn.d.valley.base.iview

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.angcyo.uiview.base.UIViewConfig
import com.angcyo.uiview.container.ContentLayout
import com.angcyo.uiview.model.TitleBarPattern
import com.angcyo.uiview.skin.SkinHelper
import com.hn.d.valley.R
import com.hn.d.valley.base.BaseContentUIView
import com.m3b.rbvideolib.widget.ScalableTextureView
import com.m3b.rbvideolib.widget.TextureVideoView

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/07/14 14:04
 * 修改人员：Robi
 * 修改时间：2017/07/14 14:04
 * 修改备注：
 * Version: 1.0.0
 */
class VideoPreviewUIView(val viewConfig: UIViewConfig) : BaseContentUIView() {

    private lateinit var videoView: TextureVideoView
    private lateinit var videoPlayView: ImageView

    var videoPath: String? = null

    override fun getTitleBar(): TitleBarPattern {
        return super.getTitleBar()
                .setFloating(true)
                .setShowBackImageView(true)
                .setTitleString("")
    }

    override fun getTitleBarBGColor(): Int {
        return SkinHelper.getSkin().getThemeTranColor(0x80)
    }

    override fun inflateContentLayout(baseContentLayout: ContentLayout?, inflater: LayoutInflater?) {
        inflate(R.layout.view_video_preview_layout)
    }

    override fun getDefaultBackgroundColor() = Color.BLACK

    override fun initOnShowContentLayout() {
        super.initOnShowContentLayout()
        videoView = mViewHolder.v(R.id.videoView)
        videoPlayView = mViewHolder.v(R.id.play_view)

        videoView.setScaleType(ScalableTextureView.ScaleType.CENTER_CROP)
        videoView.setMediaPlayerCallback(object : TextureVideoView.MediaPlayerCallback {
            override fun onCompletion(mp: MediaPlayer?) {
                pauseVideo()
            }

            override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
            }

            override fun onVideoSizeChanged(mp: MediaPlayer?, width: Int, height: Int) {
            }

            override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
                return false
            }

            override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
                return false
            }

            override fun onPrepared(mp: MediaPlayer?) {
                postDelayed(100L) {
                    videoView.pause()
                }
            }

        })

        mViewHolder.click(R.id.play_view) {
            playVideo()
        }

        mBaseContentRootLayout.setOnClickListener {
            pauseVideo()
        }

        viewConfig.initOnShowContentLayout(this, mViewHolder)
    }

    private fun isPlaying(): Boolean {
        return videoView.isPlaying
    }

    fun pauseVideo() {
        if (isPlaying()) {
            videoView.stop()
        }

        uiTitleBarContainer.show(true)
        videoPlayView.visibility = View.VISIBLE

        onPlayEnd()
    }

    fun playVideo() {
        if (!isPlaying()) {
            videoView.mediaPlayer?.start()
        }

        uiTitleBarContainer.hide(true)
        videoPlayView.visibility = View.INVISIBLE

        onPlayStart()
    }


    override fun onViewShowFirst(bundle: Bundle?) {
        super.onViewShowFirst(bundle)

        videoPath?.let {
            videoView.setVideoPath(it)
            videoView.start()
        }
    }

    override fun onViewHide() {
        super.onViewHide()
        videoView.pause()
    }

    fun onPlayEnd() {
        mViewHolder.v<View>(R.id.root_layout)
                .animate()
                .translationY(0f)
                .setDuration(300)
                .start()
    }

    fun onPlayStart() {
        mViewHolder.v<View>(R.id.root_layout)
                .animate()
                .translationY(mViewHolder.v<View>(R.id.root_layout).measuredHeight.toFloat())
                .setDuration(300)
                .start()
    }
}
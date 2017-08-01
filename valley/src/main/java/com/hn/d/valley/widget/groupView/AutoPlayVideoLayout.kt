package com.hn.d.valley.widget.groupView

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.angcyo.library.utils.L
import com.angcyo.uiview.kotlin.v
import com.angcyo.uiview.resources.AnimUtil
import com.angcyo.uiview.utils.ScreenUtil
import com.hn.d.valley.R
import com.m3b.rbvideolib.widget.ScalableTextureView
import com.m3b.rbvideolib.widget.TextureVideoView


/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：包含一个视频播放的视图, 并且控制自动播放
 * 创建人员：Robi
 * 创建时间：2017/07/24 16:38
 * 修改人员：Robi
 * 修改时间：2017/07/24 16:38
 * 修改备注：
 * Version: 1.0.0
 */
class AutoPlayVideoLayout(context: Context, attributeSet: AttributeSet? = null) : FrameLayout(context, attributeSet),
        TextureVideoView.MediaPlayerCallback {
    override fun onPrepared(mp: MediaPlayer?) {
        //L.e("call:自动播放视频解析完成 onPrepared -> $mp")

        videoControlView?.let {
            it.alpha = 1f
            colorAnim = AnimUtil.startArgb(it, Color.TRANSPARENT, Color.BLACK, 300)
            //colorAnim = AnimUtil.startArgb(it, Color.TRANSPARENT, Color.BLACK, 300)
        }
    }

    override fun onCompletion(mp: MediaPlayer?) {
        //L.e("call: onCompletion -> ")
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
        L.e("call: onBufferingUpdate -> $percent")
    }

    override fun onVideoSizeChanged(mp: MediaPlayer?, width: Int, height: Int) {
        //L.e("call: onVideoSizeChanged -> ")
    }

    override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        //L.e("call: onInfo -> ")
        return false
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        //L.e("call: onError -> ")
        return false
    }

    var path: String = ""

    /**视频宽高大小*/
    var videoSize = intArrayOf(0, 0)
        get() {
            if (field[0] <= 0) {
                field[0] = (250 * ScreenUtil.density).toInt()
            }
            if (field[1] <= 0) {
                field[1] = (150 * ScreenUtil.density).toInt()
            }
            return field
        }

    /**视频视图所在屏幕上的位置*/
    var videoRect = Rect()
        get() {
            videoView?.getGlobalVisibleRect(field)
            return field
        }

    private val videoView: TextureVideoView?
        get() {
            return v(R.id.videoView)
        }

    private val videoControlView: View?
        get() {
            return v(R.id.video_control_layout)
        }

    fun stopPlay() {
        L.e("call: stopPlay -> 停止播放视频.")
        colorAnim?.let { it.cancel() }
        videoView?.let {
            it.setMediaPlayerCallback(null)
            it.stop()
            videoControlView?.visibility = View.GONE
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopPlay()
    }

    fun setVideoPath(path: String) {
        this.path = path

        videoView?.let {
            it.mute()
            if (it.isPlaying) {
                it.stop()
            }
        }

        if (path.isNullOrEmpty()) {
            videoControlView?.visibility = View.GONE
        } else {
            videoControlView?.visibility = View.INVISIBLE
            if (AutoPlayVideoControl.canAutoPlay()) {
                post { AutoPlayVideoControl.checkPlay() }
            }
        }
        //videoView.start()
    }

    private var colorAnim: ValueAnimator? = null

    fun startPlay() {
        try {
            if (AutoPlayVideoControl.canAutoPlay()) {
                videoView?.apply {
                    setMediaPlayerCallback(this@AutoPlayVideoLayout)

                    setScaleType(ScalableTextureView.ScaleType.TOP)
                    setVideoPath(path)
                    mute()

                    videoControlView?.let {
                        it.visibility = View.VISIBLE
                        it.alpha = 0f
                        //colorAnim = AnimUtil.startArgb(it, Color.TRANSPARENT, Color.BLACK, 300)
                    }

                    val lp = layoutParams as FrameLayout.LayoutParams
                    //lp.width = getResources().getDisplayMetrics().widthPixels;
                    //lp.height = (int) (height * (lp.width / (float) width));
                    lp.height = (150 * ScreenUtil.density).toInt()
                    lp.width = (videoSize[0] * (lp.height * 1f / videoSize[1])).toInt()
                    layoutParams = lp

                    //L.e("call: startPlay -> 自动播放视频:$path")
                    start()
                }
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

    fun haveVideo(): Boolean {
        return !path.isNullOrEmpty()
    }
}
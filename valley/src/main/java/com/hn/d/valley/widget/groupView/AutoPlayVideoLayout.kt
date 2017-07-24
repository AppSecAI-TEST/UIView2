package com.hn.d.valley.widget.groupView

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.angcyo.uiview.kotlin.v
import com.hn.d.valley.R
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
class AutoPlayVideoLayout(context: Context, attributeSet: AttributeSet? = null) : FrameLayout(context, attributeSet) {

    var path: String = ""

    var videoRect = Rect()
        get() {
            videoView.getGlobalVisibleRect(field)
            return field
        }

    private val videoView: TextureVideoView
        get() {
            return v(R.id.videoView)
        }

    fun stopPlay() {
        videoView.stop()
        videoView.visibility = View.GONE
    }

    fun setVideoPath(path: String) {
        this.path = path

        videoView.mute()
        if (videoView.isPlaying) {
            videoView.stop()
        }

        if (path.isNullOrEmpty()) {
            videoView.visibility = View.GONE
        } else {
            videoView.visibility = View.INVISIBLE
            videoView.setVideoPath(path)
            post { AutoPlayVideoControl.checkPlay() }
        }
        //videoView.start()
    }

    fun startPlay() {
        try {
            videoView.visibility = View.VISIBLE
            videoView.start()
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

    fun haveVideo(): Boolean {
        return !path.isNullOrEmpty()
    }
}
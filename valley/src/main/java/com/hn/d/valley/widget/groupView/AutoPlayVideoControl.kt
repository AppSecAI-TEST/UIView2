package com.hn.d.valley.widget.groupView

import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.angcyo.github.utilcode.utils.NetworkUtils
import com.angcyo.library.utils.L
import com.angcyo.uiview.kotlin.v
import com.angcyo.uiview.receiver.NetworkStateReceiver
import com.angcyo.uiview.utils.ScreenUtil
import com.angcyo.uiview.view.UIIViewImpl
import com.hn.d.valley.R
import java.lang.ref.WeakReference

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/07/24 17:36
 * 修改人员：Robi
 * 修改时间：2017/07/24 17:36
 * 修改备注：
 * Version: 1.0.0
 */
object AutoPlayVideoControl : RecyclerView.OnScrollListener(),
        RecyclerView.OnChildAttachStateChangeListener {

    private var lastPlayLayout: WeakReference<AutoPlayVideoLayout>? = null

    override fun onChildViewDetachedFromWindow(view: View?) {
        view?.let {
            val autoPlayLayout: View? = it.v<View>(R.id.media_control_layout)
            if (autoPlayLayout != null && autoPlayLayout is AutoPlayVideoLayout) {
                autoPlayLayout.stopPlay()
                L.i("call: onChildViewDetachedFromWindow -> 停止自动播放.")
            }
        }
    }

    override fun onChildViewAttachedToWindow(view: View?) {
        AutoPlayVideoControl.checkPlay()
    }

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            checkPlay()
        }
    }

    /**检查自动播放视频*/
    fun checkPlay() {
        if (recyclerView == null || !canAutoPlay()) {
            return
        }
        L.i("call: checkPlay -> 开始检查自动播放.")
        val manager = recyclerView?.layoutManager
        if (manager is LinearLayoutManager) {
            val firstVisibleItemPosition = manager.findFirstVisibleItemPosition()

            val videoRectList = mutableListOf<Rect>()
            val autoPlayLayout = mutableListOf<AutoPlayVideoLayout>()

            //拿到屏幕上, 所有视频的位置
            for (i in firstVisibleItemPosition..manager.itemCount - 1) {
                val view: View? = recyclerView?.findViewHolderForAdapterPosition(i)?.itemView?.v<View>(R.id.media_control_layout)
                if (view != null && view is AutoPlayVideoLayout) {
                    if (view.haveVideo()) {
                        videoRectList.add(view.videoRect)
                        autoPlayLayout.add(view)
                    }
                }
            }

            //判断离屏幕中心最近的视频
            var targetPosition = -1
            var offset = Int.MAX_VALUE
            videoRectList.mapIndexed { index, rect ->
                //屏幕中心点
                val sCenterY = ScreenUtil.screenHeight / 2
                val videoCenterY = rect.top + rect.height() / 2
                val dy = Math.abs(sCenterY - videoCenterY)

                //拿到距离中心最小的index
                if (dy <= offset && (rect.top - sCenterY) < sCenterY / 2 /*top离中心很近, 再播放*/) {
                    targetPosition = index
                    offset = dy
                }
            }
            L.i("call: checkPlay -> 视频位置:$videoRectList")

            if (targetPosition > -1) {
                //开始播放
                val autoPlayVideoLayout = autoPlayLayout[targetPosition]
                if (lastPlayLayout() != autoPlayVideoLayout) {
                    stopPlay()
                    lastPlayLayout = WeakReference(autoPlayVideoLayout)
                    autoPlayVideoLayout.startPlay()
                    L.i("call: checkPlay -> 自动播放位置:$targetPosition")
                } else {
                    L.i("call: checkPlay -> 自动播放位置:已在播放")
                }
            } else {
                L.i("call: checkPlay -> 自动播放位置:屏幕中未找到视频")
                stopPlay()
            }

        }
    }

    private var recyclerView: RecyclerView? = null

    fun init(recyclerView: RecyclerView?) {
        stopPlay()
        this.recyclerView?.let {
            it.removeOnScrollListener(this)
            it.removeOnChildAttachStateChangeListener(this)
        }
        this.recyclerView = recyclerView
        this.recyclerView?.addOnScrollListener(this)
        this.recyclerView?.addOnChildAttachStateChangeListener(this)
    }

    fun stopPlay() {
        lastPlayLayout()?.stopPlay()
        lastPlayLayout = null
    }

    /**只在WIFI下,自动播放*/
    fun canAutoPlay(): Boolean {
        return !UIIViewImpl.isLowDevice() && NetworkStateReceiver.getNetType() == NetworkUtils.NetworkType.NETWORK_WIFI
    }

    private fun lastPlayLayout(): AutoPlayVideoLayout? {
        return lastPlayLayout?.get()
    }
}
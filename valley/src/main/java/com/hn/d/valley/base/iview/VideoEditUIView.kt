package com.hn.d.valley.base.iview

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.angcyo.library.utils.L
import com.angcyo.uiview.Root
import com.angcyo.uiview.container.ContentLayout
import com.angcyo.uiview.container.UIParam
import com.angcyo.uiview.dialog.UIProgressDialog
import com.angcyo.uiview.model.TitleBarPattern
import com.angcyo.uiview.net.RFunc
import com.angcyo.uiview.net.RSubscriber
import com.angcyo.uiview.net.Rx
import com.angcyo.uiview.recycler.RBaseViewHolder
import com.angcyo.uiview.recycler.RRecyclerView
import com.angcyo.uiview.recycler.adapter.RBaseAdapter
import com.angcyo.uiview.resources.ResUtil
import com.angcyo.uiview.skin.SkinHelper
import com.angcyo.uiview.utils.T_
import com.angcyo.uiview.utils.file.AttachmentStore
import com.hn.d.valley.R
import com.hn.d.valley.base.BaseContentUIView
import com.lzy.imagepicker.bean.ImageItem
import com.m3b.rbvideolib.ImageSeekBar.ImageSeekBar
import com.m3b.rbvideolib.widget.ScalableTextureView
import com.m3b.rbvideolib.widget.TextureVideoView
import rx.functions.Action1
import java.io.File

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/07/12 10:42
 * 修改人员：Robi
 * 修改时间：2017/07/12 10:42
 * 修改备注：
 * Version: 1.0.0
 */
class VideoEditUIView(val item: ImageItem, val onCommandSuccess: Action1<EditVideoInfo>) : BaseContentUIView() {


    companion object {
        val shuiyinPath: String = "${Root.getAppExternalFolder("cache")}/shui_yin_logo.jpeg"

        fun initShuiYin(resources: Resources) {
            //检查水印是否存在SD卡上
            val file = File(shuiyinPath)
            if (file.exists()) {

            } else {
                AttachmentStore.saveBitmap(BitmapFactory.decodeResource(resources, R.drawable.shuiyin_1), shuiyinPath, false)
            }
        }
    }


    private var outPath: String? = null

    override fun onViewCreate(rootView: View?, param: UIParam?) {
        super.onViewCreate(rootView, param)
        initShuiYin(resources)
    }

    override fun getTitleBar(): TitleBarPattern {
        val progressDialog = UIProgressDialog.build().apply {
            setCanCancel(false)
            isDimBehind = false
            setTipText(getString(R.string.handing_tip))
        }

        return super.getTitleBar()
                .setShowBackImageView(true)
                .setTitleString(mActivity, R.string.video_edit_title)
                .addRightItem(TitleBarPattern.buildText(getString(R.string.cut)) {
                    val cropTime = seekBar.cropTime
                    val currentTime = getCurrentTime(mCurrentX, mCurrentY)

                    progressDialog.showDialog(mParentILayout)
                    videoView.stop()
                    seekBar.cancelIndicatorAnimator()

                    RVideoEdit.cutAndCompressVideo(mActivity,
                            item.path,
                            getOutFilePath(),
                            shuiyinPath,
                            getFormatTime(currentTime),
                            getFormatTime(cropTime),
                            object : OnExecCommandListener {
                                override fun onExecProgress(progress: Int) {
                                    L.e("call: onExecProgress -> $progress")
                                    progressDialog.setProgress(progress)
                                }

                                override fun onExecSuccess(message: String) {
                                    L.e("call: onExecSuccess -> $message")
                                    progressDialog.finishDialog()
                                    finishIView(this@VideoEditUIView, false)

                                    onCommandSuccess.call(EditVideoInfo(outPath!!, cropTime))
                                }

                                override fun onExecStart() {
                                    L.e("call: onExecStart -> ")
                                }

                                override fun onExecFail(reason: String) {
                                    L.e("call: onExecFail -> $reason")
                                    progressDialog.finishDialog()
                                    T_.error(getString(R.string.handing_error_tip))
                                }
                            }
                    )
                }.setVisibility(View.INVISIBLE))
    }

    override fun onViewShowFirst(bundle: Bundle?) {
        super.onViewShowFirst(bundle)
        uiTitleBarContainer.showRightItem(0)
    }

    private fun getOutFilePath(): String {
        outPath = "${Root.getAppExternalFolder("videos")}/${Root.createFileName(".mp4")}"
        return outPath!!
    }

    private fun getFormatTime(msTime: Long): String {
        val duration = msTime / 1000
        val starthours = duration / 3600
        val startminutes = (duration - starthours * 3600) / 60
        val startseconds = duration - (starthours * 3600 + startminutes * 60)
        return String.format("%02d:%02d:%02d", starthours, startminutes, startseconds)
    }

    override fun inflateContentLayout(baseContentLayout: ContentLayout?, inflater: LayoutInflater?) {
        inflate(R.layout.view_edit_video_layout)
    }

    override fun getDefaultBackgroundColor(): Int {
        return Color.BLACK
    }

    private lateinit var recyclerView: RRecyclerView
    private lateinit var videoView: TextureVideoView
    private lateinit var seekBar: ImageSeekBar
    private lateinit var mmr: MediaMetadataRetriever

    override fun initOnShowContentLayout() {
        super.initOnShowContentLayout()

        recyclerView = mViewHolder.v(R.id.recyclerView)
        videoView = mViewHolder.v(R.id.videoView)
        seekBar = mViewHolder.v(R.id.seekBar)

        videoView.setScaleType(ScalableTextureView.ScaleType.TOP)

        mmr = MediaMetadataRetriever()
        try {
            mmr.setDataSource(item.path)
        } catch (e: RuntimeException) {
            T_.error("不支持此格式!!!")
            finishIView()
            return
        }


        initVideoViewSize()
        initRecyclerView()
        mCurrentX = 30 * density()

        seekBar.setRectStrokeColor(SkinHelper.getSkin().themeSubColor)
        seekBar.setIndicatorStrokeColor(SkinHelper.getSkin().themeSubColor)
        seekBar.setLeftDrawable(ResUtil.tintDrawable(BitmapDrawable(resources, seekBar.bitmapLeftArrow), SkinHelper.getSkin().themeSubColor))
        seekBar.setRightDrawable(ResUtil.tintDrawable(BitmapDrawable(resources, seekBar.bitmapRightArrow), SkinHelper.getSkin().themeSubColor))

        seekBar.setOnVideoStateChangeListener(object : ImageSeekBar.OnVideoStateChangeListener {
            override fun onStart(x: Float, y: Float) {
                mCurrentX = x
                mCurrentY = y
                videoView.seekTo(getCurrentTime(mCurrentX, mCurrentY).toInt())
            }

            override fun onPause() {
                if (videoView.isPlaying()) {
                    videoView.pause()
                }
            }

            override fun onEnd() {
                videoView.seekTo(getCurrentTime(mCurrentX, mCurrentY).toInt())
            }

            override fun onResume() {
                videoView.resume()
            }

        })
    }

    private fun initVideoViewSize() {
        val lp = videoView.layoutParams as FrameLayout.LayoutParams
        lp.width = resources.displayMetrics.widthPixels
        lp.height = (item.height * (lp.width / item.width.toFloat())).toInt()
        videoView.layoutParams = lp
        videoView.setVideoPath(item.path)
        videoView.start()
    }

    private var secondBitweenTwoInamge: Int = 0
    private var ImageCont: Long = 0

    private var mCurrentX: Float = 0.toFloat()
    private var mCurrentY = 0f

    private fun initRecyclerView() {

        val timeInmillisec = item.videoDuration
        val duration = timeInmillisec / 1000
        val hours = duration / 3600
        val minutes = (duration - hours * 3600) / 60
        val seconds = duration - (hours * 3600 + minutes * 60)
        val totalsecond = hours * 36000 + minutes * 60 + seconds

        secondBitweenTwoInamge = seekBar.maxDuration / (seekBar.maxwidth / seekBar.maxDuration)
        ImageCont = totalsecond / secondBitweenTwoInamge

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    videoView.seekTo(getCurrentTime(mCurrentX, mCurrentY).toInt())
                    seekBar.resetIndicatorAnimator()
                }
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })

        recyclerView.adapter = object : RBaseAdapter<String>(mActivity) {
            override fun onBindView(holder: RBaseViewHolder, position: Int, bean: String?) {

                val imageView = holder.imgV(R.id.image_view)
                //imageView.setImageResource(R.drawable.image_placeholder_shape)
                //imageView.setImageResource(R.drawable.login_logo)
                if (position == 0 || position == itemCount - 1) {
                    imageView.setBackgroundColor(Color.TRANSPARENT)
                    imageView.setImageBitmap(null)
                    return
                }

                Rx.base(object : RFunc<Bitmap>() {
                    override fun onFuncCall(): Bitmap {
                        return mmr.getFrameAtTime((position * secondBitweenTwoInamge * 1000 * 1000).toLong(),
                                MediaMetadataRetriever.OPTION_CLOSEST)
                    }

                }, object : RSubscriber<Bitmap>() {
                    override fun onSucceed(bean: Bitmap?) {
                        super.onSucceed(bean)
                        if (bean != null) {
                            imageView.setImageBitmap(bean)
                        }
                    }
                })
            }

            override fun getItemCount(): Int {
                return (ImageCont + 2).toInt()
            }

            override fun getItemLayoutId(viewType: Int): Int {
                return 0
            }

            override fun createContentView(parent: ViewGroup?, viewType: Int): View {
                val view = ImageView(mActivity)
                view.id = R.id.image_view
                view.layoutParams = ViewGroup.LayoutParams(getDimensionPixelOffset(R.dimen.base_xxxhdpi), -1)
                view.scaleType = ImageView.ScaleType.CENTER_CROP
                return view
            }

        }
    }

    private fun getCurrentTime(x: Float, y: Float): Long {
        val position = recyclerView.getChildAdapterPosition(recyclerView.findChildViewUnder(x, y)).toLong()
        return (position - 1) * secondBitweenTwoInamge.toLong() * 1000
    }

    override fun onViewHide() {
        super.onViewHide()
        keepScreenOn(false)
        videoView.pause()
        seekBar.cancelIndicatorAnimator()
    }

    override fun onViewShow(bundle: Bundle?) {
        super.onViewShow(bundle)
        keepScreenOn(true)
        videoView.resume()
        seekBar.resetIndicatorAnimator()
    }

    override fun onViewUnload() {
        super.onViewUnload()
        seekBar.release()
        mmr.release()
    }
}

data class EditVideoInfo(var videoPath: String, var videoDuration: Long)
package com.hn.d.valley.main.seek

import android.content.Intent
import android.view.View
import android.widget.ImageView
import com.angcyo.library.utils.L
import com.angcyo.uiview.base.Item
import com.angcyo.uiview.base.SingleItem
import com.angcyo.uiview.container.UIParam
import com.angcyo.uiview.github.luban.Luban
import com.angcyo.uiview.net.RException
import com.angcyo.uiview.net.RRetrofit
import com.angcyo.uiview.net.Rx
import com.angcyo.uiview.recycler.RBaseViewHolder
import com.angcyo.uiview.recycler.RDragRecyclerView
import com.angcyo.uiview.recycler.adapter.RAddPhotoAdapter
import com.angcyo.uiview.utils.RUtils
import com.angcyo.uiview.utils.T_
import com.angcyo.uiview.utils.UI
import com.angcyo.uiview.view.UIIViewImpl
import com.bumptech.glide.Glide
import com.hn.d.valley.BuildConfig
import com.hn.d.valley.R
import com.hn.d.valley.base.BaseItemUIView
import com.hn.d.valley.base.Param
import com.hn.d.valley.base.iview.VideoRecordUIView
import com.hn.d.valley.base.oss.OssControl
import com.hn.d.valley.base.oss.OssControl2
import com.hn.d.valley.base.rx.BaseSingleSubscriber
import com.hn.d.valley.control.VideoStatusInfo
import com.hn.d.valley.service.ShowService
import com.hn.d.valley.utils.Image
import com.hn.d.valley.widget.HnLoading
import com.lzy.imagepicker.ImagePickerHelper
import rx.functions.Action3
import java.io.File
import java.util.*

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：开启秀场
 * 创建人员：Robi
 * 创建时间：2017/08/05 10:03
 * 修改人员：Robi
 * 修改时间：2017/08/05 10:03
 * 修改备注：
 * Version: 1.0.0
 */
class OpenSeekUIView : BaseItemUIView() {

    private lateinit var photoAdapter: RAddPhotoAdapter<Luban.ImageItem>
    private var videoInfo: VideoStatusInfo? = null

    /**编辑秀场时的视频信息*/
    var oldVideoInfo: VideoStatusInfo? = null
    var oldImages = mutableListOf<String>()

    override fun getTitleString(): String {
        return "秀场"
    }

    override fun getItemLayoutId(position: Int): Int {
        return R.layout.item_open_seek_layout
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //请求压缩图片,排除掉已经压缩的图片
        val observable = Image.onActivityResult(mActivity, requestCode, resultCode, data)
        observable?.subscribe(object : BaseSingleSubscriber<ArrayList<Luban.ImageItem>>() {
            override fun onStart() {
                super.onStart()
                HnLoading.show(mILayout, false)
            }

            override fun onSucceed(strings: ArrayList<Luban.ImageItem>) {
                if (BuildConfig.DEBUG) {
                    Luban.logFileItems(mActivity, strings)
                }
//                val photos = mutableListOf<String>()
//                strings.map {
//                    photos.add(it.thumbPath)
//                }
                strings.addAll(0, oldImages.map { Luban.ImageItem(it) })
                photoAdapter.resetData(strings)
            }

            override fun onEnd(isError: Boolean, isNoNetwork: Boolean, e: RException?) {
                super.onEnd(isError, isNoNetwork, e)
                HnLoading.hide()
            }
        })

    }

    override fun createItems(items: MutableList<SingleItem>?) {
        items?.add(object : SingleItem() {
            override fun onBindView(holder: RBaseViewHolder, posInData: Int, dataBean: Item?) {
                val recyclerView: RDragRecyclerView = holder.v(R.id.recycler_view)
                photoAdapter = object : RAddPhotoAdapter<Luban.ImageItem>(mActivity) {
                    override fun getAddViewImageResource(position: Int): Int {
                        return when (position) {
                            0 -> R.drawable.shangchuantupian_2
                            else -> R.drawable.add_xiuchang
                        }
                    }
                }.apply {
                    setExcludeWidth(getDimensionPixelOffset(R.dimen.base_xxhdpi))
                    setMaxPhotoCount(6)
                    setItemCountLine(3)

                    setConfigCallback(object : RAddPhotoAdapter.ConfigCallback() {
                        override fun onDisplayImage(imageView: ImageView?, position: Int) {
                            if (allDatas[position].url.isNullOrEmpty()) {
                                Glide.with(mActivity)
                                        .load(File(allDatas[position].thumbPath))
                                        .into(imageView)
                            } else {
                                Glide.with(mActivity)
                                        .load(allDatas[position].url)
                                        .into(imageView)
                            }
                        }

                        override fun onImageLongClick(imageView: ImageView?, position: Int): Boolean {
                            setDeleteModel(recyclerView, true)
                            return true
                        }

                        override fun onAddClick(view: View?) {
                            ImagePickerHelper.startImagePicker(mActivity, false, false, true, 6 - oldImages.size)
                        }

                        override fun onDeleteClick(view: View?, position: Int): Boolean {
                            val imageItem = allDatas[position]
                            if (imageItem.url.isNullOrEmpty()) {
                                ImagePickerHelper.clearSelectedPath(mutableListOf(imageItem.path), false)
                            } else {
                                oldImages.remove(imageItem.url)
                            }
                            return false
                        }

                    })
                }
                UI.setView(holder.v(R.id.image_layout), photoAdapter.itemSize, photoAdapter.itemSize)

                recyclerView.adapter = photoAdapter
                recyclerView.setDragCallback { _, viewHolder ->
                    viewHolder.itemViewType != RAddPhotoAdapter.TYPE_ADD
                }

                //选择视频
                holder.click(R.id.image_view) {
                    mParentILayout.startIView(VideoRecordUIView(Action3<UIIViewImpl, String, String> { iView, path, s ->
                        videoInfo = VideoStatusInfo(path, s)
                        oldVideoInfo = null
                        iView.finishIView(iView, UIParam().setUnloadRunnable {
                            Glide.with(mActivity)
                                    .load(path)
                                    .into(holder.imgV(R.id.image_view))
                            holder.v<View>(R.id.play_view).visibility = View.VISIBLE
                        })
                    }))
                }

                //确定
                holder.click(R.id.ok_view) {
                    if (photoAdapter.allDatas.isEmpty()) {
                        T_.error("请选择图片.")
                    } else {
                        uploadStart()
                    }
                }

                //如果是编辑秀场, 初始化默认参数
                oldVideoInfo?.let {
                    Glide.with(mActivity)
                            .load(it.videoThumbPath)
                            .into(holder.imgV(R.id.image_view))
                    holder.v<View>(R.id.play_view).visibility = View.VISIBLE
                }

                val oldItems = oldImages.map { Luban.ImageItem(it) }
                photoAdapter.resetData(oldItems)
            }
        })
    }

    /**开始上传文件*/
    private fun uploadStart() {
        HnLoading.show(mParentILayout, false)
        if (videoInfo == null) {
            uploadImages()
        } else {
            uploadVideo()
        }
    }

    private var video: String? = null

    private fun uploadVideo() {
        OssControl2(object : OssControl2.OnUploadListener {
            override fun onUploadStart() {

            }

            override fun onUploadSucceed(list: List<String>?) {
                if (list == null || list.isEmpty()) {
                    uploadFailed()
                } else {
                    //视频上传成功后, 再上传图片
                    val videoUrl = list[0]
                    L.e("视频上传成功:" + videoUrl)

                    OssControl(object : OssControl.OnUploadListener {
                        override fun onUploadStart() {

                        }

                        override fun onUploadSucceed(list: List<String>) {
                            val videoThumbUrl = list[0]
                            video = videoThumbUrl + "?" + videoUrl
                            L.e("视频缩略图上传成功:" + videoThumbUrl)
                            uploadImages()
                        }

                        override fun onUploadFailed(code: Int, msg: String) {
                            uploadFailed()
                        }
                    }).uploadCircleImg(videoInfo!!.videoThumbPath)
                }
            }

            override fun onUploadFailed(code: Int, msg: String) {
                uploadFailed()
            }
        }).uploadVideo(videoInfo!!.videoPath)
    }

    private fun uploadImages() {
        if (photoAdapter.allDatas.isEmpty()) {
            uploadSuccess(video, "")
        } else {
            val files = photoAdapter.allDatas.map { imageItem -> if (imageItem.thumbPath.isNullOrEmpty()) imageItem.url else imageItem.thumbPath }

            OssControl(object : OssControl.OnUploadListener {
                override fun onUploadStart() {

                }

                override fun onUploadSucceed(list: List<String>) {
                    uploadSuccess(video, RUtils.connect(list))
                }

                override fun onUploadFailed(code: Int, msg: String) {
                    uploadFailed()
                }
            }).uploadCircleImg(files)
        }
    }

    private fun uploadFailed() {
        HnLoading.hide()
        T_.error("上传失败, 请重试.")
    }

    /**调用接口*/
    private fun uploadSuccess(video: String?, images: String?) {
        add(RRetrofit.create(ShowService::class.java)
                .upload(Param.buildMap("video:$video", "images:$images"))
                .compose(Rx.transformer(String::class.java))
                .subscribe(object : BaseSingleSubscriber<String>() {
                    override fun onSucceed(bean: String?) {
                        super.onSucceed(bean)
                        finishIView()
                        T_.show(bean)
                    }

                    override fun onEnd(isError: Boolean, isNoNetwork: Boolean, e: RException?) {
                        super.onEnd(isError, isNoNetwork, e)
                        HnLoading.hide()
                        if (isError) {
                            uploadFailed()
                        }
                    }
                }))
    }
}
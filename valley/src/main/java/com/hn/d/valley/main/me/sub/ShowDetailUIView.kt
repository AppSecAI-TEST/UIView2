package com.hn.d.valley.main.me.sub

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import com.angcyo.github.utilcode.utils.SpannableStringUtils
import com.angcyo.uiview.container.ContentLayout
import com.angcyo.uiview.github.goodview.GoodView
import com.angcyo.uiview.model.TitleBarPattern
import com.angcyo.uiview.net.RException
import com.angcyo.uiview.net.RRetrofit
import com.angcyo.uiview.net.Rx
import com.angcyo.uiview.recycler.RBaseViewHolder
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter
import com.angcyo.uiview.rsen.RefreshLayout
import com.angcyo.uiview.utils.RUtils
import com.angcyo.uiview.utils.T_
import com.angcyo.uiview.view.IView
import com.angcyo.uiview.view.OnUIViewListener
import com.hn.d.valley.R
import com.hn.d.valley.base.Param
import com.hn.d.valley.base.iview.ImagePagerUIView
import com.hn.d.valley.base.iview.RelayPhotoLongClickListener
import com.hn.d.valley.base.iview.RelayVideoLongClickListener
import com.hn.d.valley.base.iview.VideoPlayUIView
import com.hn.d.valley.base.oss.OssHelper
import com.hn.d.valley.base.rx.BaseSingleSubscriber
import com.hn.d.valley.bean.SeekBean
import com.hn.d.valley.cache.UserCache
import com.hn.d.valley.control.UserDiscussItemControl
import com.hn.d.valley.control.VideoStatusInfo
import com.hn.d.valley.main.message.gift.GiftListUIView2
import com.hn.d.valley.main.seek.HnSeekGlideImageView
import com.hn.d.valley.main.seek.OpenSeekUIView
import com.hn.d.valley.service.ShowService
import com.hn.d.valley.sub.other.SingleRecyclerUIView
import com.hn.d.valley.utils.PhotoPager
import com.hn.d.valley.x5.X5WebUIView
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：个人详情, 秀场界面
 * 创建人员：Robi
 * 创建时间：2017/08/05 09:16
 * 修改人员：Robi
 * 修改时间：2017/08/05 09:16
 * 修改备注：
 * Version: 1.0.0
 */
class ShowDetailUIView(val to_uid: String) : SingleRecyclerUIView<String>() {

    companion object {
        fun isMe(to_uid: String): Boolean {
            return UserCache.getUserAccount().equals(to_uid, ignoreCase = true)
        }
    }

    override fun getTitleBar(): TitleBarPattern? {
        return null
    }

    override fun hasDecoration(): Boolean {
        return false
    }

    override fun inflateOverlayLayout(baseContentLayout: ContentLayout, inflater: LayoutInflater) {
        inflate(R.layout.layout_show_detail_empty_pager)
    }

    override fun initRefreshLayout() {
        super.initRefreshLayout()
        refreshLayout.setRefreshDirection(RefreshLayout.NONE)
    }

    override fun onEmptyData(isEmpty: Boolean) {
        mViewHolder.v<View>(R.id.show_data_layout).visibility = View.INVISIBLE
        mViewHolder.v<View>(R.id.show_empty_text_tip).visibility = View.VISIBLE
        mViewHolder.v<View>(R.id.show_empty_button_tip).visibility = View.VISIBLE
        mViewHolder.v<View>(R.id.me_control_layout).visibility = View.GONE
        mViewHolder.v<View>(R.id.other_control_layout).visibility = View.GONE

        //秀场协议
        mViewHolder.click(R.id.show_help) {
            mParentILayout.startIView(X5WebUIView("http://wap.klgwl.com/article/detail/MaT2UxwnMdD2Iw1-NjgwMw-- ")
                    .setShowDefaultMenu(false))
        }
        if (isEmpty) {
            if (isMe(to_uid)) {
                mViewHolder.tv(R.id.show_empty_text_tip).text = "你还未开启秀场哦~"

                //开启秀场
                mViewHolder.click(R.id.show_empty_button_tip) {
                    if (needUploadShow) {
                        mParentILayout.startIView(OpenSeekUIView().apply {
                            setOnUIViewListener(object : OnUIViewListener() {
                                override fun onViewUnload(uiview: IView) {
                                    loadData()
                                }
                            })
                        })
                    } else {
                        add(RRetrofit.create(ShowService::class.java)
                                .open(Param.buildMap())
                                .compose(Rx.transformer(String::class.java))
                                .subscribe(object : BaseSingleSubscriber<String>() {
                                    override fun onSucceed(bean: String?) {
                                        super.onSucceed(bean)
                                        T_.show(bean)
                                        loadData()
                                    }
                                }))
                    }
                }
            } else {
                mViewHolder.v<View>(R.id.show_empty_layout).visibility = View.INVISIBLE
                mViewHolder.tv(R.id.show_empty_text_tip).text = "对方还未开启秀场哦~"
                mViewHolder.v<View>(R.id.show_empty_button_tip).visibility = View.INVISIBLE
            }
        } else {
            mViewHolder.v<View>(R.id.show_data_layout).visibility = View.VISIBLE
            mViewHolder.v<View>(R.id.show_empty_text_tip).visibility = View.GONE
            mViewHolder.v<View>(R.id.show_empty_button_tip).visibility = View.GONE

            mViewHolder.tv(R.id.like_cnt).text = SpannableStringUtils.getBuilder("赞: ")
                    .append(seekBean.like_cnt.toString())
                    .setForegroundColor(getColor(R.color.orange))
                    .create()

            mViewHolder.tv(R.id.dislike_cnt).text = SpannableStringUtils.getBuilder("踩: ")
                    .append(seekBean.dislike_cnt.toString())
                    .setForegroundColor(getColor(R.color.orange))
                    .create()

            if (isMe(to_uid)) {
                mViewHolder.v<View>(R.id.me_control_layout).visibility = View.VISIBLE

                //关闭秀场
                mViewHolder.click(R.id.show_close_view) {
                    add(RRetrofit.create(ShowService::class.java)
                            .close(Param.buildMap())
                            .compose(Rx.transformer(String::class.java))
                            .subscribe(object : BaseSingleSubscriber<String>() {
                                override fun onSucceed(bean: String?) {
                                    super.onSucceed(bean)
                                    T_.show(bean)
                                    loadData()
                                }
                            }))
                }

                //编辑秀场
                mViewHolder.click(R.id.show_edit_view) {
                    mParentILayout.startIView(OpenSeekUIView().apply {
                        if (!seekBean.isVideoEmpty) {
                            oldVideoInfo = VideoStatusInfo(seekBean.videoThumbUrl, seekBean.videoUrl)
                        }

                        if (!seekBean.isImagesEmpty) {
                            oldImages = RUtils.split(seekBean.images)
                        }

                        setOnUIViewListener(object : OnUIViewListener() {
                            override fun onViewUnload(uiview: IView) {
                                loadData()
                            }
                        })
                    })
                }

            } else {
                mViewHolder.v<View>(R.id.other_control_layout).visibility = View.VISIBLE

                if (seekBean.is_like == 1) {
                    mViewHolder.imgV(R.id.show_like_view).tag = "like"
                    mViewHolder.imgV(R.id.show_like_view).setImageResource(R.drawable.xiuchang_dianzhan_s)
                }
                if (seekBean.is_dislike == 1) {
                    mViewHolder.imgV(R.id.show_dislike_view).tag = "dislike"
                    mViewHolder.imgV(R.id.show_dislike_view).setImageResource(R.drawable.xiuchang_cai_s)
                }

                //点赞
                mViewHolder.click(R.id.show_like_view) {
                    if (it.tag == null) {
                        it.tag = "like"
                        seekBean.like_cnt = "${seekBean.like_cnt.toInt() + 1}"
                        mViewHolder.imgV(R.id.show_like_view).setImageResource(R.drawable.xiuchang_dianzhan_s)
                        onEmptyData(false)
                        GoodView.build(it)

                        add(RRetrofit.create(ShowService::class.java)
                                .like(Param.buildMap("to_uid:$to_uid"))
                                .compose(Rx.transformer(SeekBean::class.java))
                                .subscribe(object : BaseSingleSubscriber<SeekBean>() {
                                    override fun onError(code: Int, msg: String?) {

                                    }
                                }))
                    } else {

                    }
                }

                //踩
                mViewHolder.click(R.id.show_dislike_view) {
                    if (it.tag == null) {
                        it.tag = "dislike"
                        seekBean.dislike_cnt = "${seekBean.dislike_cnt.toInt() + 1}"
                        mViewHolder.imgV(R.id.show_dislike_view).setImageResource(R.drawable.xiuchang_cai_s)
                        onEmptyData(false)

                        add(RRetrofit.create(ShowService::class.java)
                                .dislike(Param.buildMap("to_uid:$to_uid"))
                                .compose(Rx.transformer(SeekBean::class.java))
                                .subscribe(object : BaseSingleSubscriber<SeekBean>() {
                                    override fun onError(code: Int, msg: String?) {

                                    }
                                }))
                    } else {

                    }
                }

                //送礼物
                mViewHolder.click(R.id.show_gift_view) {
                    mParentILayout.startIView(GiftListUIView2(to_uid, SessionTypeEnum.P2P))
                }
            }
        }
    }

    override fun initRExBaseAdapter(): RExBaseAdapter<String, String, String> {
        return object : RExBaseAdapter<String, String, String>(mActivity) {

            override fun getItemLayoutId(viewType: Int): Int {
                return R.layout.item_show_detail
            }

            override fun onBindDataView(holder: RBaseViewHolder, posInData: Int, dataBean: String) {
                super.onBindDataView(holder, posInData, dataBean)

                if (posInData == 0) {
                    holder.itemView.setPadding(0, 40 * density().toInt(), 0, 0)
                } else {
                    holder.itemView.setPadding(0, 0, 0, 0)
                }

                holder.v<View>(R.id.play_view).visibility = if (dataBean.contains("?")) View.VISIBLE else View.INVISIBLE
                val imageView: HnSeekGlideImageView = holder.v(R.id.image_view)

                var url: String = dataBean

                if (dataBean.contains("?")) {
                    holder.v<View>(R.id.play_view).visibility = View.VISIBLE
                    val videoParams = UserDiscussItemControl.getVideoParams(url)
                    val thumbUrl = videoParams[0]
                    val videoUrl = videoParams[1]
                    url = thumbUrl

                    imageView.setOnClickListener {
                        mParentILayout.startIView(VideoPlayUIView(videoUrl,
                                thumbUrl, imageView.copyDrawable(),
                                OssHelper.getWidthHeightWithUrl(thumbUrl))
                                .resetViewLocation(imageView)
                                .setRelayVideoLongClickListener(RelayVideoLongClickListener(mParentILayout, true)))
                    }
                } else {
                    holder.v<View>(R.id.play_view).visibility = View.INVISIBLE

                    imageView.setOnClickListener {
                        ImagePagerUIView.start(mParentILayout, imageView,
                                PhotoPager.getImageItems(if (haveVideo) allDatas.filterIndexed { index, s -> index != 0 } else allDatas,
                                        listOf(imageView), true), if (haveVideo) (posInData - 1) else posInData)
                                .setPhotoViewLongClickListener(RelayPhotoLongClickListener(mParentILayout))
                    }
                }

                imageView.apply {
                    reset()
                    this.url = url
                }
            }
        }
    }

    private lateinit var seekBean: SeekBean
    private var haveVideo = false
    /**第一次开启秀场, 需要上传资源, 第二次则不要*/
    private var needUploadShow = true

    override fun onUILoadData(page: String?) {
        super.onUILoadData(page)
        add(RRetrofit.create(ShowService::class.java)
                .detail(Param.buildMap("to_uid:$to_uid"))
                .compose(Rx.transformer(SeekBean::class.java))
                .subscribe(object : BaseSingleSubscriber<SeekBean>() {
                    override fun onSucceed(bean: SeekBean?) {
                        super.onSucceed(bean)
                        needUploadShow = bean == null || bean.isEmpty
                        if (bean != null && "1".equals(bean!!.enable, true) && !TextUtils.isEmpty(bean!!.enable)) {
                            //秀场开启了
                            seekBean = bean
                            haveVideo = bean.video.isNotEmpty() && bean.video.contains("?")

                            val datas: MutableList<String> = RUtils.split(bean.images)
                            if (haveVideo) {
                                datas.add(0, bean.video)
                            }
                            onUILoadDataEnd(datas)
                        } else {
                            onUILoadDataEnd()
                        }
                    }

                    override fun onEnd(isError: Boolean, isNoNetwork: Boolean, e: RException?) {
                        super.onEnd(isError, isNoNetwork, e)
                        if (isError) {
                            showNonetLayout {
                                loadData()
                            }
                        }
                    }
                }))
    }
}
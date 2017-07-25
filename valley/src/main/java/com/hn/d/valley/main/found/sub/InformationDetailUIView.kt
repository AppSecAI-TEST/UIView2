package com.hn.d.valley.main.found.sub

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.angcyo.library.utils.L
import com.angcyo.uiview.container.ContentLayout
import com.angcyo.uiview.design.StickLayout2
import com.angcyo.uiview.dialog.UIItemDialog
import com.angcyo.uiview.github.goodview.GoodView
import com.angcyo.uiview.github.tablayout.SlidingTabLayout
import com.angcyo.uiview.github.tablayout.TabLayoutUtil
import com.angcyo.uiview.kotlin.v
import com.angcyo.uiview.model.TitleBarPattern
import com.angcyo.uiview.net.RException
import com.angcyo.uiview.net.RRetrofit
import com.angcyo.uiview.net.Rx
import com.angcyo.uiview.recycler.RBaseViewHolder
import com.angcyo.uiview.resources.ResUtil
import com.angcyo.uiview.skin.SkinHelper
import com.angcyo.uiview.utils.RUtils
import com.angcyo.uiview.utils.T_
import com.angcyo.uiview.utils.UI
import com.angcyo.uiview.view.IView
import com.angcyo.uiview.widget.*
import com.angcyo.uiview.widget.viewpager.UIPagerAdapter
import com.angcyo.uiview.widget.viewpager.UIViewPager
import com.hn.d.valley.R
import com.hn.d.valley.base.BaseContentUIView
import com.hn.d.valley.base.Param
import com.hn.d.valley.base.iview.ImagePagerUIView
import com.hn.d.valley.base.iview.VideoPlayUIView
import com.hn.d.valley.base.rx.BaseSingleSubscriber
import com.hn.d.valley.bean.HotInfoListBean
import com.hn.d.valley.bean.ILikeData
import com.hn.d.valley.bean.InformationDetailBean
import com.hn.d.valley.bean.NewsBean
import com.hn.d.valley.cache.UserCache
import com.hn.d.valley.main.message.service.SearchService
import com.hn.d.valley.service.NewsService
import com.hn.d.valley.sub.user.PublishDynamicUIView2
import com.hn.d.valley.sub.user.dialog.DynamicShareDialog
import com.hn.d.valley.sub.user.sub.CommentInputDialog
import com.hn.d.valley.sub.user.sub.CommentListUIView
import com.hn.d.valley.utils.PhotoPager
import com.hn.d.valley.widget.HnItemTextView
import com.hn.d.valley.x5.AndroidJs
import com.hn.d.valley.x5.X5WebView
import com.tencent.smtt.sdk.WebView
import rx.functions.Action0
import rx.subscriptions.CompositeSubscription

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：资讯详情
 * 创建人员：Robi
 * 创建时间：2017/06/20 11:19
 * 修改人员：Robi
 * 修改时间：2017/06/20 11:19
 * 修改备注：
 * Version: 1.0.0
 */
class InformationDetailUIView : BaseContentUIView {

    var id: String
    var hotInfoBean: HotInfoListBean? = null
    lateinit var detailBean: InformationDetailBean

    constructor(hotInfoBean: HotInfoListBean) {
        this.id = hotInfoBean.id.toString()
        this.hotInfoBean = hotInfoBean
    }

    constructor(id: String) {
        this.id = id
    }


    companion object {
        fun getDetailUrl(id: String) = "http://news.klgwl.com/wap/news/$id"
        fun getDetailUrl(id: Int) = "http://news.klgwl.com/wap/news/$id"

        fun getDetailShareUrl(id: String) = "http://news.klgwl.com/wap/news/share/$id"
        fun getDetailShareUrl(id: Int) = "http://news.klgwl.com/wap/news/share/$id"

        fun initLikeView(subscriptions: CompositeSubscription, viewHolder: RBaseViewHolder, type: String, data: ILikeData) {
            val likeView: View = viewHolder.v(R.id.like_cnt)
            if (data.isLike == 1) {
                if (likeView is ImageView) {
                    likeView.setImageResource(R.drawable.dianzan_pinglun_s)
                } else if (likeView is HnItemTextView) {
                    likeView.setLeftIco(R.drawable.love_icon_s)
                    likeView.text = data.likeCount
                }
                likeView.setOnClickListener {
                    if (likeView is HnItemTextView) {
                        data.likeCount = (data.likeCount.toInt() - 1).toString()
                        likeView.text = data.likeCount
                    }
                    data.isLike = 2
                    initLikeView(subscriptions, viewHolder, type, data)

                    subscriptions.add(RRetrofit.create(NewsService::class.java)
                            .unlike(Param.buildInfoMap("type:$type", "id:" + data.getDiscussId("info_comment"), "uid:" + UserCache.getUserAccount()))
                            .compose(Rx.transformer(String::class.java))
                            .subscribe(object : BaseSingleSubscriber<String>() {
                                override fun onSucceed(bean: String?) {
                                    super.onSucceed(bean)
                                    //T_.show(bean)
                                    //data.isLike = 2
                                }
                            }))
                }
            } else {
                if (likeView is ImageView) {
                    likeView.setImageResource(R.drawable.dianzan_pinglun_n)
                } else if (likeView is HnItemTextView) {
                    likeView.setLeftIco(R.drawable.love_icon_n)
                    likeView.text = data.likeCount
                }
                likeView.setOnClickListener {
                    GoodView.build(likeView)
                    if (likeView is HnItemTextView) {
                        data.likeCount = (data.likeCount.toInt() + 1).toString()
                        likeView.text = data.likeCount
                    }
                    data.isLike = 1
                    initLikeView(subscriptions, viewHolder, type, data)

                    subscriptions.add(RRetrofit.create(NewsService::class.java)
                            .like(Param.buildInfoMap("type:$type", "id:" + data.getDiscussId("info_comment"), "uid:" + UserCache.getUserAccount()))
                            .compose(Rx.transformer(String::class.java))
                            .subscribe(object : BaseSingleSubscriber<String>() {
                                override fun onSucceed(bean: String?) {
                                    super.onSucceed(bean)
                                    //T_.show(bean)
                                }
                            }))
                }
            }
        }
    }

    override fun getTitleBar(): TitleBarPattern {
        return super.getTitleBar()
                .setShowBackImageView(true)
                .setTitleString(getString(R.string.information_detail_title))
                .addRightItem(TitleBarPattern.buildImage(R.drawable.more) {
                    showShareDialog()
                }.setVisibility(View.GONE))
    }

    /**分享对话框*/
    private fun showShareDialog() {
//        if (BuildConfig.SHOW_DEBUG) {
//            webView.clearFormData()
//            webView.clearCache(true)
//            webView.clearDisappearingChildren()
//            webView.reload()
//
//            return
//        }
        if (hotInfoBean != null) {
        } else {
            hotInfoBean = HotInfoListBean.from(detailBean)
        }
        startIView(DynamicShareDialog(hotInfoBean, mSubscriptions))
    }

    override fun inflateContentLayout(baseContentLayout: ContentLayout?, inflater: LayoutInflater?) {
        inflate(R.layout.view_information_detail)
    }

    lateinit var mTabLayout: SlidingTabLayout
    lateinit var mUiViewPager: UIViewPager
    lateinit var loadView: EmptyView
    lateinit var tabControlLayout: View
    lateinit var webView: X5WebView
    lateinit var stickLayout2: StickLayout2
    var mCommentListUIView: CommentListUIView? = null
//    lateinit var webLayout: FrameLayout

    override fun initOnShowContentLayout() {
        super.initOnShowContentLayout()
//        webLayout = mViewHolder.v(R.id.web_contain_layout)
        webView = mViewHolder.v(R.id.web_view)
        val settings = webView.settings
        settings.setUserAgent(settings.userAgentString + " KLG_Android")
//        webView = X5WebView(mActivity)
//        webLayout.addView(webView, -1, -2)
        mTabLayout = mViewHolder.v(R.id.tab_layout)
        mUiViewPager = mViewHolder.v(R.id.view_pager)
        loadView = mViewHolder.v(R.id.load_view)
        tabControlLayout = mViewHolder.v(R.id.tab_control_layout)

        stickLayout2 = mViewHolder.v(R.id.stick_layout)
        stickLayout2.setEdgeScroll(true)

        mTabLayout.setItemNoBackground(true)

        postDelayed(300) {
            initViewPager()
            initTabLayout()
        }
        initWebView()

        getDetail()
    }

    private fun initLikeView() {
        val likeView: RTextView = mViewHolder.v(R.id.like_view)
        val noLikeView: RTextView = mViewHolder.v(R.id.no_like_view)

        likeView.visibility = View.VISIBLE
        noLikeView.visibility = View.VISIBLE

        val line = getDimensionPixelOffset(R.dimen.base_line)

        //喜欢按钮
        likeView.text = detailBean.like.toString()
        if (detailBean.like_or_tread == 1) {
            //喜欢
            likeView.setLeftIco(R.drawable.dianzan_s)
            likeView.setTextColor(Color.parseColor("#FC6B38"))
            UI.setBackgroundDrawable(likeView, ResUtil.createDrawable(Color.parseColor("#FC6B38"), Color.TRANSPARENT, line, 10000f))

            likeView.setOnClickListener(null)
        } else {
            //不喜欢, 未操作
            likeView.setLeftIco(R.drawable.dianzan_n)
            likeView.setTextColor(Color.parseColor("#333333"))
            UI.setBackgroundDrawable(likeView, ResUtil.createDrawable(getColor(R.color.line_color), Color.TRANSPARENT, line, 10000f))

            likeView.setOnClickListener {
                detailBean.like_or_tread = 1
                detailBean.like++

                initLikeView()
            }
        }

        //不喜欢按钮
        UI.setBackgroundDrawable(noLikeView, ResUtil.createDrawable(getColor(R.color.line_color), Color.TRANSPARENT, line, 10000f))
        noLikeView.setOnClickListener {
            //            val dialog = UIItemDialog.build()
//                    .setUseFullItem(true)
//                    .setShowCancelButton(false)
//                    .setDialogTitle(getString(R.string.not_like))
//
//
//            dialog.showDialog(mParentILayout)
            mParentILayout.startIView(NoLikeDialog())
        }
    }

    private fun initTagsLayout() {
        val flowLayout: RFlowLayout = mViewHolder.v(R.id.flow_layout)
        val tagList = detailBean.tagList
        if (tagList != null && tagList.isNotEmpty()) {
            tagList.map {
                flowLayout.addTagTextView(it)
            }
        } else {
            flowLayout.visibility = View.GONE
        }
    }

    override fun onViewShow(bundle: Bundle?) {
        super.onViewShow(bundle)
        webView.onResume()
        webView.resumeTimers()
    }

    override fun onViewHide() {
        super.onViewHide()
        webView.onPause()
        webView.pauseTimers()
    }

    /**收藏按钮*/
    private fun initCollectView() {
        val imageView = mViewHolder.v<ImageView>(R.id.collect_view)
        if (detailBean.collect == 1) {
            imageView.setImageResource(R.drawable.shouchang_pinglun_s)
            imageView.setOnClickListener {
                add(RRetrofit.create(NewsService::class.java)
                        .uncollect(Param.buildInfoMap("id:" + id, "uid:" + UserCache.getUserAccount()))
                        .compose(Rx.transformer(String::class.java))
                        .subscribe(object : BaseSingleSubscriber<String>() {
                            override fun onSucceed(bean: String?) {
                                super.onSucceed(bean)
                                T_.show(getString(R.string.uncollect_success))
                                detailBean.collect = 0
                                initCollectView()
                            }
                        }))
            }
        } else {
            imageView.setImageResource(R.drawable.shouchang_pinglun_n)
            imageView.setOnClickListener {
                add(RRetrofit.create(NewsService::class.java)
                        .collect(Param.buildInfoMap("id:" + id, "uid:" + UserCache.getUserAccount()))
                        .compose(Rx.transformer(String::class.java))
                        .subscribe(object : BaseSingleSubscriber<String>() {
                            override fun onSucceed(bean: String?) {
                                super.onSucceed(bean)
                                T_.show(getString(R.string.collect_success))
                                detailBean.collect = 1
                                initCollectView()
                            }
                        }))
            }
        }
    }

    private fun initWebView() {
        showLoadView()
        webView.loadUrl(getDetailUrl(id))
        //webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webView.settings.useWideViewPort = false
        webView.onWebViewListener = object : X5WebView.OnWebViewListener {
            override fun onPageFinished(webView: WebView?, url: String?) {
                L.e("call: onPageFinished -> ")
//                L.e("onPageFinished_1 测量高度:" + webView?.measuredHeight + " 内容高度:" + webView?.contentHeight + " Range:" + webView?.computeVerticalScrollRange())
//
//                webView?.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
//
//                L.e("onPageFinished_2 测量高度:" + webView?.measuredHeight + " 内容高度:" + webView?.contentHeight + " Range:" + webView?.computeVerticalScrollRange())
                postDelayed({ invokeJs("showImg") }, 1000)
            }

            override fun onProgressChanged(webView: WebView?, progress: Int) {
                L.e("call: onProgressChanged -> $progress")

//                L.e("onPageFinished__$progress 测量高度:" + webView?.measuredHeight +
//                        " 内容高度:" + webView?.contentHeight +
//                        " Range:" + webView?.computeVerticalScrollRange() +
//                        " "
//                )

//                invokeJs("window.android.bodyHeight", "'test'")
//                invokeJs("window.android.bodyHeight", "document.getElementsByTagName('html')[0].scrollHeight)")

                if (progress >= 50) {
                    loadView.visibility = View.GONE
                }
                if (progress >= 80) {
                    tabControlLayout.visibility = View.VISIBLE
                    mUiViewPager.visibility = View.VISIBLE
                    hideLoadView()
                }
            }

            override fun onOverScroll(scrollY: Int) {
            }

            override fun onScroll(left: Int, top: Int, dx: Int, dy: Int) {
            }

            override fun onReceivedTitle(webView: WebView?, title: String?) {
            }
        }

        webView.addJavascriptInterface(object : AndroidJs {

            @JavascriptInterface
            fun bodyHeight(height: String) {
                L.e("call: bodyHeight -> 高度: $height")
                T_.show("高度: $height")
            }

            @JavascriptInterface
            fun onImageClick(images: String, position: Int) {
                //T_.show("$position $images")
                L.i("call: onImageClick $position $images -> ")
                ImagePagerUIView.start(mParentILayout, stickLayout2, PhotoPager.getImageItems(RUtils.split(images)), position)
                        .setPhotoViewLongClickListener(ImagePagerUIView.SavePhotoLongClickListener(mParentILayout))
            }

            @JavascriptInterface
            fun onVideoClick(thumbUrl: String, videoUrl: String) {
                //T_.show("$thumbUrl $videoUrl")
                L.i("call: onVideoClick  $thumbUrl $videoUrl -> ")
                startIView(VideoPlayUIView(thumbUrl, videoUrl))
            }

        }, "android")
    }

    private fun invokeJs(method: String, params: String? = null) {
        val js = "javascript:$method(${params ?: ""});"
        L.e("call: invokeJs -> $js")
        webView.loadUrl(js)
    }

    private fun initViewPager() {
        mUiViewPager.adapter = object : UIPagerAdapter() {
            override fun getIView(position: Int): IView {
                mCommentListUIView = CommentListUIView(id, true)
                mCommentListUIView?.bindParentILayout(mParentILayout)
                return mCommentListUIView!!
            }

            override fun getCount(): Int {
                return 1
            }

            override fun getPageTitle(position: Int): CharSequence {
                return getString(R.string.all_comment_tip)
            }
        }
    }

    private fun initTabLayout() {
        TabLayoutUtil.initSlidingTab(mTabLayout, mUiViewPager, null)
    }

    /**评论, 收藏, 转发, 点赞*/
    private fun initControlLayout() {
        initCollectView()
        initLikeView(mSubscriptions, mViewHolder, "comment", detailBean)

        //点击打开评论对话框
        click(R.id.input_tip_view) {
            startIView(CommentInputDialog(object : CommentInputDialog.InputConfig {
                override fun onInitDialogLayout(viewHolder: RBaseViewHolder) {
                    viewHolder.v<View>(R.id.control_layout).visibility = View.GONE
                    viewHolder.v<View>(R.id.line_view).visibility = View.GONE

                    val editText: ExEditText = viewHolder.v(R.id.input_view)
                    editText.unableCallback()
                }

                override fun onSendClick(imagePath: String, content: String?) {
                    comment(content)
                }
            }))
        }

        click(R.id.bottom_forward_item) { startIView(PublishDynamicUIView2(hotInfoBean /*detailBean*/)) }
    }

    /**
     * 发布评论
     */
    private fun comment(content: String?) {
        add(RRetrofit.create(NewsService::class.java)
                .reply(Param.buildInfoMap("type:new", "content:" + content, "id:" + id, "uid:" + UserCache.getUserAccount()))
                .compose(Rx.transformer(String::class.java))
                .doOnSubscribe { showLoadView() }
                .subscribe(object : BaseSingleSubscriber<String>() {

                    override fun onSucceed(bean: String) {
                        //T_.show(bean)
                        mCommentListUIView?.onComment()
                        mCommentListUIView?.loadData()
                        mCommentListUIView?.scrollToTop()
                    }

                    override fun onEnd(isError: Boolean, isNoNetwork: Boolean, e: RException?) {
                        super.onEnd(isError, isNoNetwork, e)
                        hideLoadView()
                    }
                }))
    }

    /**获取资讯详情*/
    private fun getDetail() {
        add(RRetrofit
                .create(NewsService::class.java)
                .detail(Param.buildInfoMap("meta:1", "id:" + id, "uid:" + UserCache.getUserAccount()))
                .compose(Rx.transformer(InformationDetailBean::class.java))
                .subscribe(object : BaseSingleSubscriber<InformationDetailBean>() {
                    override fun onSucceed(bean: InformationDetailBean?) {
                        if (bean != null) {
                            detailBean = bean

                            postDelayed(300) {
                                initControlLayout()
                                initTagsLayout()
                                initLikeView()
                                try {
                                    initOtherListLayout(detailBean.tagList[0])//有些时候会没有tag
                                } catch(e: Exception) {
                                }

                                mViewHolder.v<View>(R.id.line2).visibility = View.VISIBLE
                                mViewHolder.v<View>(R.id.line3).visibility = View.VISIBLE
                            }

                            uiTitleBarContainer.showRightItem(0)
                        }
                    }

                    override fun onEnd(isError: Boolean, isNoNetwork: Boolean, e: RException?) {
                        super.onEnd(isError, isNoNetwork, e)
                        if (isError) {
                            uiTitleBarContainer.hideRightItem(0)
                        }
                    }
                })
        )
    }

    /**相关阅读填充*/
    private fun initOtherListLayout(classify: String) {
        add(RRetrofit
                .create(SearchService::class.java)
                .search(Param.buildInfoMap("type:news",
                        "content:" + classify,
                        "amount:4"))
                .compose(Rx.transformer(NewsBean::class.java))
                .subscribe(object : BaseSingleSubscriber<NewsBean>() {

                    override fun onSucceed(bean: NewsBean?) {
                        if (bean == null) {

                        } else {
                            bean.news.map { b ->
                                val viewGroup: LinearLayout = mViewHolder.v(R.id.other_like_layout)
                                val item = TextView(mActivity)
                                item.text = b.title
                                item.gravity = Gravity.CENTER_VERTICAL

                                val offset = getDimensionPixelOffset(R.dimen.base_xhdpi)
                                item.setPadding(offset, 0, offset, 0)

                                item.setOnClickListener { mParentILayout.startIView(InformationDetailUIView(b.id.toString())) }
                                viewGroup.addView(item, LinearLayout.LayoutParams(-1, getDimensionPixelOffset(R.dimen.base_item_size)))
                            }
                        }
                    }
                })
        )
    }

    /**选中不喜欢的位置*/
    val noLikePositionSet = hashSetOf<Int>()

    /**资讯不喜欢对话框*/
    private inner class NoLikeDialog : UIItemDialog() {
        init {
            setDialogTitle(this@InformationDetailUIView.getString(R.string.not_like))
            setUseFullItem(true)
            setShowCancelButton(false)
        }

        override fun inflateItem() {
            //per.inflateItem()

            mItemContentLayout.dividerPadding = getDimensionPixelOffset(R.dimen.base_40dpi)

            detailBean.tagList.mapIndexed { index, string ->
                LayoutInflater.from(mActivity).inflate(R.layout.no_like_selector_item, mItemContentLayout)
                val view = mItemContentLayout.getChildAt(index)
                val textView: RTextCheckView? = view.v(R.id.text_view)
                val cancelView: TextView? = view.v(R.id.cancel_view)

                textView?.gravity = Gravity.LEFT
                textView?.setTextColor(ResUtil.generateTextColor(Color.parseColor("#CCCCCC"),
                        Color.parseColor("#CCCCCC"),
                        Color.parseColor("#333333")))

                textView?.text = getString(R.string.no_like_format2, string)
                cancelView?.setTextColor(SkinHelper.getSkin().themeSubColor)

                textView?.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        textView?.text = getString(R.string.is_no_like_format2, string)
                        cancelView?.visibility = View.VISIBLE
                        noLikePositionSet.add(index)
                    } else {
                        textView?.text = getString(R.string.no_like_format2, string)
                        cancelView?.visibility = View.GONE
                        noLikePositionSet.remove(index)
                    }
                }

                textView?.isChecked = noLikePositionSet.contains(index)
            }

            val okTextView = TextView(mActivity)
            okTextView.text = getString(R.string.ok)
            okTextView.gravity = Gravity.CENTER
            okTextView.setTextColor(SkinHelper.getSkin().themeSubColor)
            okTextView.setOnClickListener {
                finishDialog()

                val builder = StringBuilder()
                if (noLikePositionSet.isEmpty()) {
                    builder.append(detailBean.id)
                    builder.append(";")

                    T_.show(getString(R.string.modify_successed))
                } else {
                    noLikePositionSet.forEach {
                        builder.append(detailBean.tagList[it])
                        builder.append(";")
                    }
                    T_.show(getString(R.string.no_like_successed_tip))
                }

                icFeedback = true
                add(RRetrofit
                        .create(NewsService::class.java)
                        .feedback(Param.buildInfoMap("uid:" + UserCache.getUserAccount(),
                                "id:" + detailBean.id, "content:" + RUtils.safe(builder)))
                        .compose(Rx.transformer(String::class.java))
                        .subscribe(object : BaseSingleSubscriber<String>() {

                        })
                )
            }

            mItemContentLayout.addView(okTextView,
                    ViewGroup.LayoutParams(-1, getDimensionPixelOffset(R.dimen.base_item_size)))
        }

        override fun createItem(info: ItemInfo): View {
            return super.createItem(info)
        }
    }

    /**选择不喜欢之后的回调*/
    var noLikeAction: Action0? = null
    var icFeedback = false

    override fun onViewUnload() {
        super.onViewUnload()
        if (icFeedback && noLikePositionSet.isNotEmpty()) {
            noLikeAction?.call()
        }
    }
}


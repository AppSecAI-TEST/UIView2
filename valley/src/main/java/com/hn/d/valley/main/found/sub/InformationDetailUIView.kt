package com.hn.d.valley.main.found.sub

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.angcyo.uiview.design.StickLayout2
import com.angcyo.uiview.github.goodview.GoodView
import com.angcyo.uiview.github.tablayout.SlidingTabLayout
import com.angcyo.uiview.github.tablayout.TabLayoutUtil
import com.angcyo.uiview.model.TitleBarPattern
import com.angcyo.uiview.net.RRetrofit
import com.angcyo.uiview.net.Rx
import com.angcyo.uiview.recycler.RBaseViewHolder
import com.angcyo.uiview.utils.T_
import com.angcyo.uiview.view.IView
import com.angcyo.uiview.widget.EmptyView
import com.angcyo.uiview.widget.viewpager.UIPagerAdapter
import com.angcyo.uiview.widget.viewpager.UIViewPager
import com.hn.d.valley.R
import com.hn.d.valley.base.BaseContentUIView
import com.hn.d.valley.base.Param
import com.hn.d.valley.base.rx.BaseSingleSubscriber
import com.hn.d.valley.bean.ILikeData
import com.hn.d.valley.bean.InformationDetailBean
import com.hn.d.valley.cache.UserCache
import com.hn.d.valley.service.NewsService
import com.hn.d.valley.sub.user.sub.CommentInputDialog
import com.hn.d.valley.sub.user.sub.CommentListUIView
import com.hn.d.valley.widget.HnItemTextView
import com.hn.d.valley.x5.X5WebView
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
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
class InformationDetailUIView(var detailId: String) : BaseContentUIView() {

    companion object {
        fun getDetailUrl(id: String) = "http://news.klgwl.com/wap/news/$id"

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
                    subscriptions.add(RRetrofit.create(NewsService::class.java)
                            .unlike(Param.buildMap("type:comment", "id:" + data.getDiscussId("info_comment"), "uid:" + UserCache.getUserAccount()))
                            .compose(Rx.transformer(String::class.java))
                            .subscribe(object : BaseSingleSubscriber<String>() {
                                override fun onSucceed(bean: String?) {
                                    super.onSucceed(bean)
                                    //T_.show(bean)
                                    data.isLike = 2
                                    initLikeView(subscriptions, viewHolder, type, data)
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
                    subscriptions.add(RRetrofit.create(NewsService::class.java)
                            .like(Param.buildInfoMap("type:comment", "id:" + data.getDiscussId("info_comment"), "uid:" + UserCache.getUserAccount()))
                            .compose(Rx.transformer(String::class.java))
                            .subscribe(object : BaseSingleSubscriber<String>() {
                                override fun onSucceed(bean: String?) {
                                    super.onSucceed(bean)
                                    //T_.show(bean)
                                    data.isLike = 1
                                    initLikeView(subscriptions, viewHolder, type, data)
                                }
                            }))
                }
            }
        }
    }

    override fun getTitleBar(): TitleBarPattern {
        return super.getTitleBar().setShowBackImageView(true).setTitleString(getString(R.string.information_detail_title))
    }

    override fun inflateContentLayout(baseContentLayout: RelativeLayout?, inflater: LayoutInflater?) {
        inflate(R.layout.view_information_detail)
    }

    lateinit var mTabLayout: SlidingTabLayout
    lateinit var mUiViewPager: UIViewPager
    lateinit var loadView: EmptyView
    lateinit var tabControlLayout: View
    lateinit var webView: X5WebView
    lateinit var detailBean: InformationDetailBean
    var mCommentListUIView: CommentListUIView? = null

    override fun initOnShowContentLayout() {
        super.initOnShowContentLayout()

        webView = mViewHolder.v(R.id.web_view)
        mTabLayout = mViewHolder.v(R.id.tab_layout)
        mUiViewPager = mViewHolder.v(R.id.view_pager)
        loadView = mViewHolder.v(R.id.load_view)
        tabControlLayout = mViewHolder.v(R.id.tab_control_layout)

        val stickLayout2: StickLayout2 = mViewHolder.v(R.id.stick_layout)
        stickLayout2.setEdgeScroll(true)

        mTabLayout.setItemNoBackground(true)

        initViewPager()
        initTabLayout()
        initWebView()

        getDetail()
    }

    /**收藏按钮*/
    private fun initCollectView() {
        val imageView = mViewHolder.v<ImageView>(R.id.collect_view)
        if (detailBean.collect == 1) {
            imageView.setImageResource(R.drawable.shouchang_pinglun_s)
            imageView.setOnClickListener {
                add(RRetrofit.create(NewsService::class.java)
                        .uncollect(Param.buildInfoMap("id:" + detailId, "uid:" + UserCache.getUserAccount()))
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
                        .collect(Param.buildInfoMap("id:" + detailId, "uid:" + UserCache.getUserAccount()))
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
        webView.loadUrl(getDetailUrl(detailId))
        webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        webView.onWebViewListener = object : X5WebView.OnWebViewListener {
            override fun onPageFinished(webView: WebView?, url: String?) {
            }

            override fun onProgressChanged(webView: WebView?, progress: Int) {
                if (progress > 40) {
                    loadView.visibility = View.GONE
                }
                if (progress > 80) {
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
    }

    private fun initViewPager() {
        mUiViewPager.adapter = object : UIPagerAdapter() {
            override fun getIView(position: Int): IView {
                mCommentListUIView = CommentListUIView(detailId, true)
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
                    viewHolder.v<View>(R.id.ico_pic).visibility = View.GONE
                    viewHolder.v<View>(R.id.ico_gif).visibility = View.GONE
                    viewHolder.v<View>(R.id.ico_at).visibility = View.GONE
                }

                override fun onSendClick(imagePath: String, content: String?) {
                    comment(content)
                }
            }))
        }
    }


    /**
     * 发布评论
     */
    private fun comment(content: String?) {
        add(RRetrofit.create(NewsService::class.java)
                .reply(Param.buildInfoMap("type:new", "content:" + content, "id:" + detailId, "uid:" + UserCache.getUserAccount()))
                .compose(Rx.transformer(String::class.java))
                .doOnSubscribe { showLoadView() }
                .subscribe(object : BaseSingleSubscriber<String>() {

                    override fun onSucceed(bean: String) {
                        //T_.show(bean)
                        mCommentListUIView?.loadData()
                        mCommentListUIView?.scrollToTop()
                    }

                    override fun onEnd() {
                        super.onEnd()
                        hideLoadView()
                    }
                }))
    }

    /**获取资讯详情*/
    private fun getDetail() {
        add(RRetrofit
                .create(NewsService::class.java)
                .detail(Param.buildInfoMap("meta:1", "id:" + detailId, "uid:" + UserCache.getUserAccount()))
                .compose(Rx.transformer(InformationDetailBean::class.java))
                .subscribe(object : BaseSingleSubscriber<InformationDetailBean>() {
                    override fun onSucceed(bean: InformationDetailBean?) {
                        if (bean != null) {
                            detailBean = bean

                            initControlLayout()
                        }
                    }
                })
        )
    }
}
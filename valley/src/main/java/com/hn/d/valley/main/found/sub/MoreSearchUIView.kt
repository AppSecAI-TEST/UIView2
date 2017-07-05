package com.hn.d.valley.main.found.sub

import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.View
import android.view.View.GONE
import android.widget.ImageView
import com.angcyo.uiview.net.RException
import com.angcyo.uiview.net.RRetrofit
import com.angcyo.uiview.net.Rx
import com.angcyo.uiview.recycler.RBaseViewHolder
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter
import com.angcyo.uiview.utils.RUtils
import com.angcyo.uiview.widget.GlideImageView
import com.angcyo.uiview.widget.RTextImageLayout
import com.angcyo.uiview.widget.RTextView
import com.hn.d.valley.R
import com.hn.d.valley.base.Param
import com.hn.d.valley.base.constant.Constant
import com.hn.d.valley.base.rx.BaseSingleSubscriber
import com.hn.d.valley.bean.OtherSearchBean
import com.hn.d.valley.main.message.service.SearchService
import com.hn.d.valley.sub.other.SingleRecyclerUIView
import com.hn.d.valley.widget.HnExTextView
import com.hn.d.valley.widget.HnGlideImageView
import com.hn.d.valley.x5.X5WebUIView

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：更多搜索
 * 创建人员：Robi
 * 创建时间：2017/06/23 18:07
 * 修改人员：Robi
 * 修改时间：2017/06/23 18:07
 * 修改备注：
 * Version: 1.0.0
 */
class MoreSearchUIView(var searchText: String) : SingleRecyclerUIView<OtherSearchBean>() {

    override fun getTitleResource(): Int {
        return R.string.more_search
    }

    override fun isLoadInViewPager(): Boolean {
        return false
    }

    override fun loadData() {
        super.loadData()
        page = 0
    }

    override fun onUILoadData(page: String) {
        super.onUILoadData(page)
        showLoadView()
        add(RRetrofit.create(SearchService::class.java)
                .search(Param.buildInfoMap("type:outnet",
                        "content:$searchText",
                        "amount:${Constant.DEFAULT_PAGE_DATA_COUNT}"
                        , "page$page"
                ))
                .compose(Rx.transformerList(OtherSearchBean::class.java))
                .subscribe(object : BaseSingleSubscriber<List<OtherSearchBean>>() {
                    override fun onSucceed(bean: List<OtherSearchBean>?) {
                        super.onSucceed(bean)
                        if (bean == null || bean.isEmpty()) {
                            onUILoadDataEnd(null)
                        } else {
                            onUILoadDataEnd(bean)
                        }
                    }

                    override fun onEnd(isError: Boolean, isNoNetwork: Boolean, e: RException?) {
                        super.onEnd(isError, isNoNetwork, e)
                        hideLoadView()
                        if (isError) {
                            showNonetLayout { loadData() }
                        }
                    }
                })
        )
    }

    override fun initRExBaseAdapter(): RExBaseAdapter<String, OtherSearchBean, String> {
        return object : RExBaseAdapter<String, OtherSearchBean, String>(mActivity) {
            override fun getItemLayoutId(viewType: Int): Int {
                return R.layout.item_search_result_article
            }

            override fun onBindDataView(holder: RBaseViewHolder, posInData: Int, dataBean: OtherSearchBean) {
                super.onBindDataView(holder, posInData, dataBean)
                holder.v<View>(R.id.delete_view).visibility = View.GONE

                holder.tv(R.id.author_view).text = "来自百度搜索"

                holder.tv(R.id.reply_cnt_view).visibility = View.GONE

                val imageView = holder.v<HnGlideImageView>(R.id.image_view)
                imageView.visibility = GONE

                holder.itemView.setOnClickListener { startIView(X5WebUIView(dataBean.link)) }

                val textImageLayout = holder.v<RTextImageLayout>(R.id.text_image_layout)
                textImageLayout.setConfigTextView(RTextImageLayout.ConfigTextView { textView ->
                    if (textView == null) {
                        return@ConfigTextView HnExTextView(holder.context)
                    }
                    textView.setTextColor(ContextCompat.getColor(holder.context, R.color.main_text_color))
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            holder.context.resources.getDimensionPixelOffset(R.dimen.default_text_size16).toFloat())
                    textView
                })
                textImageLayout.setText(dataBean.title)
                textImageLayout.setConfigCallback(object : RTextImageLayout.ConfigCallback {
                    override fun getImageSize(position: Int): IntArray? {
                        return null
                    }

                    override fun onCreateImageView(imageView: GlideImageView) {
                        imageView.setImageResource(R.drawable.zhanweitu_1)
                    }

                    override fun displayImage(imageView: GlideImageView, url: String) {
                        imageView.setShowGifTip(false)
                        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                        HotInfoListUIView.displayImage(imageView, url)
                    }

                    override fun isVideoType(): Boolean {
                        return false
                    }
                })
                textImageLayout.setImages(RUtils.split(dataBean.img, ";"))

                val textView = textImageLayout.textView
                if (textView is RTextView) {
                    textView.setHighlightWord(searchText)
                }
            }
        }
    }
}
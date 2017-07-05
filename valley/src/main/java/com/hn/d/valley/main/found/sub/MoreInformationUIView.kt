package com.hn.d.valley.main.found.sub

import android.view.View
import com.angcyo.uiview.net.RException
import com.angcyo.uiview.net.RRetrofit
import com.angcyo.uiview.net.Rx
import com.angcyo.uiview.recycler.RBaseViewHolder
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter
import com.hn.d.valley.R
import com.hn.d.valley.base.Param
import com.hn.d.valley.base.constant.Constant
import com.hn.d.valley.base.rx.BaseSingleSubscriber
import com.hn.d.valley.bean.HotInfoListBean
import com.hn.d.valley.bean.NewsBean
import com.hn.d.valley.main.message.service.SearchService
import com.hn.d.valley.sub.other.SingleRecyclerUIView

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：更多资讯
 * 创建人员：Robi
 * 创建时间：2017/06/23 16:53
 * 修改人员：Robi
 * 修改时间：2017/06/23 16:53
 * 修改备注：
 * Version: 1.0.0
 */
class MoreInformationUIView(var searchText: String) : SingleRecyclerUIView<HotInfoListBean>() {

    /**分页加载*/
    var offset: Int = 0

    override fun getTitleResource(): Int {
        return R.string.more_information
    }

    override fun isLoadInViewPager(): Boolean {
        return false
    }

    override fun onUILoadData(page: String?) {
        super.onUILoadData(page)
        showLoadView()
        add(RRetrofit.create(SearchService::class.java)
                .search(Param.buildInfoMap("type:news",
                        "content:$searchText",
                        "amount:${Constant.DEFAULT_PAGE_DATA_COUNT}"
                        , "offset$offset"
                ))
                .compose(Rx.transformer(NewsBean::class.java))
                .subscribe(object : BaseSingleSubscriber<NewsBean>() {
                    override fun onSucceed(bean: NewsBean?) {
                        super.onSucceed(bean)
                        if (bean == null || bean.news.isEmpty()) {
                            onUILoadDataEnd(null)
                        } else {
                            offset += bean.news.size
                            onUILoadDataEnd(bean.news)
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

    override fun initRExBaseAdapter(): RExBaseAdapter<String, HotInfoListBean, String> {
        return object : RExBaseAdapter<String, HotInfoListBean, String>(mActivity) {
            override fun getItemLayoutId(viewType: Int): Int {
                return R.layout.item_search_result_article
            }

            override fun onBindDataView(holder: RBaseViewHolder, posInData: Int, dataBean: HotInfoListBean) {
                super.onBindDataView(holder, posInData, dataBean)
                HotInfoListUIView.initItem(mParentILayout, holder, dataBean, searchText)
                holder.v<View>(R.id.delete_view).visibility = View.GONE
            }
        }
    }
}
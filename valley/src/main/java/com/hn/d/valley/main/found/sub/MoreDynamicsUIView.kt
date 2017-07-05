package com.hn.d.valley.main.found.sub

import android.view.View
import com.angcyo.uiview.container.ILayout
import com.angcyo.uiview.net.RException
import com.angcyo.uiview.net.RRetrofit
import com.angcyo.uiview.net.Rx
import com.angcyo.uiview.recycler.RBaseViewHolder
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter
import com.angcyo.uiview.utils.TimeUtil
import com.angcyo.uiview.widget.RTextView
import com.hn.d.valley.R
import com.hn.d.valley.base.Param
import com.hn.d.valley.base.constant.Constant
import com.hn.d.valley.base.rx.BaseSingleSubscriber
import com.hn.d.valley.bean.DynamicsBean
import com.hn.d.valley.bean.LikeUserInfoBean
import com.hn.d.valley.control.UserDiscussItemControl
import com.hn.d.valley.main.message.service.SearchService
import com.hn.d.valley.sub.adapter.UserInfoAdapter
import com.hn.d.valley.sub.other.SingleRecyclerUIView

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：更多动态
 * 创建人员：Robi
 * 创建时间：2017/06/23 16:53
 * 修改人员：Robi
 * 修改时间：2017/06/23 16:53
 * 修改备注：
 * Version: 1.0.0
 */
class MoreDynamicsUIView(var searchText: String) : SingleRecyclerUIView<LikeUserInfoBean>() {

    /**分页加载*/
    var offset: Int = 0

    override fun getTitleResource(): Int {
        return R.string.more_status
    }

    override fun isLoadInViewPager(): Boolean {
        return false
    }

    override fun loadData() {
        super.loadData()
        offset = 0
    }

    override fun onUILoadData(page: String?) {
        super.onUILoadData(page)
        showLoadView()
        add(RRetrofit.create(SearchService::class.java)
                .search(Param.buildInfoMap("type:dynamic",
                        "content:$searchText",
                        "amount:${Constant.DEFAULT_PAGE_DATA_COUNT}"
                        , "offset$offset"
                ))
                .compose(Rx.transformer(DynamicsBean::class.java))
                .subscribe(object : BaseSingleSubscriber<DynamicsBean>() {
                    override fun onSucceed(bean: DynamicsBean?) {
                        super.onSucceed(bean)
                        if (bean == null) {
                            onUILoadDataEnd(null)
                        } else {
                            offset += bean.dynamics.size
                            onUILoadDataEnd(bean.dynamics)
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

    override fun initRExBaseAdapter(): RExBaseAdapter<String, LikeUserInfoBean, String> {
        return object : RExBaseAdapter<String, LikeUserInfoBean, String>(mActivity) {
            override fun getItemLayoutId(viewType: Int): Int {
                return R.layout.item_search_result_user
            }

            override fun onBindDataView(holder: RBaseViewHolder, posInData: Int, dataBean: LikeUserInfoBean) {
                super.onBindDataView(holder, posInData, dataBean)
                initDynamicsItemLayout(holder, dataBean, mILayout, searchText)
            }
        }
    }

    companion object {
        fun initDynamicsItemLayout(holder: RBaseViewHolder, dataBean: LikeUserInfoBean, mILayout: ILayout<*>, highlightWord: String?) {
            UserInfoAdapter.initUserItem(holder, dataBean, mILayout)
            holder.tv(R.id.signature).text = dataBean.content
            holder.tv(R.id.time_text_view).text = TimeUtil.getTimeShowString(java.lang.Long.valueOf(dataBean.created)!! * 1000, false)

            val nameView: RTextView = holder.v(R.id.username)
            val signView: RTextView = holder.v(R.id.signature)
            if (!highlightWord.isNullOrEmpty()) {
                nameView.setHighlightWord(highlightWord)
                signView.setHighlightWord(highlightWord)
            }

            holder.v<View>(R.id.follow_image_view).visibility = View.GONE

            holder.itemView.setOnClickListener({ UserDiscussItemControl.jumpToDynamicDetailUIView(mILayout, dataBean.id, false, false, false) })
        }
    }
}
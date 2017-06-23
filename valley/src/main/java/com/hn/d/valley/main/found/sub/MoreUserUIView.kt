package com.hn.d.valley.main.found.sub

import android.view.View
import com.angcyo.uiview.net.RRetrofit
import com.angcyo.uiview.net.Rx
import com.angcyo.uiview.recycler.RBaseViewHolder
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter
import com.angcyo.uiview.widget.RTextView
import com.hn.d.valley.R
import com.hn.d.valley.base.Param
import com.hn.d.valley.base.constant.Constant
import com.hn.d.valley.base.rx.BaseSingleSubscriber
import com.hn.d.valley.bean.LikeUserInfoBean
import com.hn.d.valley.main.message.service.SearchService
import com.hn.d.valley.sub.adapter.UserInfoAdapter
import com.hn.d.valley.sub.other.SingleRecyclerUIView

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：更多用户
 * 创建人员：Robi
 * 创建时间：2017/06/23 16:53
 * 修改人员：Robi
 * 修改时间：2017/06/23 16:53
 * 修改备注：
 * Version: 1.0.0
 */
class MoreUserUIView(var searchText: String) : SingleRecyclerUIView<LikeUserInfoBean>() {

    override fun getTitleResource(): Int {
        return R.string.more_user
    }

    override fun isLoadInViewPager(): Boolean {
        return false
    }

    override fun onUILoadData(page: String?) {
        super.onUILoadData(page)
        showLoadView()
        add(RRetrofit.create(SearchService::class.java)
                .search(Param.buildInfoMap("type:user",
                        "content:$searchText",
                        "amount:${Constant.DEFAULT_PAGE_DATA_COUNT}"
                        , "lastid:$last_id"
                ))
                .compose(Rx.transformerList(LikeUserInfoBean::class.java))
                .subscribe(object : BaseSingleSubscriber<List<LikeUserInfoBean>>() {
                    override fun onSucceed(bean: List<LikeUserInfoBean>) {
                        super.onSucceed(bean)
                        if (bean.isEmpty()) {
                            onUILoadDataEnd(null)
                        } else {
                            last_id = bean[bean.size - 1].id
                            onUILoadDataEnd(bean)
                        }
                    }

                    override fun onEnd(isError: Boolean, errorCode: Int, isNoNetwork: Boolean, e: Throwable?) {
                        super.onEnd(isError, errorCode, isNoNetwork, e)
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
                UserInfoAdapter.initUserItem(holder, dataBean, mILayout)
                UserInfoAdapter.initSignatureItem(holder.tv(R.id.signature), dataBean)

                val nameView = holder.v<RTextView>(R.id.username)
                val signView = holder.v<RTextView>(R.id.signature)
                nameView.setHighlightWord(searchText)
                signView.setHighlightWord(searchText)

                holder.v<View>(R.id.follow_image_view).visibility = View.GONE
            }
        }
    }
}
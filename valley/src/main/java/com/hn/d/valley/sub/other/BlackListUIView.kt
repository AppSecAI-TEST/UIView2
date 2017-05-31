package com.hn.d.valley.sub.other

import com.angcyo.uiview.model.TitleBarPattern
import com.angcyo.uiview.net.RRetrofit
import com.angcyo.uiview.net.Rx
import com.angcyo.uiview.recycler.RBaseViewHolder
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter
import com.hn.d.valley.R
import com.hn.d.valley.base.Param
import com.hn.d.valley.base.rx.BaseSingleSubscriber
import com.hn.d.valley.bean.ReplyListBean
import com.hn.d.valley.main.me.UserDetailUIView2
import com.hn.d.valley.service.ContactService
import com.hn.d.valley.widget.HnGlideImageView

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：黑名单列表
 * 创建人员：Robi
 * 创建时间：2017/05/31 18:18
 * 修改人员：Robi
 * 修改时间：2017/05/31 18:18
 * 修改备注：
 * Version: 1.0.0
 */
class BlackListUIView : SingleRecyclerUIView<ReplyListBean.DataListBean>() {

    override fun getTitleBar(): TitleBarPattern {
        return super.getTitleBar().setTitleString(getString(R.string.black_list_title))
    }

    override fun initRExBaseAdapter(): RExBaseAdapter<String, ReplyListBean.DataListBean, String> {
        return object : RExBaseAdapter<String, ReplyListBean.DataListBean, String>(mActivity) {
            override fun getItemLayoutId(viewType: Int): Int {
                return R.layout.item_single_user_info
            }

            override fun onBindDataView(holder: RBaseViewHolder, posInData: Int, dataBean: ReplyListBean.DataListBean) {
                holder.tv(R.id.text_view).text = dataBean.username
                val glideImageView: HnGlideImageView = holder.v(R.id.image_view)
                glideImageView.setImageThumbUrl(dataBean.avatar)

                holder.itemView.setOnClickListener { startIView(UserDetailUIView2(dataBean.uid)) }
            }
        }
    }

    override fun isLoadInViewPager(): Boolean {
        return false
    }

    override fun onUILoadData(page: String?) {
        showLoadView()
        add(RRetrofit.create(ContactService::class.java)
                .blacklist(Param.buildMap())
                .compose(Rx.transformer(ReplyListBean::class.java))
                .subscribe(object : BaseSingleSubscriber<ReplyListBean>() {
                    override fun onSucceed(bean: ReplyListBean?) {
                        super.onSucceed(bean)
                        if (bean == null || bean.data_list.isEmpty()) {
                            showEmptyLayout()
                        } else {
                            onUILoadDataEnd(bean.data_list)
                        }
                    }

                    override fun onEnd(isError: Boolean, isNoNetwork: Boolean, e: Throwable?) {
                        super.onEnd(isError, isNoNetwork, e)
                        hideLoadView()
                        if (isError) {
                            showNonetLayout { loadData() }
                        }
                    }
                }))
    }
}
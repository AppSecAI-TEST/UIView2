package com.hn.d.valley.sub.other

import com.angcyo.uiview.net.RRetrofit
import com.angcyo.uiview.net.Rx
import com.hn.d.valley.R
import com.hn.d.valley.base.Param
import com.hn.d.valley.base.rx.BaseSingleSubscriber
import com.hn.d.valley.bean.LikeUserModel
import com.hn.d.valley.service.ContactService

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：共同关注 列表页面
 * 创建人员：Robi
 * 创建时间：2017/06/14 17:05
 * 修改人员：Robi
 * 修改时间：2017/06/14 17:05
 * 修改备注：
 * Version: 1.0.0
 */
class RelationListUIView(val to_uid: String) : UserInfoRecyclerUIView() {
    override fun getTitleString(): String {
        return getString(R.string.relation_title)
    }

    override fun onUILoadData(page: String?) {
        super.onUILoadData(page)
        add(RRetrofit.create(ContactService::class.java)
                .sameAttention(Param.buildMap("to_uid:$to_uid"))
                .compose(Rx.transformer(LikeUserModel::class.java))
                .subscribe(object : BaseSingleSubscriber<LikeUserModel>() {

                    override fun onStart() {
                        super.onStart()
                        showLoadView()
                    }

                    override fun onSucceed(bean: LikeUserModel?) {
                        super.onSucceed(bean)
                        if (bean == null || bean.data_list == null || bean.data_list.isEmpty()) {
                            onUILoadDataEnd()
                        } else {
                            onUILoadDataEnd(bean.data_list)
                        }
                    }

                    override fun onEnd() {
                        super.onEnd()
                        hideLoadView()
                    }
                }))
    }
}
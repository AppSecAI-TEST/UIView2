package com.hn.d.valley.main.me

import com.angcyo.uiview.base.Item
import com.angcyo.uiview.base.SingleItem
import com.angcyo.uiview.recycler.RBaseViewHolder
import com.hn.d.valley.R
import com.hn.d.valley.base.BaseItemUIView
import com.hn.d.valley.bean.realm.UserInfoBean
import com.hn.d.valley.widget.HnAuthStatusView
import com.hn.d.valley.widget.HnGlideImageView

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：认证详情
 * 创建人员：Robi
 * 创建时间：2017/06/08 08:24
 * 修改人员：Robi
 * 修改时间：2017/06/08 08:24
 * 修改备注：
 * Version: 1.0.0
 */
class AuthDetailUIView(val userBean: UserInfoBean) : BaseItemUIView() {

    override fun getTitleString(): String {
        return getString(R.string.auth_detail_title)
    }

    override fun getItemLayoutId(viewType: Int): Int {
        return R.layout.item_auth_detail
    }

    override fun createItems(items: MutableList<SingleItem>) {
        items.add(object : SingleItem() {
            override fun onBindView(holder: RBaseViewHolder, posInData: Int, dataBean: Item?) {
                val avatarView: HnGlideImageView = holder.v(R.id.image_view)
                avatarView.setImageThumbUrl(userBean.avatar)
                avatarView.setAuth(userBean.is_auth)

                holder.tv(R.id.user_name_view).text = userBean.username
                holder.tv(R.id.auth_type_view).text = MyAuthUIView.AuthType.from(userBean.auth_type.toInt()).des
                holder.tv(R.id.auth_des_view).text = userBean.auth_desc

                val authStatusView: HnAuthStatusView = holder.v(R.id.auth_status_view)
                authStatusView.setAuthType(userBean.is_auth.toInt())

                holder.tv(R.id.introduce_view).text = userBean.introduce
            }

        })
    }
}
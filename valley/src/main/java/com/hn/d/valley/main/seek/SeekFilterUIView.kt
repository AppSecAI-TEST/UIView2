package com.hn.d.valley.main.seek

import com.angcyo.uiview.base.Item
import com.angcyo.uiview.base.SingleItem
import com.angcyo.uiview.recycler.RBaseViewHolder
import com.hn.d.valley.R
import com.hn.d.valley.base.BaseItemUIView

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：秀场筛选界面
 * 创建人员：Robi
 * 创建时间：2017/08/04 08:35
 * 修改人员：Robi
 * 修改时间：2017/08/04 08:35
 * 修改备注：
 * Version: 1.0.0
 */
class SeekFilterUIView : BaseItemUIView() {

    override fun getItemLayoutId(position: Int): Int {
        return R.layout.view_seek_filter_layout
    }

    override fun createItems(items: MutableList<SingleItem>?) {
        items?.add(object : SingleItem() {
            override fun onBindView(holder: RBaseViewHolder, posInData: Int, dataBean: Item?) {

            }
        })
    }
}
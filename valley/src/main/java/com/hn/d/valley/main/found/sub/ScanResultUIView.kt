package com.hn.d.valley.main.found.sub

import com.angcyo.uiview.base.Item
import com.angcyo.uiview.base.SingleItem
import com.angcyo.uiview.model.TitleBarPattern
import com.angcyo.uiview.recycler.RBaseViewHolder
import com.hn.d.valley.R
import com.hn.d.valley.base.BaseItemUIView

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/06/16 13:54
 * 修改人员：Robi
 * 修改时间：2017/06/16 13:54
 * 修改备注：
 * Version: 1.0.0
 */
class ScanResultUIView(var result: String = "") : BaseItemUIView() {

    override fun getTitleBar(): TitleBarPattern {
        return super.getTitleBar().setTitleString(getString(R.string.scan_result_title))
    }

    override fun getItemLayoutId(viewType: Int): Int {
        return R.layout.view_scan_result
    }

    override fun createItems(items: MutableList<SingleItem>) {
        items.add(object : SingleItem() {
            override fun onBindView(holder: RBaseViewHolder, posInData: Int, dataBean: Item?) {
                holder.tv(R.id.text_view).text = result
            }
        })
    }

}
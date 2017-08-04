package com.hn.d.valley.main.seek

import android.view.View
import com.angcyo.uiview.model.TitleBarPattern
import com.angcyo.uiview.recycler.RBaseItemDecoration
import com.angcyo.uiview.recycler.RBaseViewHolder
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter
import com.angcyo.uiview.recycler.adapter.RModelAdapter
import com.angcyo.uiview.rsen.RefreshLayout
import com.hn.d.valley.R
import com.hn.d.valley.sub.other.SingleRecyclerUIView

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：星座选择
 * 创建人员：Robi
 * 创建时间：2017/08/04 16:22
 * 修改人员：Robi
 * 修改时间：2017/08/04 16:22
 * 修改备注：
 * Version: 1.0.0
 */
class ConstellationSelectorUIView(val default: Int, val onSelector: (String) -> Unit) : SingleRecyclerUIView<String>() {

    override fun getTitleBar(): TitleBarPattern {
        return super.getTitleBar()
                .addRightItem(TitleBarPattern.TitleBarItem("确定") {
                    finishIView()
                    onSelector.invoke(mRExBaseAdapter.allSelectorList[0].toString())
                })
    }

    override fun getTitleString(): String {
        return "筛选星座"
    }

    override fun getDefaultLayoutState(): LayoutState {
        return LayoutState.CONTENT
    }

    override fun createBaseItemDecoration(): RBaseItemDecoration {
        return super.createBaseItemDecoration().setMarginStart(getDimensionPixelOffset(R.dimen.base_xhdpi))
    }

    override fun initRefreshLayout() {
        super.initRefreshLayout()
        mRefreshLayout.setRefreshDirection(RefreshLayout.BOTH)
        mRefreshLayout.setNotifyListener(false)
    }

    override fun initRExBaseAdapter(): RExBaseAdapter<String, String, String> {
        return object : RExBaseAdapter<String, String, String>(mActivity) {

            override fun getItemCount(): Int {
                return 13
            }

            override fun getItemLayoutId(viewType: Int): Int {
                return R.layout.item_constellation_selector
            }

            override fun onBindModelView(model: Int, isSelector: Boolean, holder: RBaseViewHolder, position: Int, bean: String?) {
                super.onBindModelView(model, isSelector, holder, position, bean)
                holder.v<View>(R.id.check_view).visibility = if (isSelector) View.VISIBLE else View.INVISIBLE
            }

            override fun onBindCommonView(holder: RBaseViewHolder, position: Int, bean: String?) {
                super.onBindCommonView(holder, position, bean)
                holder.itemView.setOnClickListener {
                    setSelectorPosition(position)
                }
                holder.tv(R.id.text_view).text = SeekFilterUIView.getConstellationString(position.toString())
            }

        }.apply {
            model = RModelAdapter.MODEL_SINGLE
            addSelectorPosition(default)
        }
    }
}
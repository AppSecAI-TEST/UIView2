package com.hn.d.valley.main.me.setting

import com.angcyo.github.utilcode.utils.AppUtils
import com.angcyo.github.utilcode.utils.PhoneUtils
import com.angcyo.uiview.base.Item
import com.angcyo.uiview.base.SingleItem
import com.angcyo.uiview.recycler.RBaseViewHolder
import com.angcyo.uiview.utils.RUtils
import com.hn.d.valley.R
import com.hn.d.valley.base.BaseItemUIView
import com.hn.d.valley.control.MainControl
import com.hn.d.valley.x5.UseAgreementUIView

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：关于恐龙谷
 * 创建人员：Robi
 * 创建时间：2017/06/08 17:34
 * 修改人员：Robi
 * 修改时间：2017/06/08 17:34
 * 修改备注：
 * Version: 1.0.0
 */
class AboutKlgUIView : BaseItemUIView() {

    companion object {
        const val phone = "0755-26777170"
    }

    override fun getTitleString(): String {
        return getString(R.string.about_klg)
    }

    override fun getItemLayoutId(viewType: Int): Int {
        return R.layout.item_about_klg
    }

    override fun createItems(items: MutableList<SingleItem>) {
        items.add(object : SingleItem() {
            override fun onBindView(holder: RBaseViewHolder, posInData: Int, dataBean: Item?) {
                holder.tv(R.id.version_name_view).text = AppUtils.getAppVersionName(mActivity)

                holder.click(R.id.user_agreement_view) {
                    startIView(UseAgreementUIView())
                }
                holder.click(R.id.maker_to_layout) {
                    RUtils.jumpToMarket(mActivity, mActivity.packageName)
                }
                holder.click(R.id.call_to_layout) {
                    PhoneUtils.dial(phone)
                }
                holder.click(R.id.email_to_layout) {
                    RUtils.emailTo(mActivity, "support@konglonggu.com")
                }
                holder.click(R.id.logo_view) {
                    MainControl.checkVersion(mParentILayout)
                }
            }
        })
    }

    override fun needTransitionExitAnim(): Boolean {
        return true
    }

    override fun needTransitionStartAnim(): Boolean {
        return true
    }
}
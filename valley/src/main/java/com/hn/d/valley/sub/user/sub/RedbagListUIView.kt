package com.hn.d.valley.sub.user.sub

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.angcyo.uiview.container.ContentLayout
import com.angcyo.uiview.model.TitleBarPattern
import com.angcyo.uiview.net.RException
import com.angcyo.uiview.net.RRetrofit
import com.angcyo.uiview.net.Rx
import com.angcyo.uiview.recycler.RBaseViewHolder
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter
import com.angcyo.uiview.rsen.RefreshLayout
import com.angcyo.uiview.utils.TimeUtil
import com.angcyo.uiview.widget.GlideImageView
import com.angcyo.uiview.widget.RTextView
import com.angcyo.uiview.widget.viewpager.UIViewPager
import com.hn.d.valley.R
import com.hn.d.valley.base.Param
import com.hn.d.valley.base.rx.BaseSingleSubscriber
import com.hn.d.valley.main.message.redpacket.GrabedRDDetail
import com.hn.d.valley.main.message.service.RedPacketService
import com.hn.d.valley.sub.other.SingleRecyclerUIView
import java.util.*

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：动态详情, 红包领取详情
 * 创建人员：Robi
 * 创建时间：2017/08/02 17:38
 * 修改人员：Robi
 * 修改时间：2017/08/02 17:38
 * 修改备注：
 * Version: 1.0.0
 */
class RedbagListUIView(val red_id: String /*红包id*/) : SingleRecyclerUIView<GrabedRDDetail.ResultBean>() {

    override fun getTitleBar(): TitleBarPattern? {
        return null
    }

    override fun isLoadInViewPager(): Boolean {
        return true
    }

    override fun initRExBaseAdapter(): RExBaseAdapter<String, GrabedRDDetail.ResultBean, String> {
        return object : RExBaseAdapter<String, GrabedRDDetail.ResultBean, String>(mActivity) {

            override fun getItemLayoutId(viewType: Int): Int {
                return R.layout.item_redbag_list_layout
            }

            override fun onBindDataView(holder: RBaseViewHolder, posInData: Int, dataBean: GrabedRDDetail.ResultBean) {
                super.onBindDataView(holder, posInData, dataBean)
                val userAvatarView: GlideImageView = holder.v(R.id.user_avatar_view)

                val userNameView: RTextView = holder.v(R.id.user_name_view)
                val timeView: RTextView = holder.v(R.id.time_view)

                val bestView: View = holder.v(R.id.best_view)

                val moneyView: RTextView = holder.v(R.id.money_view)

                //时间
                timeView.text = TimeUtil.getTimeShowString(dataBean.created.toLong() * 1000, false)
                //用户名
                userNameView.text = dataBean.username
                //用户头像
                userAvatarView.apply {
                    reset()
                    url = dataBean.avatar
                }

                bestView.visibility = if (dataBean.best == 1) View.VISIBLE else View.INVISIBLE
                moneyView.text = String.format(Locale.CHINA, "%.2f", dataBean.money / 100f)
            }
        }
    }

    override fun inflateOverlayLayout(baseContentLayout: ContentLayout, inflater: LayoutInflater) {
        inflate(R.layout.layout_default_empty_pager)
    }

    override fun onEmptyData(isEmpty: Boolean) {
        val emptyRootLayout = mViewHolder.v<View>(R.id.default_pager_root_layout)
        val emptyTipView = mViewHolder.v<TextView>(R.id.default_pager_tip_view)
        if (emptyRootLayout != null) {
            emptyRootLayout.visibility = if (isEmpty) View.VISIBLE else View.GONE
            emptyTipView.text = emptyTipString
        }
    }

    override fun getEmptyTipString(): String {
        return "暂无记录"
    }

    override fun initRefreshLayout() {
        super.initRefreshLayout()
        mRefreshLayout.setRefreshDirection(RefreshLayout.NONE)//禁用刷新
    }

    override fun onUILoadData(page: String) {
        super.onUILoadData(page)
        add(RRetrofit.create(RedPacketService::class.java)
                .resultlist(Param.buildInfoMap("redid:$red_id", "limit:20", "lastid:$last_id"))
                .compose(Rx.transformerList(GrabedRDDetail.ResultBean::class.java, 0))
                .subscribe(object : BaseSingleSubscriber<List<GrabedRDDetail.ResultBean>>() {

                    override fun onSucceed(bean: List<GrabedRDDetail.ResultBean>) {
                        onUILoadDataEnd(bean)
                        if (bean.isNotEmpty()) {
                            last_id = bean.last().id.toString()
                        }
                        onDataLoadListener?.onLoadData(mRExBaseAdapter.rawItemCount)
                    }

                    override fun onEnd(isError: Boolean, isNoNetwork: Boolean, e: RException) {
                        super.onEnd(isError, isNoNetwork, e)
                        onUILoadDataFinish()
                        if (isError) {
                            showNonetLayout { loadData() }
                        }
                    }
                }))
    }

    override fun onShowInPager(viewPager: UIViewPager) {
        showInPagerCount++
        if (showInPagerCount == 1L) {
            loadData()
        }
    }

    var onDataLoadListener: OnDataLoadListener? = null

    override fun onHideInPager(viewPager: UIViewPager) {
        //super.onHideInPager(viewPager);
    }

    interface OnDataLoadListener {
        fun onLoadData(dataCount: Int)
    }
}
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
import com.hn.d.valley.bean.RewardModel
import com.hn.d.valley.service.RewardService
import com.hn.d.valley.sub.other.SingleRecyclerUIView
import com.hn.d.valley.widget.HnGenderView

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：动态详情, 打赏列表
 * 创建人员：Robi
 * 创建时间：2017/08/02 17:38
 * 修改人员：Robi
 * 修改时间：2017/08/02 17:38
 * 修改备注：
 * Version: 1.0.0
 */
class RewardListUIView(val item_id: String /*动态id*/) : SingleRecyclerUIView<RewardModel.DataListBean>() {

    override fun getTitleBar(): TitleBarPattern? {
        return null
    }

    override fun isLoadInViewPager(): Boolean {
        return true
    }

    override fun initRExBaseAdapter(): RExBaseAdapter<String, RewardModel.DataListBean, String> {
        return object : RExBaseAdapter<String, RewardModel.DataListBean, String>(mActivity) {

            override fun getItemLayoutId(viewType: Int): Int {
                return R.layout.item_reward_list_layout
            }

            override fun onBindDataView(holder: RBaseViewHolder, posInData: Int, dataBean: RewardModel.DataListBean) {
                super.onBindDataView(holder, posInData, dataBean)
                val userAvatarView: GlideImageView = holder.v(R.id.user_avatar_view)
                val giftUrlView: GlideImageView = holder.v(R.id.gift_url_view)

                val userNameView: RTextView = holder.v(R.id.user_name_view)
                val giftNameView: RTextView = holder.v(R.id.gift_name_view)
                val timeView: RTextView = holder.v(R.id.time_view)

                val genderView: HnGenderView = holder.v(R.id.gender_view)

                //时间
                timeView.text = TimeUtil.getTimeShowString(dataBean.created.toLong() * 1000, false)
                //性别
                genderView.setGender(dataBean.user_info.sex)
                //用户名
                userNameView.text = dataBean.user_info.username
                //用户头像
                userAvatarView.apply {
                    reset()
                    url = dataBean.user_info.avatar
                }

                giftUrlView.setImageResource(R.drawable.hongbao_xiao_60)

                // type 打赏类型 1 - 礼物 2-红包
                if ("1".equals(dataBean.type, true)) {
                    giftUrlView.apply {
                        reset()
                        url = dataBean.gift_info.thumb
                    }
                    giftNameView.text = dataBean.gift_info.name
                } else {
                    giftUrlView.apply {
                        reset()
                        setImageResource(R.drawable.hongbao_xiao_60)
                    }
                    giftNameView.text = "打赏¥${dataBean.package_info.money.toFloat() / 100}红包"
                }
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
        return "暂无打赏"
    }

    override fun initRefreshLayout() {
        super.initRefreshLayout()
        mRefreshLayout.setRefreshDirection(RefreshLayout.NONE)//禁用刷新
    }

    override fun onUILoadData(page: String) {
        super.onUILoadData(page)
        add(RRetrofit
                .create(RewardService::class.java)
                .list(Param.buildMap("item_type:discuss", "item_id:$item_id", "page:$page"))
                .compose(Rx.transformer(RewardModel::class.java))
                .subscribe(object : BaseSingleSubscriber<RewardModel>() {

                    override fun onSucceed(bean: RewardModel?) {
                        if (bean == null) {
                            onUILoadDataEnd(null)
                        } else {
                            onUILoadDataEnd(bean.data_list)
                        }
                    }

                    override fun onEnd(isError: Boolean, isNoNetwork: Boolean, e: RException) {
                        super.onEnd(isError, isNoNetwork, e)
                        onUILoadDataFinish()
                        if (isError) {
                            showNonetLayout { loadData() }
                        }
                    }
                })
        )
    }

    override fun onShowInPager(viewPager: UIViewPager) {
        showInPagerCount++
        if (showInPagerCount == 1L) {
            loadData()
        }
    }

    override fun onHideInPager(viewPager: UIViewPager) {
        //super.onHideInPager(viewPager);
    }

    interface OnRewardListener {

    }
}
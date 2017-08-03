package com.hn.d.valley.main.message.gift

import android.content.Context
import android.graphics.Color
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.angcyo.uiview.base.UIBaseRxView
import com.angcyo.uiview.container.ContentLayout
import com.angcyo.uiview.model.TitleBarPattern
import com.angcyo.uiview.net.RRetrofit
import com.angcyo.uiview.net.Rx
import com.angcyo.uiview.recycler.RBaseViewHolder
import com.angcyo.uiview.recycler.RRecyclerView
import com.angcyo.uiview.recycler.RecyclerViewPagerIndicator
import com.angcyo.uiview.recycler.adapter.RModelAdapter
import com.angcyo.uiview.utils.ScreenUtil
import com.angcyo.uiview.utils.T_
import com.angcyo.uiview.widget.viewpager.RViewPager
import com.hn.d.valley.R
import com.hn.d.valley.base.BaseContentUIView
import com.hn.d.valley.base.Param
import com.hn.d.valley.base.rx.BaseSingleSubscriber
import com.hn.d.valley.bean.GiftBean
import com.hn.d.valley.bean.ListModel
import com.hn.d.valley.cache.NimUserInfoCache
import com.hn.d.valley.cache.UserCache
import com.hn.d.valley.main.friend.AbsContactItem
import com.hn.d.valley.main.message.groupchat.BaseContactSelectAdapter
import com.hn.d.valley.main.message.groupchat.GroupMemberItem
import com.hn.d.valley.main.message.groupchat.GroupMemberSelectUIVIew
import com.hn.d.valley.main.message.groupchat.RequestCallback
import com.hn.d.valley.main.message.session.Container
import com.hn.d.valley.main.message.setThumbUrl
import com.hn.d.valley.start.SpaceItemDecoration
import com.hn.d.valley.sub.other.KLGCoinUIVIew
import com.hn.d.valley.widget.HnGlideImageView
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import rx.functions.Action0
import rx.functions.Action3

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：cjh
 * 创建时间：2017/7/5
 * 修改人员：cjh
 * 修改时间：2017/7/5
 * 修改备注：
 * Version: 1.0.0
 */
class GiftListUIView2 : BaseContentUIView {

    var recycler_view: RRecyclerView? = null
    var indicator: RecyclerViewPagerIndicator? = null
    var tv_selected: TextView? = null
    var user_ico_view: HnGlideImageView? = null
    var tv_interest_desc: TextView? = null
    var btn_send: TextView? = null
    var iv_switch: ImageView? = null
    var adapter : GiftListAdapter? = null

    var account: String
    var sessionType: SessionTypeEnum
    var container: Container? = null

    constructor(account: String, sessionType: SessionTypeEnum) : super() {
        this.account = account
        this.sessionType = sessionType
    }

    constructor(container: Container) : this(container.account, container.sessionType) {
        this.container = container
    }

    constructor(uid: String, container: Container) : this(container) {
        this.container = container
        account = uid

    }

    override fun getTitleBar(): TitleBarPattern {
        val titleBarPattern = super.getTitleBar()
                .setShowBackImageView(true)
                .setTitleString(getString(R.string.text_send_gift2))
                .setFloating(true)
                .setTitleBarBGColor(Color.TRANSPARENT)
        return titleBarPattern
    }

    override fun inflateContentLayout(baseContentLayout: ContentLayout?, inflater: LayoutInflater?) {
        inflate(R.layout.view_gift_list2)
    }

    override fun onViewLoad() {
        super.onViewLoad()
    }

    override fun initOnShowContentLayout() {
        super.initOnShowContentLayout()
        recycler_view = mViewHolder.v(R.id.recycler_view)
        indicator = mViewHolder.v(R.id.recycler_view_pager_indicator)
        tv_selected = mViewHolder.v(R.id.tv_selected)
        user_ico_view = mViewHolder.v(R.id.user_ico_view)
        tv_interest_desc = mViewHolder.v(R.id.tv_interest_desc)
        btn_send = mViewHolder.v(R.id.btn_send)
        iv_switch = mViewHolder.imgV(R.id.iv_switch)

        initAdapter()

        if (sessionType == SessionTypeEnum.Team) {
            iv_switch?.visibility = View.VISIBLE
            iv_switch?.setOnClickListener {
                GroupMemberSelectUIVIew.start(mILayout, BaseContactSelectAdapter.Options(RModelAdapter.MODEL_SINGLE), null, container?.proxy!!.gid
                        , Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback<Any>> { _, items, callback ->
                    if (items.isEmpty()) {
                        T_.show(getString(R.string.not_empty_tip))
                        return@Action3
                    }
                    callback.onSuccess("")
                    val item = items[0] as GroupMemberItem
                    user_ico_view!!.setImageThumbUrl(item.memberBean.userAvatar)
                    tv_interest_desc!!.text = String.format("送给 %s", item.memberBean.defaultNick)
                    account = item.memberBean.userId
                })

            }
        }

        btn_send!!.setOnClickListener {
            startIView(KLGCoinUIVIew())
        }

        val userInfoCache = NimUserInfoCache.getInstance()
        val userInfo = userInfoCache.getUserInfo(account)
        if (userInfo != null) {
            val avatar = userInfo.getAvatar()
            user_ico_view!!.setImageThumbUrl(avatar)
            tv_interest_desc!!.text = String.format("送给 %s", userInfo.name)
        }

        tv_selected!!.text = String.format("龙币:%s", UserCache.instance().getLoginBean().getCoins())
        loadData()

    }

    private fun initAdapter() {
        val layoutParams = ViewGroup.LayoutParams(-2, -2)
        layoutParams.width = ScreenUtil.screenWidth
        val itemHeight = (layoutParams.width - ScreenUtil.dip2px(48f)) / 3

        val itemDecoration = SpaceItemDecoration(10)
        recycler_view?.addItemDecoration(itemDecoration)
        //禁止RecyclerView 上下拖动阴影
        recycler_view?.setOverScrollMode(View.OVER_SCROLL_NEVER)
        recycler_view?.setLayoutManager(GridLayoutManager(container?.activity, 3))

        adapter = GiftListAdapter(container?.activity, itemHeight)
        recycler_view?.adapter = adapter

    }

    private fun loadData() {
        RRetrofit.create(GiftService::class.java)
                .giftList(Param.buildMap())
                .compose(Rx.transformer(GiftList::class.java))
                .subscribe(object : BaseSingleSubscriber<GiftList>() {
                    override fun onSucceed(bean: GiftList?) {
                        super.onSucceed(bean)
                        adapter?.resetData(bean?.data_list)
                        adapter?.notifyDataSetChanged()
                    }

                    override fun onError(code: Int, msg: String?) {
                        super.onError(code, msg)
                    }
                })
    }

    class GiftList : ListModel<GiftBean>()


    inner class GiftListAdapter(context: Context?, itemHeight: Int) : RModelAdapter<GiftBean>(context) {

        var itemHeight: Int

        init {
            setModel(RModelAdapter.MODEL_SINGLE)
            this.itemHeight = itemHeight
        }

        override fun getItemLayoutId(viewType: Int): Int {
            return R.layout.item_git_list_layout
        }

        override fun onBindCommonView(holder: RBaseViewHolder?, position: Int, bean: GiftBean?) {

            val imageView = holder!!.imgV(R.id.image_view) as HnGlideImageView
            val username = holder.tv(R.id.tv_username)
            val tv_is_vip = holder.tv(R.id.tv_is_vip)
            val tv_klg_coin = holder.tv(R.id.tv_klg_coin)

            if (itemHeight != 0) {
                val layoutParams = holder!!.itemView.layoutParams
                layoutParams.height = itemHeight
                holder.itemView.layoutParams = layoutParams
            }

            imageView.setThumbUrl(bean!!.thumb)
//            username.setTextColor(R.color.main_text_color_dark)
            username.text = bean.name
            if (bean.coins.toInt() == 0) {
                tv_klg_coin.text = "免费"
            } else {
                tv_klg_coin.text = String.format("%s 龙币", bean.coins)
            }
            tv_is_vip.visibility = if (bean.is_vip.equals("0")) {
                View.GONE
            } else {
                View.VISIBLE
            }

        }

        override fun onBindModelView(model: Int, isSelector: Boolean, holder: RBaseViewHolder?, position: Int, bean: GiftBean?) {
            super.onBindModelView(model, isSelector, holder, position, bean)
            holder!!.itemView.setOnClickListener {
                sendGift(bean!!)
                setSelectorPosition(position)
                notifyDataSetChanged()
            }
            if (isPositionSelector(position)) {
                holder.itemView.setBackgroundResource(R.drawable.base_pink_rect_shape)
            } else {
                holder.itemView.background = null
            }
        }

        private fun sendGift(gift: GiftBean) {
            startIView(SendGiftUIDialog(gift, Action0 {
                if (sessionType == SessionTypeEnum.P2P) {

                    sendGift(account, "", gift.gift_id)

                } else if (sessionType == SessionTypeEnum.Team) {
                    // 送给群里某群友
                    sendGift(account, container?.proxy!!.gid, gift.gift_id)
                }

//                val attachment = GiftReceiveAttachment(gift,account)
//                val message = MessageBuilder.createCustomMessage(account, sessionType,attachment.giftReceiveMsg.msg, attachment)
//                if (container == null) {
//                    msgService().sendMessage(message, false)
//                } else {
//                    container?.proxy?.sendMessage(message)
//                }
                finishIView()
            }))
        }

    }

    companion object {
        fun sendGift(account: String, gid: String, giftId: String) {
            RRetrofit.create(GiftService::class.java)
                    .giving(Param.buildMap("to_uid:" + account, "gift_id:" + giftId, "gid:" + gid))
                    .compose(Rx.transformer(String::class.java))
                    .subscribe(object : BaseSingleSubscriber<String>() {
                        override fun onSucceed(bean: String) {
                            super.onSucceed(bean)
                            T_.show(bean)
                        }

                        override fun onStart() {
                            super.onStart()
                        }

                        override fun onError(code: Int, msg: String) {
                            super.onError(code, msg)
                        }
                    })
        }
    }


}
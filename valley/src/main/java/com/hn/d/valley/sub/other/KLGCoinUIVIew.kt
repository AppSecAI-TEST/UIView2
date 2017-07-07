package com.hn.d.valley.sub.other

import android.view.View
import com.angcyo.uiview.model.TitleBarPattern
import com.angcyo.uiview.recycler.RBaseViewHolder
import com.angcyo.uiview.utils.T_
import com.angcyo.uiview.widget.ItemInfoLayout
import com.hn.d.valley.R
import com.hn.d.valley.cache.UserCache
import com.hn.d.valley.main.me.setting.MyQrCodeUIView
import com.hn.d.valley.main.message.redpacket.ChoosePayWayUIDialog
import com.hn.d.valley.main.message.redpacket.GrabPacketHelper
import com.hn.d.valley.main.message.redpacket.PayUIDialog
import com.hn.d.valley.main.message.redpacket.ThirdPayUIDialog
import com.hn.d.valley.main.wallet.KlGCoinBillUIView
import com.hn.d.valley.main.wallet.WalletAccountUpdateEvent
import com.hn.d.valley.main.wallet.WalletHelper
import com.hn.d.valley.utils.RBus
import rx.functions.Action1

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：cjh
 * 创建时间：2017/7/3
 * 修改人员：cjh
 * 修改时间：2017/7/3
 * 修改备注：
 * Version: 1.0.0
 */
class KLGCoinUIVIew : ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo>() {

    var balanceView: ItemInfoLayout? = null

    override fun getTitleBar(): TitleBarPattern {
        return super.getTitleBar().setTitleString("购买龙币")
    }


    override fun createItems(items: MutableList<ViewItemInfo>?) {
        val line = mActivity.resources.getDimensionPixelSize(R.dimen.base_line)

        //龙币余额
        val add = items!!.add(ItemRecyclerUIView.ViewItemInfo.build(object : ItemOffsetCallback(line) {
            override fun onBindView(holder: RBaseViewHolder, posInData: Int, dataBean: ItemRecyclerUIView.ViewItemInfo) {
                val itemInfoLayout = holder.v<ItemInfoLayout>(R.id.item_info_layout)
                itemInfoLayout.setItemDarkText(UserCache.instance().loginBean.coins)
                balanceView = itemInfoLayout
                itemInfoLayout.setItemText("龙币余额")
                itemInfoLayout.setDarkDrawableRes(R.drawable.longbi)
//                itemInfoLayout.darkTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }))

        //龙币记录
        items!!.add(ItemRecyclerUIView.ViewItemInfo.build(object : ItemOffsetCallback(line) {
            override fun onBindView(holder: RBaseViewHolder, posInData: Int, dataBean: ItemRecyclerUIView.ViewItemInfo) {
                val itemInfoLayout = holder.v<ItemInfoLayout>(R.id.item_info_layout)
                itemInfoLayout.setItemText("龙币记录")
                itemInfoLayout.setOnClickListener {
                    startIView(KlGCoinBillUIView())
                }
            }
        }))
        //选择充值金额
        items!!.add(ItemRecyclerUIView.ViewItemInfo.build(object : ItemOffsetCallback(line) {
            override fun onBindView(holder: RBaseViewHolder, posInData: Int, dataBean: ItemRecyclerUIView.ViewItemInfo) {
                holder.tv(R.id.text_view).text = "选择充值金额"
            }
        }))
        //60
        items!!.add(ItemRecyclerUIView.ViewItemInfo.build(object : ItemOffsetCallback(line) {
            override fun onBindView(holder: RBaseViewHolder, posInData: Int, dataBean: ItemRecyclerUIView.ViewItemInfo) {
                val itemInfoLayout = holder.v<ItemInfoLayout>(R.id.item_info_layout)
                bindInfo(itemInfoLayout, "60 龙币", "￥ 6")
                bindRechargeAction(itemInfoLayout, 60)
            }
        }))

        //120
        items!!.add(ItemRecyclerUIView.ViewItemInfo.build(object : ItemOffsetCallback(line) {
            override fun onBindView(holder: RBaseViewHolder, posInData: Int, dataBean: ItemRecyclerUIView.ViewItemInfo) {
                val itemInfoLayout = holder.v<ItemInfoLayout>(R.id.item_info_layout)
                bindInfo(itemInfoLayout, "120 龙币", "￥ 12")
                bindRechargeAction(itemInfoLayout, 120)
            }
        }))

        //300
        items.add(ItemRecyclerUIView.ViewItemInfo.build(object : ItemOffsetCallback(line) {
            override fun onBindView(holder: RBaseViewHolder, posInData: Int, dataBean: ItemRecyclerUIView.ViewItemInfo) {
                val itemInfoLayout = holder.v<ItemInfoLayout>(R.id.item_info_layout)
                bindInfo(itemInfoLayout, "300 龙币", "￥ 30")
                bindRechargeAction(itemInfoLayout, 300)
            }
        }))

        //600
        items!!.add(ItemRecyclerUIView.ViewItemInfo.build(object : ItemOffsetCallback(line) {
            override fun onBindView(holder: RBaseViewHolder, posInData: Int, dataBean: ItemRecyclerUIView.ViewItemInfo) {
                val itemInfoLayout = holder.v<ItemInfoLayout>(R.id.item_info_layout)
                bindInfo(itemInfoLayout, "600 龙币", "￥ 60")
                bindRechargeAction(itemInfoLayout, 600)
            }
        }))

        //1080
        items!!.add(ItemRecyclerUIView.ViewItemInfo.build(object : ItemOffsetCallback(line) {
            override fun onBindView(holder: RBaseViewHolder, posInData: Int, dataBean: ItemRecyclerUIView.ViewItemInfo) {
                val itemInfoLayout = holder.v<ItemInfoLayout>(R.id.item_info_layout)
                bindInfo(itemInfoLayout, "1080 龙币", "￥ 108")
                bindRechargeAction(itemInfoLayout, 1080)
            }
        }))

        //5180
        items!!.add(ItemRecyclerUIView.ViewItemInfo.build(object : ItemOffsetCallback(line) {
            override fun onBindView(holder: RBaseViewHolder, posInData: Int, dataBean: ItemRecyclerUIView.ViewItemInfo) {
                val itemInfoLayout = holder.v<ItemInfoLayout>(R.id.item_info_layout)
                bindInfo(itemInfoLayout, "5180 龙币", "￥ 518")
                bindRechargeAction(itemInfoLayout, 5180)
            }
        }))

    }

    private fun bindRechargeAction(itemInfoLayout: ItemInfoLayout, coin: Int) {
        itemInfoLayout.setOnClickListener {
            val params = PayUIDialog.Params()
//            params.balance = WalletHelper.getInstance().walletAccount.money

            params.setMoney(coin * 10f)
                    .setCoin(coin)
                    .setWay(1)
                    .setType(2)

            //检查余额是否足够
            GrabPacketHelper.balanceCheck { money ->
                //参数设置余额
                params.balance = money!!
                val action = object : Action1<Any> {
                    override fun call(o: Any) {
                        T_.show("充值成功!")
                        // 重新获取个人信息 刷新列表 || 增加余额 刷新列表数据
                        UserCache.instance().loginBean.coins = (UserCache.instance().loginBean.coins.toInt() + coin / 10).toString()
                        balanceView!!.setItemDarkText(UserCache.instance().loginBean.coins)
                    }
                }
                if (money >= params.money) {
                    mParentILayout.startIView(PayUIDialog(action, params))

                } else {
                    mParentILayout.startIView(ChoosePayWayUIDialog(action, params))
                }
            }
        }
    }

    private fun bindInfo(itemInfoLayout: ItemInfoLayout, type: String, value: String) {
        itemInfoLayout.setItemText(type)
        itemInfoLayout.darkTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        itemInfoLayout.darkTextView.setTextColor(getColor(R.color.yellow_ffac2d))
        itemInfoLayout.setItemDarkText(value)
        itemInfoLayout.darkTextView.setBackgroundResource(R.drawable.base_round_longbi_border_shape)
    }

    override fun getItemLayoutId(viewType: Int): Int {

        if (viewType == 2) {
            return R.layout.item_single_main_text_view
        } else {
            return R.layout.item_info_layout
        }


    }


}
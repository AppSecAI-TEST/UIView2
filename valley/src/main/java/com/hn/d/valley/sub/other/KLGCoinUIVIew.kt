package com.hn.d.valley.sub.other

import android.view.View
import com.angcyo.uiview.model.TitleBarPattern
import com.angcyo.uiview.recycler.RBaseViewHolder
import com.angcyo.uiview.widget.ItemInfoLayout
import com.hn.d.valley.R
import com.hn.d.valley.main.me.setting.MyQrCodeUIView

/**
 * Created by cjh on 2017/7/3.
 */
class KLGCoinUIVIew : ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo>() {

    override fun getTitleBar(): TitleBarPattern {
        return super.getTitleBar().setTitleString("购买龙币")
    }


    override fun createItems(items: MutableList<ViewItemInfo>?) {
        val line = mActivity.resources.getDimensionPixelSize(R.dimen.base_line)

        //龙币余额
        val add = items!!.add(ItemRecyclerUIView.ViewItemInfo.build(object : ItemOffsetCallback(line) {
            override fun onBindView(holder: RBaseViewHolder, posInData: Int, dataBean: ItemRecyclerUIView.ViewItemInfo) {
                val itemInfoLayout = holder.v<ItemInfoLayout>(R.id.item_info_layout)
                itemInfoLayout.setItemDarkText("20")
                itemInfoLayout.setItemText("龙币余额")
                itemInfoLayout.setDarkDrawableRes(R.drawable.longbi)
            }
        }))

        //龙币记录
        items!!.add(ItemRecyclerUIView.ViewItemInfo.build(object : ItemOffsetCallback(line) {
            override fun onBindView(holder: RBaseViewHolder, posInData: Int, dataBean: ItemRecyclerUIView.ViewItemInfo) {
                val itemInfoLayout = holder.v<ItemInfoLayout>(R.id.item_info_layout)
                itemInfoLayout.setItemText("龙币记录")
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
                itemInfoLayout.setItemText("60 龙币")
                itemInfoLayout.darkTextView.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                itemInfoLayout.darkTextView.setTextColor(getColor(R.color.yellow_ffac2d))
                itemInfoLayout.setItemDarkText("￥ 6")
                itemInfoLayout.darkTextView.setBackgroundResource(R.drawable.base_round_longbi_border_shape)
            }
        }))

        //120
        items!!.add(ItemRecyclerUIView.ViewItemInfo.build(object : ItemOffsetCallback(line) {
            override fun onBindView(holder: RBaseViewHolder, posInData: Int, dataBean: ItemRecyclerUIView.ViewItemInfo) {
                val itemInfoLayout = holder.v<ItemInfoLayout>(R.id.item_info_layout)
                itemInfoLayout.darkTextView.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                itemInfoLayout.setItemText("120 龙币")
            }
        }))

        //300
        items!!.add(ItemRecyclerUIView.ViewItemInfo.build(object : ItemOffsetCallback(line) {
            override fun onBindView(holder: RBaseViewHolder, posInData: Int, dataBean: ItemRecyclerUIView.ViewItemInfo) {
                val itemInfoLayout = holder.v<ItemInfoLayout>(R.id.item_info_layout)
                itemInfoLayout.darkTextView.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                itemInfoLayout.setItemText("300 龙币")
            }
        }))

        //600
        items!!.add(ItemRecyclerUIView.ViewItemInfo.build(object : ItemOffsetCallback(line) {
            override fun onBindView(holder: RBaseViewHolder, posInData: Int, dataBean: ItemRecyclerUIView.ViewItemInfo) {
                val itemInfoLayout = holder.v<ItemInfoLayout>(R.id.item_info_layout)
                itemInfoLayout.darkTextView.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                itemInfoLayout.setItemText("600 龙币")
            }
        }))

        //1080
        items!!.add(ItemRecyclerUIView.ViewItemInfo.build(object : ItemOffsetCallback(line) {
            override fun onBindView(holder: RBaseViewHolder, posInData: Int, dataBean: ItemRecyclerUIView.ViewItemInfo) {
                val itemInfoLayout = holder.v<ItemInfoLayout>(R.id.item_info_layout)
                itemInfoLayout.darkTextView.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                itemInfoLayout.setItemText("1080 龙币")
            }
        }))

        //5180
        items!!.add(ItemRecyclerUIView.ViewItemInfo.build(object : ItemOffsetCallback(line) {
            override fun onBindView(holder: RBaseViewHolder, posInData: Int, dataBean: ItemRecyclerUIView.ViewItemInfo) {
                val itemInfoLayout = holder.v<ItemInfoLayout>(R.id.item_info_layout)
                itemInfoLayout.darkTextView.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                itemInfoLayout.setItemText("5180 龙币")
            }
        }))

    }

    override fun getItemLayoutId(viewType: Int): Int {

        if (viewType == 2) {
            return R.layout.item_single_main_text_view
        } else {
            return R.layout.item_info_layout
        }


    }


}
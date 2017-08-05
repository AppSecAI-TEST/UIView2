package com.hn.d.valley.main.seek

import android.view.View
import android.widget.TextView
import com.angcyo.uiview.base.Item
import com.angcyo.uiview.base.SingleItem
import com.angcyo.uiview.model.TitleBarPattern
import com.angcyo.uiview.recycler.RBaseViewHolder
import com.angcyo.uiview.widget.RCheckGroup
import com.angcyo.uiview.widget.RRangeBar
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
class SeekFilterUIView(val defaultFilterBean: FilterBean, val onFilter: (FilterBean) -> Unit) : BaseItemUIView() {

    private val filterBean = FilterBean(defaultFilterBean.distance,
            defaultFilterBean.age_start,
            defaultFilterBean.age_end,
            defaultFilterBean.sex,
            defaultFilterBean.c)

    override fun getTitleBar(): TitleBarPattern {
        return super.getTitleBar()
                .addRightItem(TitleBarPattern.TitleBarItem("确定") {
                    finishIView()
                    onFilter.invoke(filterBean)
                })
    }

    override fun getTitleString(): String {
        return "筛选"
    }

    override fun getItemLayoutId(position: Int): Int {
        return R.layout.view_seek_filter_layout
    }


    override fun createItems(items: MutableList<SingleItem>?) {
        items?.add(object : SingleItem() {
            override fun onBindView(holder: RBaseViewHolder, posInData: Int, dataBean: Item?) {
                //性别
                val sexGroup: RCheckGroup = holder.v(R.id.sex_group)
                sexGroup.checkId = when (defaultFilterBean.sex) {
                    "1" -> R.id.male
                    "2" -> R.id.female
                    else -> R.id.all
                }
                sexGroup.setOnCheckChangedListener(object : RCheckGroup.OnCheckChangedListener {
                    override fun onReChecked(view: View?) {

                    }

                    override fun onChecked(fromm: View?, to: View?) {
                        filterBean.sex = when (to!!.id) {
                            R.id.male -> "1"
                            R.id.female -> "2"
                            else -> "0"
                        }
                    }
                })

                //距离
                val kmGroup: RCheckGroup = holder.v(R.id.km_group)
                kmGroup.checkId = when (defaultFilterBean.distance) {
                    "10" -> R.id.km_10
                    "5" -> R.id.km_5
                    "3" -> R.id.km_3
                    else -> R.id.km_1
                }
                kmGroup.setOnCheckChangedListener(object : RCheckGroup.OnCheckChangedListener {
                    override fun onReChecked(view: View?) {

                    }

                    override fun onChecked(fromm: View?, to: View?) {
                        filterBean.distance = when (to!!.id) {
                            R.id.km_10 -> "10"
                            R.id.km_5 -> "5"
                            R.id.km_3 -> "3"
                            else -> "1"
                        }
                    }
                })

                //年龄
                val rangeBar: RRangeBar = holder.v(R.id.range_bar)

                rangeBar.currentMinValue = defaultFilterBean.age_start!!.toInt() * 2
                rangeBar.currentMaxValue = defaultFilterBean.age_end!!.toInt() * 2

                rangeBar.rangeListener = object : RRangeBar.OnRangeListener {
                    override fun onRangeChange(minValue: Int, maxValue: Int) {
                        filterBean.age_start = (minValue / 2).toString()
                        filterBean.age_end = (maxValue / 2).toString()
                    }

                    override fun getProgressText(progress: Int): String {
                        return when (progress) {
                            100 -> "50+"
                            else -> "${(50 * (progress / 100f)).toInt()}"
                        }
                    }
                }

                //星座
                val constellationView: TextView = holder.v(R.id.constellation_view)
                holder.click(R.id.constellation_layout) {
                    //选择星座
                    startIView(ConstellationSelectorUIView(filterBean.c!!.toInt()) {
                        constellationView.text = getConstellationString(it)
                        filterBean.c = it
                    })
                }
                constellationView.text = getConstellationString(defaultFilterBean.c!!)
            }
        })
    }

    companion object {
        fun getConstellationString(constellation: String): String {
            return when (constellation) {
                "1" -> "水瓶座"
                "2" -> "双鱼座"
                "3" -> "白羊座"
                "4" -> "金牛座"
                "5" -> "双子座"
                "6" -> "巨蟹座"
                "7" -> "狮子座"
                "8" -> "处女座"
                "9" -> "天秤座"
                "10" -> "天蝎座"
                "11" -> "射手座"
                "12" -> "摩羯座"
                else -> "不限"
            }
        }
    }
}
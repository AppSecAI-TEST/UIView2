package com.hn.d.valley.main.message.gift

import android.support.v4.view.ViewPager
import com.angcyo.uiview.base.UISlidingTabView
import com.angcyo.uiview.github.tablayout.SlidingTabLayout
import com.angcyo.uiview.github.tablayout.listener.OnTabSelectListener
import com.angcyo.uiview.model.TitleBarPattern
import com.angcyo.uiview.widget.viewpager.UIViewPager
import com.hn.d.valley.R

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：cjh
 * 创建时间：2017/8/2
 * 修改人员：cjh
 * 修改时间：2017/8/2
 * 修改备注：
 * Version: 1.0.0
 */
class GiftRecordTabUIView(val uid : String) : UISlidingTabView() {

    override fun getTitleBar(): TitleBarPattern {
        return super.getTitleBar().setTitleString(getString(R.string.text_gift_record))
    }

    override fun createPages(pages: ArrayList<TabPageBean>) {
        super.createPages(pages)
        pages.add(TabPageBean(GiftRecordUIView(uid,1),"收到"))
        pages.add(TabPageBean(GiftRecordUIView(uid,2),"送出"))
    }

    override fun initViewPager(viewPager: UIViewPager) {
        super.initViewPager(viewPager)
        viewPager.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageSelected(position: Int) {
                mSlidingTab.currentTab = position
            }
        })
    }

    override fun initTabLayout(tabLayout: SlidingTabLayout) {
        super.initTabLayout(tabLayout)
        tabLayout.isTabSpaceEqual = true
        tabLayout.setIndicatorWidthEqualTitle(false)
        tabLayout.setOnTabSelectListener(object : OnTabSelectListener{
            override fun onTabReselect(position: Int) {
            }

            override fun onTabSelect(position: Int) {
                mViewPager.currentItem = position
            }

        })
    }

}
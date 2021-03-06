package com.hn.d.valley.start

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import com.angcyo.uiview.container.ContentLayout
import com.angcyo.uiview.model.TitleBarPattern
import com.angcyo.uiview.widget.JumpAdProgressBar
import com.angcyo.uiview.widget.RExTextView
import com.angcyo.uiview.widget.viewpager.ImageAdapter
import com.angcyo.uiview.widget.viewpager.RViewPager
import com.bumptech.glide.Glide
import com.hn.d.valley.R
import com.hn.d.valley.base.BaseContentUIView
import com.hn.d.valley.control.AdsControl
import com.hn.d.valley.x5.X5WebUIView
import java.io.File

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：广告页
 * 创建人员：Robi
 * 创建时间：2017/06/19 13:46
 * 修改人员：Robi
 * 修改时间：2017/06/19 13:46
 * 修改备注：
 * Version: 1.0.0
 */
class AdUIView : BaseContentUIView() {

    override fun getTitleBar(): TitleBarPattern? {
        return null
    }

    override fun getDefaultBackgroundColor(): Int {
        return Color.WHITE
    }

    override fun inflateContentLayout(baseContentLayout: ContentLayout?, inflater: LayoutInflater?) {
        inflate(R.layout.view_ad_layout)
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun canTryCaptureView(): Boolean {
        return false
    }

    override fun onViewLoad() {
        super.onViewLoad()
        val viewPager: RViewPager = mViewHolder.v(R.id.view_pager)

        val adList = AdsControl.getAdList()

        viewPager.adapter = object : ImageAdapter() {
            override fun initImageView(rootLayout: RelativeLayout?, imageView: ImageView, position: Int) {
                imageView.setImageDrawable(ColorDrawable(getColor(R.color.base_placeholder_color)))

                val filePath = adList[position].filePath
                if (!TextUtils.isEmpty(filePath) && File(filePath).exists()) {
                    imageView.setImageDrawable(BitmapDrawable(resources, filePath))
                } else {
                    Glide.with(mActivity)
                            .load(adList[position].filePath)
                            .dontAnimate()
                            .into(imageView)
                }

                if (RExTextView.isWebUrl(adList[position].value)) {
                    imageView.setOnClickListener {
                        startIView(X5WebUIView(adList[position].value))
                    }
                }
            }

            override fun getCount(): Int = adList.size
        }

        AdsControl.setIsShowAd(true)
    }

    override fun initOnShowContentLayout() {
        super.initOnShowContentLayout()
        val adProgressBar: JumpAdProgressBar = mViewHolder.v(R.id.ad_progress_bar)
        adProgressBar.start()
        adProgressBar.setOnJumpListener {
            finishIView(this@AdUIView, false)
        }
    }
}
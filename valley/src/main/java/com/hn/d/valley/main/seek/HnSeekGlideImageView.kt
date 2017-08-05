package com.hn.d.valley.main.seek

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import com.angcyo.uiview.kotlin.density
import com.angcyo.uiview.utils.ScreenUtil
import com.angcyo.uiview.widget.GlideImageView
import com.hn.d.valley.base.oss.OssHelper
import com.lzy.imagepicker.YImageControl

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/08/03 16:07
 * 修改人员：Robi
 * 修改时间：2017/08/03 16:07
 * 修改备注：
 * Version: 1.0.0
 */
class HnSeekGlideImageView(context: Context, attributeSet: AttributeSet? = null) : GlideImageView(context, attributeSet) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (!TextUtils.isEmpty(url)) {
            val thumbSize = OssHelper.getWidthHeightWithUrl(YImageControl.url(url))
            if (thumbSize[0] == 0 || thumbSize[1] == 0) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            } else {
                var widthSize = MeasureSpec.getSize(widthMeasureSpec)
//                val widthMode = MeasureSpec.getMode(widthMeasureSpec)
//                var heightSize = MeasureSpec.getSize(heightMeasureSpec)
//                val heightMode = MeasureSpec.getMode(heightMeasureSpec)

                val thumbDisplaySize = OssHelper.getThumbDisplaySize3(thumbSize[0].toFloat(), thumbSize[1].toFloat(),
                        widthSize.toFloat(), (2 * widthSize).toFloat())

                if (widthSize == ScreenUtil.screenWidth) {
                    setMeasuredDimension(widthSize, Math.max(Math.min(thumbDisplaySize[1], 2 * widthSize), 80 * density.toInt()))
                } else {
                    setMeasuredDimension(widthSize, Math.min(thumbDisplaySize[1], 2 * widthSize))
                }
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}
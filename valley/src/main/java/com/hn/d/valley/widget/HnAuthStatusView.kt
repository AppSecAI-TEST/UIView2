package com.hn.d.valley.widget

import android.content.Context
import android.util.AttributeSet
import com.hn.d.valley.R

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：认证失败,认证中, 图片View
 * 创建人员：Robi
 * 创建时间：2017/06/08 08:36
 * 修改人员：Robi
 * 修改时间：2017/06/08 08:36
 * 修改备注：
 * Version: 1.0.0
 */
class HnAuthStatusView(context: Context, attributeSet: AttributeSet? = null) : HnGlideImageView(context, attributeSet) {
    fun setAuthType(auth_type: Int) {
        when (auth_type) {
            1 -> setImageResource(R.drawable.certified)
            2 -> setImageResource(R.drawable.authentication)
            3 -> setImageResource(R.drawable.authentication_failed)
            else -> setImageResource(R.drawable.authentication)
        }
    }
}
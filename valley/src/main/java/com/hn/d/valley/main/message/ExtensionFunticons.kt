package com.hn.d.valley.main.message

import android.app.Activity
import android.widget.ImageView
import android.widget.Toast
import com.angcyo.library.glide.GlideCircleTransform
import com.bumptech.glide.Glide
import com.hn.d.valley.R
import com.hn.d.valley.ValleyApp

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/06/07 9:10
 * 修改人员：hewking
 * 修改时间：2017/06/07 9:10
 * 修改备注：
 * Version: 1.0.0
 */

    fun Activity.toast(msg : String,duration : Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this,msg,duration).show()
    }

    fun ImageView.setImageUrl(url : String) {
        Glide.with(context)
                .load(url)
                .transform(GlideCircleTransform(ValleyApp.getApp().applicationContext))
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(this)

    }
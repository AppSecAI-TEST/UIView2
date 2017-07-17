package com.hn.d.valley

import android.content.Intent
import android.os.Bundle
import com.angcyo.uiview.base.UILayoutActivity
import com.hn.d.valley.activity.HnUIMainActivity

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/07/17 10:54
 * 修改人员：Robi
 * 修改时间：2017/07/17 10:54
 * 修改备注：
 * Version: 1.0.0
 */
class JumpActivity : UILayoutActivity() {
    override fun onLoadView(intent: Intent?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val main = Intent(this, HnUIMainActivity::class.java)
        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(main)

        finishSelf()
    }
}
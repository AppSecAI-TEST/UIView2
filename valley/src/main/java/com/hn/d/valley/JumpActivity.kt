package com.hn.d.valley

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.angcyo.library.utils.L
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

    companion object {
        const val KEY_TYPE = "key_type"
        const val KEY_ID = "key_id"
    }

    override fun onLoadView(intent: Intent?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            L.e("call: onCreate -> ${intent.data}")
        } catch(e: Exception) {
        }

        try {
            val type = intent.data.getQueryParameter("type")
            var id = intent.data.getQueryParameter("id")

            if (TextUtils.isEmpty(id)) {
                id = intent.data.getQueryParameter("uid")
            }

            if ("homepage".equals(type, true) ||
                    "user".equals(type, true) /*打开用户首页*/ ||
                    "news".equals(type, true) /*打开资讯详情*/ ||
                    "dynamic".equals(type, true) /*打开动态详情*/) {

                if (!TextUtils.isEmpty(id)) {
                    toMain(type, id)
                } else {
                    L.e("call: 跳转id为空-> ")
                }
            } else if ("open".equals(type, true) ||
                    "main".equals(type, true) /*打开程序*/) {
                toMain(type, "")
            } else {
                L.e("call: 跳转不支持的命令 -> " + type)
            }
        } catch(e: Exception) {
            L.e("call: 跳转异常 -> " + e)
        }

        finishSelf()
    }

    private fun toMain(type: String, id: String) {
        val main = Intent(this, HnUIMainActivity::class.java)
        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        main.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
        main.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        main.putExtra(KEY_TYPE, type)
        main.putExtra(KEY_ID, id)
        startActivity(main)
    }
}
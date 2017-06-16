package com.hn.d.valley.main.found.sub

import android.content.Intent
import com.angcyo.uiview.base.UIScanView
import com.angcyo.uiview.github.utilcode.utils.VibrationUtils
import com.angcyo.uiview.model.TitleBarPattern
import com.hn.d.valley.R
import com.hn.d.valley.main.me.UserDetailUIView2
import com.lzy.imagepicker.ImagePickerHelper

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：扫一扫
 * 创建人员：Robi
 * 创建时间：2017/06/16 13:44
 * 修改人员：Robi
 * 修改时间：2017/06/16 13:44
 * 修改备注：
 * Version: 1.0.0
 */
class HnScanUIView : UIScanView() {

    override fun getTitleBar(): TitleBarPattern {
        return super.getTitleBar().setTitleString(getString(R.string.scan_title))
    }

    override fun onHandleDecode(result: String?) {
        if (result.isNullOrEmpty()) {
            return
        }
        VibrationUtils.vibrate(mActivity, 200)//震动
        if (result!!.contains("uid=")) {
            //个人名片
            try {
                val split = result.split("uid=".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                if (split.size >= 2) {
                    replaceIView(UserDetailUIView2(split[1]))
                }
            } catch (e: Exception) {

            }

        } else if (result.contains("team=")) {
            // 群名片
            try {


            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            //显示未识别的二维码结果界面
            replaceIView(ScanResultUIView(result))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        val images = ImagePickerHelper.getImages(mActivity, requestCode, resultCode, data)
        if (images.size > 0) {
            scanPicture(images[0])
        }
    }
}
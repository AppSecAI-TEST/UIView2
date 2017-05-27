package com.hn.d.valley.control

import com.angcyo.library.utils.L
import com.angcyo.uiview.github.utilcode.utils.AppUtils
import com.angcyo.uiview.github.utilcode.utils.FileUtils
import com.angcyo.uiview.net.RRetrofit
import com.angcyo.uiview.net.Rx
import com.hn.d.valley.ValleyApp
import com.hn.d.valley.base.Param
import com.hn.d.valley.base.rx.BaseSingleSubscriber
import com.hn.d.valley.bean.VersionBean
import com.hn.d.valley.service.AppService
import com.liulishuo.FDown
import com.liulishuo.FDownListener
import com.liulishuo.filedownloader.BaseDownloadTask
import java.io.File

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/05/26 17:40
 * 修改人员：Robi
 * 修改时间：2017/05/26 17:40
 * 修改备注：
 * Version: 1.0.0
 */
object VersionControl {

    //是否正在检测
    var checking = false

    lateinit var versionBean: VersionBean

    fun isChecking(isCheck: () -> Unit) {
        if (checking) {
            //正在检测的回调
            isCheck?.invoke()
        }
    }

    //检查版本
    fun checkVersion() {
        if (!checking) {
            checking = true
            RRetrofit.create<AppService>(AppService::class.java)
                    .checkVersion(Param.buildMap())
                    .compose(Rx.transformer(VersionBean::class.java))
                    .subscribe(object : BaseSingleSubscriber<VersionBean>() {
                        override fun onSucceed(bean: VersionBean) {
                            super.onSucceed(bean)
                            versionBean = bean
                            checkUpgradeInfo()
                        }

                        override fun onError(code: Int, msg: String?) {
                            super.onError(code, msg)
                            checking = false
                        }
                    })
        }
    }

    private fun checkUpgradeInfo() {
        try {
            val versionName: String? = AppUtils.getAppVersionName(ValleyApp.getApp())
            if (versionName != null) {
                val vName = versionName.toSubFloat()
                var dName = versionBean.version.toSubFloat()

                if (dName > vName) {
                    //有版本更新
                    downFile()
                } else {
                    //无更新
                    checking = false
                }
            }
        } catch (e: Exception) {
            checking = false
        }
    }

    private fun downFile() {
        val targetFile = File(MusicControl.generateApkFilePath("${versionBean.version}.apk"))

        if (targetFile.exists()) {
            checking = false
        } else {
            val path = MusicControl.generateApkFilePath("${targetFile.name}.temp")
            L.e("下载:${versionBean.download_url} 至$path")
            FDown.build(versionBean.download_url)
                    .setFullPath(path)
                    .download(object : FDownListener() {
                        override fun onCompleted(task: BaseDownloadTask?) {
                            super.onCompleted(task)
                            FileUtils.rename(File(path), targetFile.name)
                            checking = false
                        }

                        override fun onError(task: BaseDownloadTask?, e: Throwable?) {
                            super.onError(task, e)
                            checking = false
                        }
                    })
        }
    }

    fun String.toSubFloat(): Float {
        val lastIndex = this.lastIndexOf('.')
        val vName = this.removeRange(lastIndex, lastIndex + 1).toFloat()
        return vName
    }
}
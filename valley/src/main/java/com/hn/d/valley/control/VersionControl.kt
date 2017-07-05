package com.hn.d.valley.control

import com.angcyo.library.utils.L
import com.angcyo.uiview.github.utilcode.utils.AppUtils
import com.angcyo.uiview.github.utilcode.utils.FileUtils
import com.angcyo.uiview.net.RException
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
import com.liulishuo.filedownloader.SimpleTask
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

    val targetFile: File  by lazy {
        File(MusicControl.generateApkFilePath("${versionBean.version}.apk"))
    }

    fun isChecking(isCheck: (() -> Unit)?) {
        if (checking) {
            //正在检测的回调
            isCheck?.invoke()
        }
    }

    //检查版本
    fun checkVersion(onUpdate: (VersionBean) -> Unit) {
        if (!checking) {
            checking = true
            RRetrofit.create<AppService>(AppService::class.java)
                    .checkVersion(Param.buildMap())
                    .compose(Rx.transformer(VersionBean::class.java))
                    .subscribe(object : BaseSingleSubscriber<VersionBean>() {
                        override fun onSucceed(bean: VersionBean) {
                            super.onSucceed(bean)
                            versionBean = bean
                            checkUpgradeInfo(onUpdate)
                        }

                        override fun onEnd(isError: Boolean, isNoNetwork: Boolean, e: RException?) {
                            super.onEnd(isError, isNoNetwork, e)
                            checking = false
                        }
                    })
        }
    }

    private fun checkUpgradeInfo(onUpdate: (VersionBean) -> Unit) {
        try {
            val versionName: String? = AppUtils.getAppVersionName(ValleyApp.getApp())
            if (versionName != null) {
                val vName = versionName.toSubFloat()
                var dName = versionBean.version.toSubFloat()
                versionBean.forceUpdate = versionBean.limit_version.toSubFloat() >= vName

                if (dName > vName) {
                    //有版本更新
                    //downFile()
                    checking = false
                    onUpdate.invoke(versionBean)
                } else {
                    //无更新
                    checking = false
                }
            }
        } catch (e: Exception) {
            checking = false
        }
    }

    //是否已经下载
    fun isFileDowned(): Boolean {
        return targetFile.exists()
    }

    fun downFile(downListener: FDownListener) {

        if (targetFile.exists()) {
            checking = false
            val task = SimpleTask()
            task.path = targetFile.absolutePath
            downListener.onCompleted(task)
        } else {
            val path = MusicControl.generateApkFilePath("${targetFile.name}.temp")
            L.e("下载:${versionBean.download_url} 至$path")
            FDown.build(versionBean.download_url)
                    .setFullPath(path)
                    .download(object : FDownListener() {
                        override fun onCompleted(task: BaseDownloadTask?) {
                            super.onCompleted(task)
                            FileUtils.rename(File(path), targetFile.name)
                            task?.path = targetFile.absolutePath
                            checking = false
                            downListener.onCompleted(task)
                        }

                        override fun onError(task: BaseDownloadTask?, e: Throwable?) {
                            super.onError(task, e)
                            checking = false
                            downListener.onError(task, e)
                        }

                        override fun onProgress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int, progress: Float) {
                            super.onProgress(task, soFarBytes, totalBytes, progress)
                            downListener.onProgress(task, soFarBytes, totalBytes, progress)
                        }
                    })
        }
    }

    fun String.toSubFloat(): Float {
        if (this.isEmpty()) {
            return 0f
        }
        val lastIndex = this.lastIndexOf('.')
        if (lastIndex < 0) {
            return 0f
        }
        val vName = this.removeRange(lastIndex, lastIndex + 1).toFloat()
        return vName
    }
}

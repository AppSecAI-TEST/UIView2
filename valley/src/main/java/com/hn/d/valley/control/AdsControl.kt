package com.hn.d.valley.control

import com.angcyo.library.utils.L
import com.angcyo.uiview.container.ILayout
import com.angcyo.uiview.container.UIParam
import com.angcyo.uiview.net.RException
import com.angcyo.uiview.net.RRetrofit
import com.angcyo.uiview.net.Rx
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.hn.d.valley.BuildConfig
import com.hn.d.valley.ValleyApp
import com.hn.d.valley.base.Param
import com.hn.d.valley.base.rx.BaseSingleSubscriber
import com.hn.d.valley.bean.AdModel
import com.hn.d.valley.bean.realm.AdRealm
import com.hn.d.valley.realm.RRealm
import com.hn.d.valley.service.AdsService
import com.hn.d.valley.start.AdUIView
import com.orhanobut.hawk.Hawk
import io.realm.RealmResults
import java.io.File

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：广告控制器
 * 创建人员：Robi
 * 创建时间：2017/06/19 11:12
 * 修改人员：Robi
 * 修改时间：2017/06/19 11:12
 * 修改备注：
 * Version: 1.0.0
 */
object AdsControl {

    const val KEY_SHOW_AD = "key_show_ad"

    private var checking = false
    private var count = 0
    private var size = 0

    fun checkAds(ilayout: ILayout<UIParam>) {
        if (needShowAds()) {
            ilayout.startIView(AdUIView(), UIParam(false, true))
        } else {
            if (BuildConfig.SHOW_DEBUG) {
                updateAds()
            }
        }
    }

    fun setIsShowAd(isShow: Boolean) {
        Hawk.put(KEY_SHOW_AD, isShow)
    }

    /**是否需要显示广告*/
    fun needShowAds(): Boolean {
        //是否显示过广告
        val isShowAd: Boolean = Hawk.get(KEY_SHOW_AD, false)

        return !isShowAd && getAdList().size > 0
    }

    fun getAdList(): RealmResults<AdRealm> {
        val realm = RRealm.instance().realm

        realm.beginTransaction()
        val findAll = realm.where(AdRealm::class.java).findAll()
        realm.commitTransaction()

        return findAll
    }

    /**更新广告*/
    fun updateAds() {
        if (checking) {
            return
        }
        checking = true
        count = 0
        RRetrofit.create(AdsService::class.java)
                .list(Param.buildMap("ads_key:app_start_guide"))
                .compose(Rx.transformer(AdModel::class.java))
                .subscribe(object : BaseSingleSubscriber<AdModel>() {

                    override fun onSucceed(bean: AdModel?) {
                        if (bean == null || bean.data_count == 0) {
                        } else {
                            size = bean.data_count
                            downloadAd(bean.data_list)
                        }
                    }

                    override fun onEnd(isError: Boolean, isNoNetwork: Boolean, e: RException?) {
                        super.onEnd(isError, isNoNetwork, e)
                        checking = false
                    }
                })
    }

    /**下载广告资源*/
    private fun downloadAd(ads: List<AdRealm>) {
        ads.map { adRealm ->
            L.e("call: 下载广告页 -> Ads: ${adRealm.image}")
            Glide.with(ValleyApp.getApp())
                    .load(adRealm.image)
                    .downloadOnly(object : SimpleTarget<File>() {
                        override fun onResourceReady(resource: File?, glideAnimation: GlideAnimation<in File>?) {
                            L.e("call: 广告页下载完成 -> Ads: ${resource?.absolutePath}")

                            adRealm.filePath = resource?.absolutePath

                            count++
                            if (count == size) {
                                //广告加载完成
                                RRealm.exe {
                                    it.where(AdRealm::class.java).findAll().deleteAllFromRealm()
                                    it.copyToRealm(ads)
                                }
                                setIsShowAd(false)
                            }
                        }
                    })
        }
    }


}
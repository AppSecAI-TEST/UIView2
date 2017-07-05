package com.hn.d.valley.control

import com.angcyo.uiview.net.RException
import com.angcyo.uiview.net.ROnSubscribe
import com.angcyo.uiview.net.RRetrofit
import com.angcyo.uiview.net.Rx
import com.hn.d.valley.base.Param
import com.hn.d.valley.base.rx.BaseSingleSubscriber
import com.hn.d.valley.bean.TopBean
import com.hn.d.valley.service.DiscussService
import rx.Observable
import rx.Observer

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/06/14 13:31
 * 修改人员：Robi
 * 修改时间：2017/06/14 13:31
 * 修改备注：
 * Version: 1.0.0
 */
class TopControl {
    companion object {
        var topBean: TopBean? = null

        /**检查是否可以置顶*/
        fun canTop(): Observable<Boolean> {
            topBean = null
            return Observable.create(object : ROnSubscribe<Boolean>() {
                override fun next(observer: Observer<in Boolean>) {
                    RRetrofit.create(DiscussService::class.java)
                            .checkTop(Param.buildMap())
                            .compose(Rx.transformer(TopBean::class.java))
                            .subscribe(object : BaseSingleSubscriber<TopBean>() {
                                override fun onSucceed(bean: TopBean?) {
                                    topBean = bean
                                    if (bean != null && bean.left > 0) {
                                        observer.onNext(true)
                                    } else {
                                        observer.onNext(false)
                                    }
                                    observer.onCompleted()
                                }

                                override fun onEnd(isError: Boolean, isNoNetwork: Boolean, e: RException?) {
                                    super.onEnd(isError, isNoNetwork, e)
                                    if (isError) {
                                        observer.onNext(false)
                                        observer.onError(e)
                                    }
                                }
                            })
                }

                override fun isAsync(): Boolean {
                    return true
                }

            }).compose(Rx.transformer())
        }
    }
}
package com.hn.d.valley.main.message.chat

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.hn.d.valley.ValleyApp

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/06/07 13:51
 * 修改人员：hewking
 * 修改时间：2017/06/07 13:51
 * 修改备注：
 * Version: 1.0.0
 */

class MsgUIObservable(ctx : Context = ValleyApp.getApp().applicationContext) {

    val uiHander = Handler(ctx.mainLooper)

    private val observers = mutableListOf<MsgUIObserver>()

    fun registerObserver(observer : MsgUIObserver) {
        observers.add(observer)
    }

    fun unregisterObserver(observer : MsgUIObserver) {
        observers.remove(observer)
    }

    fun notifyObservers() {
        uiHander.post {
            for (observer in observers) {
                observer.onViewShow()
            }
        }
    }

    fun notifyOnViewShow() {
        uiHander.post {
            for (observer in observers) {
                observer.onViewShow()
            }
        }
    }

    fun notifyOnViewHide(){
        uiHander.post {
            for (observer in observers) {
                observer.onViewHide()
            }
        }
    }


}
package com.hn.d.valley.main.message.uinfo

import com.hn.d.valley.realm.RRealm

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/06/14 17:05
 * 修改人员：hewking
 * 修改时间：2017/06/14 17:05
 * 修改备注：
 * Version: 1.0.0
 */

class DynamicFuncManager private constructor() {

    val REDPACKET: String = "REDPACKET"
    val WALLET: String = "WALLET"

    var result: Result? = Result()

    companion object {
        fun get(): DynamicFuncManager {
            return Inner.instance
        }
    }

    private object Inner {
        val instance = DynamicFuncManager()
    }


    fun fetchRemote() {

        // 服务器获取设置

        result = fetchResult()

        saveToDB(result)

    }

    private fun fetchResult(): Result {


//        RRetrofit.create(javaClass)
//                .personal(Param.buildMap("to_uid:" + to_uid, "key:1002", "val:0"))
//                .compose(Rx.transformer(javaClass<String>()))
//                .subscribe(object : BaseSingleSubscriber<String>() {
//
//                    override fun onSucceed(bean: String) {
//                    }
//                }

                        return Result ()
    }


    fun fetchLocal(): Result {

        // 从数据库获取数据

        return Result()

    }


    fun saveToDB(result: Result?) {

//        RRealm.instance().

    }


    fun load(): Result? {

        result = fetchLocal()
        if (result == null) {
            fetchRemote()
            return null
        } else {
            result = fetchLocal()
        }
        return result

    }


    class Result() {

        //0-否 1-是
        //	显示钱包及红包功能
        var wallet: Int = 0

        //显示版本信息
        //	0-否 1-是
        var version: Int = 0

        fun isShowWallet() : Boolean{
            return wallet == 1
        }

    }


}
package com.hn.d.valley.main.message.uinfo;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.service.SettingService;

import io.realm.RealmObject;

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

public class DynamicFuncManager2 {

    public static final String REDPACKET = "REDPACKET";
    public static final String WALLET = "WALLET";

    public DynamicFuncResult dynamicFuncResult = new DynamicFuncResult();

    private DynamicFuncManager2() {
    }

    public static DynamicFuncManager2 instance() {
        return Holder.instance;
    }

    private static class Holder {
        private static DynamicFuncManager2 instance = new DynamicFuncManager2();
    }

    public void fetchRemote() {

        // 服务器获取设置

//        dynamicFuncResult = fetchResult();

        RRetrofit.create(SettingService.class)
                .setting(Param.buildMap("to_uid:" + UserCache.getUserAccount(),"development:1"))
                .compose(Rx.transformer(DynamicFuncResult.class))
                .subscribe(new BaseSingleSubscriber<DynamicFuncResult>() {
                    @Override
                    public void onSucceed(DynamicFuncResult bean) {
                        L.d(DynamicFuncManager2.class.getSimpleName(), bean.wallet + ".." + bean.isShowWallet());
                        if (bean == null) {
                            return;
                        }
                        dynamicFuncResult = bean;
                        saveToDB(bean);
                    }
                });

//        saveToDB(dynamicFuncResult);

    }

    private DynamicFuncResult fetchResult() {


        return new DynamicFuncResult();
    }


    private DynamicFuncResult fetchLocal() {
        // 从数据库获取数据
        DynamicFuncResult dynamicFuncResult = RRealm.getRealmInstance().where(DynamicFuncResult.class).findFirst();
        return dynamicFuncResult;

    }


    private void saveToDB(DynamicFuncResult dynamicFuncResult) {
        L.d(DynamicFuncManager2.class.getSimpleName(), " saveToDB" + dynamicFuncResult.wallet);

        RRealm.save(dynamicFuncResult);

    }


    public DynamicFuncResult load() {

//        if (dynamicFuncResult == null) {
        fetchRemote();
        dynamicFuncResult = fetchLocal();
        if (dynamicFuncResult == null){
            return null;
        }
        return dynamicFuncResult;
//        }
//        return dynamicFuncResult;

    }


}
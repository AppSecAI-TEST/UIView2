package com.hn.d.valley.main.message.uinfo;

import io.realm.RealmObject;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/06/14 18:53
 * 修改人员：hewking
 * 修改时间：2017/06/14 18:53
 * 修改备注：
 * Version: 1.0.0
 */
public class DynamicFuncResult extends RealmObject {

    //0-否 1-是
    //	显示钱包及红包功能
    String wallet = "0";

    public DynamicFuncResult(String wallet, String version) {
        this.wallet = wallet;
        this.version = version;
    }

    public DynamicFuncResult(){}

    //显示版本信息
    //	0-否 1-是
    String version = "0";

    public boolean isShowWallet() {
        return "1".equals(wallet);
    }

}

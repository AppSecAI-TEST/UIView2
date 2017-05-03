package com.hn.d.valley.bean.realm;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/24 10:02
 * 修改人员：Robi
 * 修改时间：2016/12/24 10:02
 * 修改备注：
 * Version: 1.0.0
 */
public class AmapBean extends RealmObject {
    //经    度
    public double longitude;
    //纬    度
    public double latitude;

    public String country;//国家
    public String province;//省
    public String city;//市
    public String district;//区
    public String address;//地址

    public String title;

    @Ignore
    public boolean result = true;

    public AmapBean() {
    }

    public AmapBean(boolean result) {
        this.result = result;
    }

    public String getString() {
        StringBuilder builder = new StringBuilder();
        builder.append("经度:");
        builder.append(longitude);
        builder.append(" ");
        builder.append("纬度:");
        builder.append(latitude);
        builder.append(" ");
        builder.append(country);
        builder.append(" ");
        builder.append(province);
        builder.append(" ");
        builder.append(city);
        builder.append(" ");
        builder.append(district);
        builder.append(" ");
        builder.append(address);
        builder.append(" ");
        return builder.toString();
    }
}

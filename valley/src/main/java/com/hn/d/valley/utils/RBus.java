package com.hn.d.valley.utils;

import com.hwangjr.rxbus.RxBus;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/24 10:11
 * 修改人员：Robi
 * 修改时间：2016/12/24 10:11
 * 修改备注：
 * Version: 1.0.0
 */
public class RBus {
    public static void register(Object object) {
        RxBus.get().register(object);
    }

    public static void unregister(Object object) {
        RxBus.get().unregister(object);
    }

    public static void post(Object event) {
        RxBus.get().post(event);
    }

    public static void post(String tag, Object event) {
        RxBus.get().post(tag, event);
    }
}

package com.hn.d.valley.utils;

import com.hwangjr.rxbus.RxBus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private static volatile RBus mDefaultInstance;

    private final Map<Class<?>, Object> mStickyEventMap;

    public RBus() {
        mStickyEventMap = new ConcurrentHashMap<>();
    }

    public static RBus getDefault() {
        if (mDefaultInstance == null) {
            synchronized (RxBus.class) {
                if (mDefaultInstance == null) {
                    mDefaultInstance = new RBus();
                }
            }
        }
        return mDefaultInstance;
    }


    /**
     * 发送一个新Sticky事件
     */
    public void postSticky(Object event) {
        synchronized (mStickyEventMap) {
            mStickyEventMap.put(event.getClass(), event);
        }
        post(event);
    }

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

package com.hn.d.valley.main.message.attachment;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/05 17:31
 * 修改人员：hewking
 * 修改时间：2017/04/05 17:31
 * 修改备注：
 * Version: 1.0.0
 */
public class BaseCustomBean <T> {

    T data;

    int type;

    public BaseCustomBean(T bean, int type) {
        this.data = bean;
        this.type = type;
    }

    public T getBean() {
        return data;
    }
}
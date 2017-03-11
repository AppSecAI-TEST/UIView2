package com.hn.d.valley.base.rx;

import rx.functions.Action0;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：订阅之前, 用来检查网络
 * 创建人员：Robi
 * 创建时间：2016/12/15 10:39
 * 修改人员：Robi
 * 修改时间：2016/12/15 10:39
 * 修改备注：
 * Version: 1.0.0
 */
public class EmptyAction implements Action0 {

    public static EmptyAction build() {
        return new EmptyAction();
    }

    @Override
    public void call() {
    }
}

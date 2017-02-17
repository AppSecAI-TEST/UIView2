package com.hn.d.valley.base.rx;

import com.angcyo.uiview.net.RSubscriber;
import com.angcyo.uiview.utils.T_;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/15 11:53
 * 修改人员：Robi
 * 修改时间：2016/12/15 11:53
 * 修改备注：
 * Version: 1.0.0
 */
public abstract class BaseSingleSubscriber<T> extends RSubscriber<T> {

    @Override
    public void onError(int code, String msg) {
        super.onError(code, msg);
        T_.show(msg);
    }
}

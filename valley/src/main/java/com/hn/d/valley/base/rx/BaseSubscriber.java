package com.hn.d.valley.base.rx;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.utils.Json;
import com.hn.d.valley.base.Bean;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/14 16:46
 * 修改人员：Robi
 * 修改时间：2016/12/14 16:46
 * 修改备注：
 * Version: 1.0.0
 */
public class BaseSubscriber<O extends Object, B extends Bean<O>> extends BaseSingleSubscriber<B> {

    @Override
    public void onNext(B b) {
        if (b.isSuccess()) {
            onSuccess(b);
        } else {
            onError(b.error.code, b.error.msg);
        }
    }

    /**
     * 请求成功
     */
    public void onSuccess(B b) {
        L.d("订阅成功->" + this.getClass().getSimpleName() + "\n" + Json.to(b) + "\n");
    }
}

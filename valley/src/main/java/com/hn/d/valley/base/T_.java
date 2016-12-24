package com.hn.d.valley.base;

import android.view.Gravity;

import com.angcyo.uiview.utils.T;
import com.hn.d.valley.ValleyApp;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/14 17:25
 * 修改人员：Robi
 * 修改时间：2016/12/14 17:25
 * 修改备注：
 * Version: 1.0.0
 */
public class T_ {

    static {
        T.T_GRAVITY = Gravity.BOTTOM;
    }

    public static void show(CharSequence text) {
        T.show(ValleyApp.getApp(), text);
    }
}

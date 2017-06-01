package com.hn.d.valley.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.angcyo.uiview.base.UILayoutActivity;
import com.angcyo.uiview.skin.SkinHelper;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.skin.SkinUtils;
import com.orhanobut.hawk.Hawk;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/12 17:55
 * 修改人员：Robi
 * 修改时间：2016/12/12 17:55
 * 修改备注：
 * Version: 1.0.0
 */
public abstract class BaseActivity extends UILayoutActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Param.changeLang(Hawk.get(Constant.LANG, 1));
        SkinHelper.setSkin(SkinUtils.getSkin(this));
        setTheme(SkinUtils.getSkinStyle());
        super.onCreate(savedInstanceState);
        //RBus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //RBus.unregister(this);
    }
}

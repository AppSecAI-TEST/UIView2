package com.hn.d.valley.base;

import android.support.annotation.NonNull;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.mvp.presenter.IBasePresenter;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/16 12:08
 * 修改人员：Robi
 * 修改时间：2016/12/16 12:08
 * 修改备注：
 * Version: 1.0.0
 */
public abstract class NoTitleBarUIView<P extends IBasePresenter> extends BaseUIView<P> {

    @Override
    protected TitleBarPattern getTitleBar() {
        return null;
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }
}

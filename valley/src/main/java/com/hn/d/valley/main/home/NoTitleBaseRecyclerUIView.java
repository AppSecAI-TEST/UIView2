package com.hn.d.valley.main.home;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.angcyo.uiview.model.TitleBarPattern;
import com.hn.d.valley.base.BaseRecyclerUIView;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/07 11:53
 * 修改人员：Robi
 * 修改时间：2017/01/07 11:53
 * 修改备注：
 * Version: 1.0.0
 */
public abstract class NoTitleBaseRecyclerUIView<T> extends BaseRecyclerUIView<String, T, String> {
    @Override
    protected TitleBarPattern getTitleBar() {
        return null;
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }

    @Override
    protected boolean isLoadInViewPager() {
        return true;
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        if (!isLoadInViewPager()) {
            loadData();
        }
    }

    //    @Override
//    public void onViewShow(Bundle bundle) {
//        super.onViewShow(bundle);
//        onShowInPager(null);
//    }
}

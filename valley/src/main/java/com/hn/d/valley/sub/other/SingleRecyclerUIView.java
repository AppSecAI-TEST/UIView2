package com.hn.d.valley.sub.other;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.angcyo.uiview.model.TitleBarPattern;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseRecyclerUIView;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/14 18:01
 * 修改人员：Robi
 * 修改时间：2017/01/14 18:01
 * 修改备注：
 * Version: 1.0.0
 */
public abstract class SingleRecyclerUIView<T> extends BaseRecyclerUIView<String, T, String> {

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setTitleString(getTitleString())
                .setFloating(false)
                .setTitleHide(false)
                .setTitleBarBGColor(mActivity.getResources().getColor(R.color.theme_color_primary));
    }

    @Override
    protected boolean hasDecoration() {
        return true;
    }

    @Override
    protected int getItemDecorationHeight() {
        return mActivity.getResources().getDimensionPixelSize(R.dimen.base_line);
    }

    @Override
    protected boolean hasScrollListener() {
        return false;
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        loadData();
    }
}

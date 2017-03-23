package com.hn.d.valley.sub;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.angcyo.uiview.model.TitleBarPattern;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/03/23 08:57
 * 修改人员：Robi
 * 修改时间：2017/03/23 08:57
 * 修改备注：
 * Version: 1.0.0
 */
public class MyStatusUIView extends BaseContentUIView {

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setShowBackImageView(true).setTitleString(mActivity, R.string.me_dynamic_state);
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {

    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.EMPTY;
    }
}

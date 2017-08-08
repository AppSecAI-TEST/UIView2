package com.hn.d.valley.base;

import android.support.annotation.CallSuper;
import android.view.View;

import com.angcyo.uiview.base.UIContentView;
import com.hn.d.valley.R;
import com.hn.d.valley.utils.RBus;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/05 11:32
 * 修改人员：Robi
 * 修改时间：2017/01/05 11:32
 * 修改备注：
 * Version: 1.0.0
 */
public abstract class BaseContentUIView extends UIContentView {
    boolean isRegister = false;

    @Override
    protected int getBaseEmptyLayoutId() {
        return R.layout.empty_layout;
    }

    @Override
    protected int getBaseNonetLayoutId() {
        return R.layout.no_network_layout;
    }

    @Override
    public void loadContentView(View rootView) {
        super.loadContentView(rootView);
    }

//    @Override
//    public void showContentLayout() {
//        //基类中, 已经不使用 butterknife 了, 为了兼容, 在这里使用.
//        removeOtherView(LayoutState.CONTENT);
//        if (mBaseContentLayout.getChildCount() == 0) {
//            inflateContentLayout(mBaseContentLayout, LayoutInflater.from(mActivity));
//            ButterKnife.bind(this, mBaseContentLayout);
//            initOnShowContentLayout();
//        }
//        onShowContentLayout();
//        changeState(mLayoutState, LayoutState.CONTENT);
//    }

    @CallSuper
    @Override
    public void onViewLoad() {
        super.onViewLoad();
        if (!isRegister) {
            RBus.register(this);
            isRegister = true;
        }
    }

    @CallSuper
    @Override
    public void onViewUnload() {
        super.onViewUnload();
        if (isRegister) {
            RBus.unregister(this);
        }
        isRegister = false;
    }
}

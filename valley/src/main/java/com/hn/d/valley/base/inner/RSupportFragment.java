package com.hn.d.valley.base.inner;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.recycler.RBaseViewHolder;

import butterknife.ButterKnife;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/28 17:04
 * 修改人员：Robi
 * 修改时间：2016/12/28 17:04
 * 修改备注：
 * Version: 1.0.0
 */
public abstract class RSupportFragment extends SupportFragment {

    protected RBaseViewHolder mViewHolder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutId = getFragmentLayoutId();
        View view;
        if (layoutId == View.NO_ID) {
            view = getFragmentRootView(container);
        } else {
            view = inflater.inflate(layoutId, container, false);
        }

        ButterKnife.bind(this, view);

        if (savedInstanceState == null) {
            onCreateView();
        }

        onInitView();

        mViewHolder = new RBaseViewHolder(view);
        return view;
    }

    @LayoutRes
    protected abstract int getFragmentLayoutId();

    protected View getFragmentRootView(@Nullable ViewGroup container) {
        return null;
    }

    protected abstract void onCreateView();

    protected void onInitView() {

    }

    @Override
    protected void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        L.i(this.getClass().getSimpleName());
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        L.i(this.getClass().getSimpleName());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        L.i(this.getClass().getSimpleName());
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        L.i(this.getClass().getSimpleName());
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        L.i(this.getClass().getSimpleName());
    }
}

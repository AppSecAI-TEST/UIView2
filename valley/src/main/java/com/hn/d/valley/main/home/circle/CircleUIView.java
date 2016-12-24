package com.hn.d.valley.main.home.circle;

import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.angcyo.uiview.widget.UIViewPager;
import com.hn.d.valley.R;
import com.hn.d.valley.base.NoTitleBarUIView;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：首页 下面的 圈子
 * 创建人员：Robi
 * 创建时间：2016/12/16 11:18
 * 修改人员：Robi
 * 修改时间：2016/12/16 11:18
 * 修改备注：
 * Version: 1.0.0
 */
public class CircleUIView extends NoTitleBarUIView {
    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_home_circle_layout);
    }

    @Override
    public void onShowInPager(UIViewPager viewPager) {
        super.onShowInPager(viewPager);
    }
}

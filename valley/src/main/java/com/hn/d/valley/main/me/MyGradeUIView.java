package com.hn.d.valley.main.me;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.model.TitleBarPattern;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：我的等级界面
 * 创建人员：Robi
 * 创建时间：2016/12/17 10:15
 * 修改人员：Robi
 * 修改时间：2016/12/17 10:15
 * 修改备注：
 * Version: 1.0.0
 */
public class MyGradeUIView extends BaseContentUIView {
    @Override
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        ImageView imageView = new ImageView(mActivity);
        imageView.setImageResource(R.drawable.default_page_3);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        baseContentLayout.addView(imageView, params);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(getString(R.string.my_level)).setShowBackImageView(true);
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
    }
}
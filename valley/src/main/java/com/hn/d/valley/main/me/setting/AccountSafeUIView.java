package com.hn.d.valley.main.me.setting;

import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseSubContentUIView;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：账户安全界面
 * 创建人员：Robi
 * 创建时间：2017/02/15 09:50
 * 修改人员：Robi
 * 修改时间：2017/02/15 09:50
 * 修改备注：
 * Version: 1.0.0
 */
public class AccountSafeUIView extends BaseSubContentUIView {
    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_account_safe);
    }

    @Override
    protected String getTitle() {
        return mActivity.getString(R.string.account_safe);
    }

}

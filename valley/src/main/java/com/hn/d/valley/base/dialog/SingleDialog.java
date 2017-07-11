package com.hn.d.valley.base.dialog;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.angcyo.uiview.base.UIIDialogImpl;
import com.hn.d.valley.R;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/13 11:30
 * 修改人员：Robi
 * 修改时间：2016/12/13 11:30
 * 修改备注：
 * Version: 1.0.0
 */
public class SingleDialog extends UIIDialogImpl {
    @Override
    protected View inflateDialogView(FrameLayout dialogRootLayout, LayoutInflater inflater) {
        return inflate(R.layout.base_view_single_dialog);
    }
}

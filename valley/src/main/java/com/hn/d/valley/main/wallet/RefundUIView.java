package com.hn.d.valley.main.wallet;

import com.angcyo.uiview.model.TitleBarPattern;
import com.hn.d.valley.R;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/03 9:28
 * 修改人员：hewking
 * 修改时间：2017/05/03 9:28
 * 修改备注：
 * Version: 1.0.0
 */
public class RefundUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_tixian));
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return 0;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {

    }
}

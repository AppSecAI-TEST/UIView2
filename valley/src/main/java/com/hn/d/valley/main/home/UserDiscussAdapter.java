package com.hn.d.valley.main.home;

import android.content.Context;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExBaseAdapter;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.control.UserDiscussItemControl;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/07 12:06
 * 修改人员：Robi
 * 修改时间：2017/01/07 12:06
 * 修改备注：
 * Version: 1.0.0
 */
public class UserDiscussAdapter extends RExBaseAdapter<String, UserDiscussListBean.DataListBean, String> {
    public UserDiscussAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_search_user_item_layout;
    }

    @Override
    protected void onBindDataView(RBaseViewHolder holder, int posInData, UserDiscussListBean.DataListBean dataBean) {
        super.onBindDataView(holder, posInData, dataBean);
        UserDiscussItemControl.initItem(holder, dataBean, null);
    }
}

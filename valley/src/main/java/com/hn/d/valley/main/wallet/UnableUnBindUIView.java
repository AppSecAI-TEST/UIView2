package com.hn.d.valley.main.wallet;

import android.widget.TextView;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.hn.d.valley.R;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/04 17:27
 * 修改人员：hewking
 * 修改时间：2017/05/04 17:27
 * 修改备注：
 * Version: 1.0.0
 */
public class UnableUnBindUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    public UnableUnBindUIView() {

    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_unbind_alipay));
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_unable_unbind_alipay;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                TextView tv = holder.v(R.id.text_view);
                tv.setText(R.string.text_unable_unbind_alipay_reason);
            }
        }));

    }
}

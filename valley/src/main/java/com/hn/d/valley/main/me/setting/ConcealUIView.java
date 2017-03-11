package com.hn.d.valley.main.me.setting;

import android.support.v7.widget.SwitchCompat;
import android.view.View;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：隐私界面
 * 创建人员：Robi
 * 创建时间：2017/02/17 14:47
 * 修改人员：Robi
 * 修改时间：2017/02/17 14:47
 * 修改备注：
 * Version: 1.0.0
 */
public class ConcealUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    @Override
    protected int getTitleResource() {
        return R.string.conceal;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_switch_view;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        final int size = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);
        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                SwitchCompat switchCompat = holder.v(R.id.switch_view);
                itemInfoLayout.setItemText("绑定手机通讯录");
            }
        }));
        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                SwitchCompat switchCompat = holder.v(R.id.switch_view);
                switchCompat.setVisibility(View.GONE);

                itemInfoLayout.setItemText("联系人黑名单");
                itemInfoLayout.setRightDrawableRes(R.drawable.ic_right);
            }
        }));
    }
}

package com.hn.d.valley.main.wallet;

import android.view.View;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/02 10:11
 * 修改人员：hewking
 * 修改时间：2017/05/02 10:11
 * 修改备注：
 * Version: 1.0.0
 */
public class PayPwdSettingUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_pay_passwd));
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_info_layout;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {

        int left = mActivity.getResources().getDimensionPixelOffset(R.dimen.base_xhdpi);
        int top = mActivity.getResources().getDimensionPixelOffset(R.dimen.base_dpi);

        items.add(ViewItemInfo.build(new ItemLineCallback(left,0) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText(mActivity.getString(R.string.text_change_paypwd));
                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(new ChangePayPwdUIview());
                    }
                });
            }
        }));

        items.add(ViewItemInfo.build(new ItemLineCallback(left,top) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText(mActivity.getString(R.string.text_find_paypwd));
                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(new SetPayPwdUIView(SetPayPwdUIView.FINDPAYPWD));
                    }
                });
            }
        }));
    }
}

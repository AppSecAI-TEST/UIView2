package com.hn.d.valley.main.wallet;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.hn.d.valley.R;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.List;
import java.util.Locale;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/02 10:24
 * 修改人员：hewking
 * 修改时间：2017/05/02 10:24
 * 修改备注：
 * Version: 1.0.0
 */
public class SetPayPwdUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_forget_paypwd));
    }

    @Override
    protected int getItemLayoutId(int viewType) {

        if (viewType == 0) {
            return R.layout.item_single_main_text_view;
        }

        if (mRExBaseAdapter.isLast(viewType)) {
            return R.layout.item_redpacket_button_view;
        }

        return R.layout.item_set_paypwd;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {

        int left = mActivity.getResources().getDimensionPixelOffset(R.dimen.base_xhdpi);
        int top = mActivity.getResources().getDimensionPixelOffset(R.dimen.base_xxxhdpi);

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                TextView text = holder.tv(R.id.text_view);
                text.setTextColor(ContextCompat.getColor(mActivity,R.color.main_text_color_dark));
                text.setText(String.format(Locale.CHINA,mActivity.getString(R.string.text_aut_phonenum),1888888888));
            }
        }));

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {

            }

        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(top) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                Button btn_next = holder.v(R.id.btn_send);
                btn_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        }));

    }

}

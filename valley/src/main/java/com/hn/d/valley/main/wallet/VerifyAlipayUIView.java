package com.hn.d.valley.main.wallet;

import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.widget.ExEditText;
import com.hn.d.valley.R;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/02 16:06
 * 修改人员：hewking
 * 修改时间：2017/05/02 16:06
 * 修改备注：
 * Version: 1.0.0
 */
public class VerifyAlipayUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_verifyalipay));
    }

    @Override
    protected int getItemLayoutId(int viewType) {

        if (viewType == 0) {
            return R.layout.item_single_main_text_view;
        }

        if(mRExBaseAdapter.isLast(viewType)) {
            return R.layout.item_redpacket_button_view;
        }

        return R.layout.item_input_view;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {

        int top = mActivity.getResources().getDimensionPixelOffset(R.dimen.base_xhdpi);

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.text_view).setText(R.string.text_input_need_bind_info);
            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(top) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.input_tip_view).setText(R.string.text_account);
                ExEditText editText = holder.v(R.id.edit_text_view);
                editText.setHint(R.string.text_input_full_account);
                editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                editText.setMaxLength(18);
            }
        }));

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.input_tip_view).setText(R.string.text_name);
                ExEditText editText = holder.v(R.id.edit_text_view);
                editText.setHint(R.string.text_input_full_name);
                editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                editText.setMaxLength(18);
            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(3 * top) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                Button button = holder.v(R.id.btn_send);
                button.setText(R.string.text_immediately_bind);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        }));

    }
}

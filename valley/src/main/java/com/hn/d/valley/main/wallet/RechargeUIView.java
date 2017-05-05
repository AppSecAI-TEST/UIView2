package com.hn.d.valley.main.wallet;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.hn.d.valley.R;
import com.hn.d.valley.main.message.redpacket.PayUIDialog;
import com.hn.d.valley.main.message.redpacket.ThirdPayUIDialog;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.List;

import static android.R.attr.button;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/02 10:30
 * 修改人员：hewking
 * 修改时间：2017/05/02 10:30
 * 修改备注：
 * Version: 1.0.0
 */
public class RechargeUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    private Button button;

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_recharege));
    }

    @Override
    protected int getItemLayoutId(int viewType) {

        if (viewType == 1) {
            return R.layout.item_redpacket_button_view;
        }

        if (mRExBaseAdapter.isLast(viewType)) {
            return R.layout.item_single_main_text_view;
        }

        return R.layout.item_input_view;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {

        int top = mActivity.getResources().getDimensionPixelOffset(R.dimen.base_xhdpi);

        items.add(ViewItemInfo.build(new ItemOffsetCallback(top) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.input_tip_view).setText(R.string.text_jine_yuan);
                final EditText edit_text_view = holder.v(R.id.edit_text_view);
                TextWatcher textWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        boolean enable = edit_text_view.getText().toString().length() > 0;
                        button.setEnabled(enable);
                    }
                };
                edit_text_view.addTextChangedListener(textWatcher);

            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(2 * top) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                button = holder.v(R.id.btn_send);
                button.setText(R.string.text_next_step);
                button.setText(R.string.text_immediately_pay);
                button.setEnabled(false);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: 2017/5/2 recharge use alipay
                        PayUIDialog.Params params = new PayUIDialog.Params(1,Integer.valueOf("0"),"","0",null,0);
                        startIView(new ThirdPayUIDialog(params,ThirdPayUIDialog.ALIPAY));
                    }
                });
            }
        }));

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                TextView tv_tip = holder.tv(R.id.text_view);

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tv_tip.getLayoutParams();
                params.gravity = Gravity.CENTER_HORIZONTAL;
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                tv_tip.setLayoutParams(params);

                tv_tip.setTextColor(getResources().getColor(R.color.default_base_bg_dark));
                tv_tip.setGravity(Gravity.CENTER);
                tv_tip.setText(R.string.text_next_show_agree_klg_protocl);
            }
        }));

    }
}

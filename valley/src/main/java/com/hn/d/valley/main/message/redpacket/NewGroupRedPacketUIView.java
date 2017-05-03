package com.hn.d.valley.main.message.redpacket;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.UI;
import com.hn.d.valley.R;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/24 16:45
 * 修改人员：hewking
 * 修改时间：2017/04/24 16:45
 * 修改备注：
 * Version: 1.0.0
 */
public class NewGroupRedPacketUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    private String to_gid;

    public NewGroupRedPacketUIView(String to_gid) {
       this.to_gid = to_gid;
    }


    @Override
    protected TitleBarPattern getTitleBar() {

        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();

        rightItems.add(TitleBarPattern.TitleBarItem.build().setText("红包规则").setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }));

        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_send_redpacket)).setRightItems(rightItems);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_new_group_redpacket;

    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {

                final EditText etMoney = holder.v(R.id.et_money);
                final EditText etContent = holder.v(R.id.et_content);
                final Button btn_send = holder.v(R.id.btn_send);
                final EditText et_count = holder.v(R.id.et_count);
                final TextView tv_cursor = holder.v(R.id.tv_cursor);

                UI.setViewHeight(etContent, mActivity.getResources().getDimensionPixelOffset(R.dimen.base_100dpi));

                TextWatcher textWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        boolean enable = etMoney.getText().toString().length() > 0;
                        tv_cursor.setVisibility(!enable ? View.VISIBLE : View.GONE);
                        btn_send.setEnabled(enable && et_count.getText().toString().length() > 0);
                    }
                };

                etMoney.addTextChangedListener(textWatcher);
                et_count.addTextChangedListener(textWatcher);

                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String content = etContent.getText().toString();
                        if ("".equals(content)) {
                            content = etContent.getHint().toString();
                        }
                        PayUIDialog.Params params = new PayUIDialog.Params(Integer.valueOf(et_count.getText().toString())
                                ,Integer.valueOf(etMoney.getText().toString()),content,null,to_gid);
                        mOtherILayout.startIView(new PayUIDialog(params));
                    }
                });
            }
        }));
    }


}

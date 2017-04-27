package com.hn.d.valley.main.message.redpacket;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.widget.RTextView;
import com.hn.d.valley.R;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

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
public class NewRedPacketUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    private String to_uid;

    public NewRedPacketUIView(String to_uid) {
       this.to_uid = to_uid;
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
        return R.layout.item_new_redpacket;

    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {

//        final int left = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi_5);
//        int line = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {

                final EditText etMoney = holder.v(R.id.et_money);
                final EditText etContent = holder.v(R.id.et_content);
                Button btn_send = holder.v(R.id.btn_send);

                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PayUIDialog.Params params = new PayUIDialog.Params(1,Integer.valueOf(etMoney.getText().toString()),etContent.getText().toString(),to_uid);
                        mOtherILayout.startIView(new PayUIDialog(params));
                    }
                });
            }
        }));
    }


}

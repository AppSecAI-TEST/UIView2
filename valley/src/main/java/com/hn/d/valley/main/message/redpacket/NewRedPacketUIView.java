package com.hn.d.valley.main.message.redpacket;
import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
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
public class NewRedPacketUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {


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

        if (viewType == 0) {
            return R.layout.item_redpacket_input;
        }

        if (viewType == 1) {
            return R.layout.item_red_input_note;
        }

        if (viewType == 2) {
            return R.layout.item_single_text_view;
        }

        if (viewType == 3 ) {
            return R.layout.item_redpacket_button_view;
        }

        return R.layout.item_redpacket_button_view;

    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {

        final int size = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi_15);
        int line = mActivity.getResources().getDimensionPixelSize(R.dimen.base_line);

        items.add(ViewItemInfo.build(new ItemLineCallback(mBaseOffsetSize, mBaseLineSize) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {

            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(mBaseLineSize) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {

            }

        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(mBaseLineSize) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                TextView textView = holder.v(R.id.text_view);
                textView.setText(R.string.text_redpacket_notice);
            }
        }));


        items.add(ViewItemInfo.build(new ItemOffsetCallback( 3 * mBaseLineSize) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {

                holder.itemView.findViewById(R.id.text_view).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOtherILayout.startIView(new PayUIDialog());
                    }
                });

            }
        }));




    }



}

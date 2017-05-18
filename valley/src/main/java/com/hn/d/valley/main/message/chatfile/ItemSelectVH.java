package com.hn.d.valley.main.message.chatfile;

import android.view.View;
import android.widget.CheckBox;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.hn.d.valley.R;
import com.hn.d.valley.main.message.slide.ISlide;

import static com.angcyo.uiview.recycler.adapter.RGroupAdapter.TYPE_GROUP_DATA;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/17 14:43
 * 修改人员：hewking
 * 修改时间：2017/05/17 14:43
 * 修改备注：
 * Version: 1.0.0
 */
public class ItemSelectVH extends RBaseViewHolder implements ISlide {

    private CheckBox cb_select;


    public ItemSelectVH(View itemView, int viewType) {
        super(itemView, viewType);
        if (viewType == TYPE_GROUP_DATA) {
            cb_select = (CheckBox) itemView.findViewById(R.id.delete_view);
        }

    }

    @Override
    public void slideOpen() {
        if (getItemViewType() == TYPE_GROUP_DATA) {
            cb_select.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void slideClose() {
        if (getItemViewType() == TYPE_GROUP_DATA) {
            cb_select.setVisibility(View.GONE);
            cb_select.setChecked(false);
        }
    }
}

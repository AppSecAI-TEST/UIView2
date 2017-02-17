package com.hn.d.valley.main.me.setting;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseAdapter;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RDragRecyclerView;
import com.hn.d.valley.R;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：编辑个人资料
 * 创建人员：Robi
 * 创建时间：2017/02/17 17:42
 * 修改人员：Robi
 * 修改时间：2017/02/17 17:42
 * 修改备注：
 * Version: 1.0.0
 */
public class EditInfoUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    @Override
    protected int getTitleResource() {
        return R.string.edit_into_title;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().addRightItem(TitleBarPattern.TitleBarItem
                .build(mActivity.getResources().getString(R.string.finish), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }));
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_drag_recycler_view;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        items.add(new ViewItemInfo(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                RDragRecyclerView dragRecyclerView = holder.v(R.id.drag_recycler_view);
                dragRecyclerView.setAdapter(new RBaseAdapter<String>(mActivity) {
                    @Override
                    protected int getItemLayoutId(int viewType) {
                        return 0;
                    }

                    @Override
                    protected View createContentView(ViewGroup parent, int viewType) {
                        return new ImageView(mActivity);
                    }

                    @Override
                    public int getItemCount() {
                        return 10;
                    }

                    @Override
                    protected void onBindView(RBaseViewHolder holder, int position, String bean) {
                        ((ImageView) holder.itemView).setImageResource(R.drawable.default_page);
                    }
                });
            }
        }));
    }
}

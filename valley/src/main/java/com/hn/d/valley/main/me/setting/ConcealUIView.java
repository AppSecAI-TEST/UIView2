package com.hn.d.valley.main.me.setting;

import android.view.View;
import android.widget.CompoundButton;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.sub.other.BlackListUIView;
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
                CompoundButton switchCompat = holder.v(R.id.switch_view);
                switchCompat.setChecked(MsgNotifySetting.instance().isBindContacts());
                switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        MsgNotifySetting.instance().enableBindContacts(isChecked);
                    }
                });
                itemInfoLayout.setItemText("不允许查看我的粉丝及关注列表");
            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                CompoundButton switchCompat = holder.v(R.id.switch_view);
                switchCompat.setChecked(MsgNotifySetting.instance().isBindContacts());
                switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        MsgNotifySetting.instance().enableBindContacts(isChecked);
                    }
                });
                itemInfoLayout.setItemText("不出现在附近");
            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                CompoundButton switchCompat = holder.v(R.id.switch_view);
                switchCompat.setVisibility(View.GONE);

                itemInfoLayout.setItemText("不让TA看我的动态");
                itemInfoLayout.setRightDrawableRes(R.drawable.base_next);

                itemInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });

            }
        }));

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                CompoundButton switchCompat = holder.v(R.id.switch_view);
                switchCompat.setVisibility(View.GONE);

                itemInfoLayout.setItemText("不看TA的动态");
                itemInfoLayout.setRightDrawableRes(R.drawable.base_next);

                itemInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });

            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(size) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                CompoundButton switchCompat = holder.v(R.id.switch_view);
                switchCompat.setVisibility(View.GONE);

                itemInfoLayout.setItemText("联系人黑名单");
                itemInfoLayout.setRightDrawableRes(R.drawable.base_next);

                itemInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(new BlackListUIView());
                    }
                });

            }
        }));
    }
}

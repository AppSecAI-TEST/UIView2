package com.hn.d.valley.main.me.setting;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.widget.SwitchCompat;
import android.text.TextPaint;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/02/17 10:55
 * 修改人员：Robi
 * 修改时间：2017/02/17 10:55
 * 修改备注：
 * Version: 1.0.0
 */
public class MessageNotifyUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    @Override
    protected String getTitleString() {
        return "消息通知";
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == 0 || viewType == 2 || viewType == 4 || viewType == 6 || viewType == 7 || viewType == 9) {
            return R.layout.item_switch_view;
        }
        return R.layout.item_single_text_view;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        final int size = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                SwitchCompat switchCompat = holder.v(R.id.switch_view);
                switchCompat.setChecked(MsgNotifySetting.instance().isEnableNoti());
                switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        MsgNotifySetting.instance().toggleNotification(isChecked);
                    }
                });
                infoLayout.setItemText("接受新消息通知");
            }

            @Override
            public void setItemOffsets(Rect rect) {
                rect.set(0, size, 0, 0);
            }
        }));
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                TextView textView = holder.v(R.id.text_view);
                textView.setText("关闭或开启恐龙谷的新消息通知.");
            }
        }));

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                SwitchCompat switchCompat = holder.v(R.id.switch_view);
                switchCompat.setChecked(MsgNotifySetting.instance().isShowMsgNotiDetail());
                switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        MsgNotifySetting.instance().showMsgNotiDetail(isChecked);
                    }
                });
                infoLayout.setItemText("通知显示消息详情");
            }

            @Override
            public void setItemOffsets(Rect rect) {
                rect.set(0, size, 0, 0);
            }
        }));
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                TextView textView = holder.v(R.id.text_view);
                textView.setText("关闭后,当收到恐龙谷消息时,通知提示将不显示发信人和内容摘要.");
            }
        }));

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setRightDrawableRes(R.drawable.base_next);
                infoLayout.setItemText("功能消息免打扰");
                holder.v(R.id.switch_view).setVisibility(View.GONE);
            }

            @Override
            public void setItemOffsets(Rect rect) {
                rect.set(0, size, 0, 0);
            }
        }));
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                TextView textView = holder.v(R.id.text_view);
                textView.setText("设置系统功能消息提示声音和振动的时段.");
            }
        }));

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                SwitchCompat switchCompat = holder.v(R.id.switch_view);
                switchCompat.setChecked(MsgNotifySetting.instance().isRing());
                switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        MsgNotifySetting.instance().setRing(isChecked);
                    }
                });
                infoLayout.setItemText("声音");
            }

            @Override
            public void setItemOffsets(Rect rect) {
                rect.set(0, size, 0, 0);
            }
        }));
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                SwitchCompat switchCompat = holder.v(R.id.switch_view);
                switchCompat.setChecked(MsgNotifySetting.instance().isVirbrate());
                switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        MsgNotifySetting.instance().setVibrate(isChecked);
                    }
                });
                infoLayout.setItemText("震动");
            }

            @Override
            public void setItemOffsets(Rect rect) {
                rect.set(0, mActivity.getResources().getDimensionPixelSize(R.dimen.base_line), 0, 0);
            }

            @Override
            public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {
                paint.setColor(Color.WHITE);
                offsetRect.set(itemView.getLeft(), itemView.getTop() - offsetRect.top,
                        itemView.getLeft() + size, itemView.getTop());
                canvas.drawRect(offsetRect, paint);
            }
        }));

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                TextView textView = holder.v(R.id.text_view);
                textView.setText("当恐龙谷运行时,你可以设置是否需要声音或者振动.");
            }
        }));

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText("圈子动态更新");
            }

            @Override
            public void setItemOffsets(Rect rect) {
                rect.set(0, size, 0, 0);
            }
        }));
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                TextView textView = holder.v(R.id.text_view);
                textView.setText("关闭后,有朋友更新动态时,界面下面的\"首页\"切换按钮上不再出现红点提示.");
            }
        }));
    }
}

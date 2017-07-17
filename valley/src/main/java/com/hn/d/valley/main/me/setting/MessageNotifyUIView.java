package com.hn.d.valley.main.me.setting;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.TextPaint;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.mixpush.MixPushService;

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
        return mActivity.getString(R.string.texc_msg_notify);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == 0 || viewType == 2 || viewType == 3 || viewType == 5) {
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
                final CompoundButton switchCompat = holder.v(R.id.switch_view);
                switchCompat.setChecked(MsgNotifySetting.instance().isEnableNoti());
                switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                        MsgNotifySetting.instance().toggleNotification(isChecked);
                    }
                });
                infoLayout.setItemText(mActivity.getString(R.string.text_reveive_msg_notify));
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
                textView.setText(R.string.text_enable_klg_msg_notify);
            }
        }));

//        items.add(ViewItemInfo.build(new ItemCallback() {
//            @Override
//            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
//                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
//                CompoundButton switchCompat = holder.v(R.id.switch_view);
//                switchCompat.setChecked(MsgNotifySetting.instance().isShowMsgNotiDetail());
//                switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        MsgNotifySetting.instance().showMsgNotiDetail(isChecked);
//                    }
//                });
//                infoLayout.setItemText(mActivity.getString(R.string.text_notification_enable_detail));
//            }
//
//            @Override
//            public void setItemOffsets(Rect rect) {
//                rect.set(0, size, 0, 0);
//            }
//        }));
//        items.add(ViewItemInfo.build(new ItemCallback() {
//            @Override
//            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
//                TextView textView = holder.v(R.id.text_view);
//                textView.setText(R.string.text_notification_detail_tip);
//            }
//        }));

//        items.add(ViewItemInfo.build(new ItemCallback() {
//            @Override
//            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
//                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
//                infoLayout.setRightDrawableRes(R.drawable.base_next);
//                infoLayout.setItemText(mActivity.getString(R.string.text_func_msg_bother));
//                holder.v(R.id.switch_view).setVisibility(View.GONE);
//            }
//
//            @Override
//            public void setItemOffsets(Rect rect) {
//                rect.set(0, size, 0, 0);
//            }
//        }));
//        items.add(ViewItemInfo.build(new ItemCallback() {
//            @Override
//            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
//                TextView textView = holder.v(R.id.text_view);
//                textView.setText(R.string.tex_set_msg_perid_of_time);
//            }
//        }));

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                CompoundButton switchCompat = holder.v(R.id.switch_view);
                switchCompat.setChecked(MsgNotifySetting.instance().isRing());
                switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        MsgNotifySetting.instance().setRing(isChecked);
                    }
                });
                infoLayout.setItemText(mActivity.getString(R.string.text_sound));
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
                CompoundButton switchCompat = holder.v(R.id.switch_view);
                switchCompat.setChecked(MsgNotifySetting.instance().isVirbrate());
                switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        MsgNotifySetting.instance().setVibrate(isChecked);
                    }
                });
                infoLayout.setItemText(mActivity.getString(R.string.text_virbrate));
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
                textView.setText(R.string.text_sound_virbrate_tip);
            }
        }));

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText(mActivity.getString(R.string.text_friend_circle_dynamic_notify));
                CompoundButton switchCompat = holder.v(R.id.switch_view);
                switchCompat.setChecked(MsgNotifySetting.instance().isCircleCNotify());
                switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        MsgNotifySetting.instance().enableCircleNotify(isChecked);
                    }
                });
                infoLayout.setItemText(mActivity.getString(R.string.text_friend_circle_dynamic_notify));
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
                textView.setText(R.string.text_cirlcle_show_tip);
            }
        }));

//        items.add(ViewItemInfo.build(new ItemCallback() {
//            @Override
//            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
//                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
//                infoLayout.setItemText(mActivity.getString(R.string.text_subscribe_redpacket_notify));
//                CompoundButton switchCompat = holder.v(R.id.switch_view);
//                switchCompat.setChecked(MsgNotifySetting.instance().isCircleCNotify());
//                switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        MsgNotifySetting.instance().enableCircleNotify(isChecked);
//                    }
//                });
//                infoLayout.setItemText(mActivity.getString(R.string.text_subscribe_redpacket_notify));
//            }
//
//            @Override
//            public void setItemOffsets(Rect rect) {
//                rect.set(0, size, 0, 0);
//            }
//        }));
//        items.add(ViewItemInfo.build(new ItemCallback() {
//            @Override
//            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
//                TextView textView = holder.v(R.id.text_view);
//                textView.setText(R.string.text_redpacket_noti_tip);
//            }
//        }));
    }
}

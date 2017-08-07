package com.hn.d.valley.main.me.setting;

import android.view.View;
import android.widget.CompoundButton;

import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.service.SettingService;
import com.hn.d.valley.sub.other.BlackListUIView;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.List;

import static com.hn.d.valley.main.me.setting.SpecialListUIView.NOT_ACCESS_OTHER;
import static com.hn.d.valley.main.me.setting.SpecialListUIView.NOT_ALLOW_ACCESS_ME;

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
                        add(RRetrofit.create(SettingService.class)
                                .set(Param.buildMap("key:look_fans", "val:" + (isChecked ? "0" : "1")))
                                .compose(Rx.transformer(String.class))
                                .subscribe(new BaseSingleSubscriber<String>() {
                                    @Override
                                    public void onSucceed(String bean) {
                                        super.onSucceed(bean);
                                        T_.ok(bean);
                                    }
                                }));
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
                        add(RRetrofit.create(SettingService.class)
                                .set(Param.buildMap("key:hide_location", "val:" + (isChecked ? "1" : "0")))
                                .compose(Rx.transformer(String.class))
                                .subscribe(new BaseSingleSubscriber<String>() {
                                    @Override
                                    public void onSucceed(String bean) {
                                        super.onSucceed(bean);
                                        T_.ok(bean);
                                    }
                                }));
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

                itemInfoLayout.setItemText(mActivity.getString(R.string.text_not_allow_access_me));
                itemInfoLayout.setRightDrawableRes(R.drawable.base_next);

                itemInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(new SpecialListUIView(NOT_ALLOW_ACCESS_ME));
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

                itemInfoLayout.setItemText(mActivity.getString(R.string.text_not_see_other));
                itemInfoLayout.setRightDrawableRes(R.drawable.base_next);

                itemInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(new SpecialListUIView(NOT_ACCESS_OTHER));
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

                itemInfoLayout.setItemText(getString(R.string.blacklist_title));
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

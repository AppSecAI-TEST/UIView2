package com.hn.d.valley.main.me.setting;

import android.view.View;
import android.widget.CompoundButton;

import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.service.SettingService;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：设置动态权限
 * 创建人员：Robi
 * 创建时间：2017/02/28 17:53
 * 修改人员：Robi
 * 修改时间：2017/02/28 17:53
 * 修改备注：
 * Version: 1.0.0
 */
public class DynamicPermissionUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    UserInfoBean mInfoBean;

    public DynamicPermissionUIView(UserInfoBean infoBean) {
        mInfoBean = infoBean;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_switch_view;
    }

    @Override
    protected int getTitleResource() {
        return R.string.set_dynamic_permission;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        int offset = getResources().getDimensionPixelOffset(R.dimen.base_xhdpi);
        int line = getResources().getDimensionPixelOffset(R.dimen.base_line);
        items.add(ViewItemInfo.build(new ItemOffsetCallback(offset) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                super.onBindView(holder, posInData, dataBean);
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText(getString(R.string.cant_see_my_status_tip));

                final CompoundButton switchView = holder.v(R.id.switch_view);
                switchView.setChecked(!(mInfoBean.getLook_my_discuss() == 1));
                switchView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int value = switchView.isChecked() ? 0 : 1;
                        mInfoBean.setLook_my_discuss(value);

                        add(RRetrofit.create(SettingService.class)
                                .personal(Param.buildMap("to_uid:" + mInfoBean.getUid(), "key:1001", "val:" + value))
                                .compose(Rx.transformer(String.class))
                                .subscribe(new BaseSingleSubscriber<String>() {
                                }));
                    }
                });
                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switchView.performClick();
                    }
                });
            }
        }));

        items.add(ViewItemInfo.build(new ItemLineCallback(offset, line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                super.onBindView(holder, posInData, dataBean);
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText(getString(R.string.dont_see_he_status_tip));

                final CompoundButton switchView = holder.v(R.id.switch_view);
                switchView.setChecked(!(mInfoBean.getLook_his_discuss() == 1));
                switchView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int value = switchView.isChecked() ? 0 : 1;
                        mInfoBean.setLook_his_discuss(value);

                        add(RRetrofit.create(SettingService.class)
                                .personal(Param.buildMap("to_uid:" + mInfoBean.getUid(), "key:1002", "val:" + value))
                                .compose(Rx.transformer(String.class))
                                .subscribe(new BaseSingleSubscriber<String>() {
                                }));
                    }
                });
                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switchView.performClick();
                    }
                });
            }
        }));
    }
}

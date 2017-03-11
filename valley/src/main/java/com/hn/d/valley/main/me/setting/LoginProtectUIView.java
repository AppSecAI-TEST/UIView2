package com.hn.d.valley.main.me.setting;

import android.graphics.Rect;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;

import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.service.SettingService;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.List;

import io.realm.Realm;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：登录保护
 * 创建人员：Robi
 * 创建时间：2017/02/17 09:18
 * 修改人员：Robi
 * 修改时间：2017/02/17 09:18
 * 修改备注：
 * Version: 1.0.0
 */
public class LoginProtectUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    boolean isSetLoginProtect = false;

    public LoginProtectUIView(boolean isSetLoginProtect) {
        this.isSetLoginProtect = isSetLoginProtect;
    }

    @Override
    protected void onBindDataView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
        dataBean.mCallback.onBindView(holder, posInData, dataBean);
    }

    @Override
    protected String getTitleString() {
        return mActivity.getString(R.string.login_protect_title);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == 0) {
            return R.layout.item_switch_view;
        }
        return R.layout.item_single_text_view;
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        items.add(new ViewItemInfo(new ItemCallback() {

            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText(mActivity.getString(R.string.login_protect_title));
                SwitchCompat switchCompat = holder.v(R.id.switch_view);
                switchCompat.setChecked(isSetLoginProtect);
                switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                        add(RRetrofit.create(SettingService.class)
                                .loginProtect(Param.buildMap("type:" + (isChecked ? 1 : 2)))
                                .compose(Rx.transformer(String.class))
                                .subscribe(new BaseSingleSubscriber<String>() {
                                    @Override
                                    public void onSucceed(String bean) {
                                        super.onSucceed(bean);
                                        T_.show(bean);
                                        RRealm.exe(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                UserCache.instance().getUserInfoBean().setIs_login_protect((isChecked ? 1 : 0));
                                            }
                                        });
                                        UserCache.instance().updateUserInfo();
                                    }
                                })
                        );
                    }
                });
            }

            @Override
            public void setItemOffsets(Rect rect) {
                rect.top = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);
            }
        }));
        items.add(new ViewItemInfo(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.text_view).setText(R.string.open_login_protect_tip);
            }
        }));
    }
}

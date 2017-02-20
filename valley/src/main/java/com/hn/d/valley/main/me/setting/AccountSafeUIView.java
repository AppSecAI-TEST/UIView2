package com.hn.d.valley.main.me.setting;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExItemDecoration;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：账户安全界面
 * 创建人员：Robi
 * 创建时间：2017/02/15 09:50
 * 修改人员：Robi
 * 修改时间：2017/02/15 09:50
 * 修改备注：
 * Version: 1.0.0
 */
public class AccountSafeUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    @Override
    protected String getTitleString() {
        return mActivity.getString(R.string.account_safe);
    }

    @Override
    protected void onBindDataView(RBaseViewHolder holder, int posInData, ItemRecyclerUIView.ViewItemInfo dataBean) {
        ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
        infoLayout.setItemText(dataBean.itemString);
        if (dataBean.itemClickListener == null) {
            infoLayout.setClickable(false);
        } else {
            infoLayout.setOnClickListener(dataBean.itemClickListener);
        }
        if (posInData == 0) {
            infoLayout.setItemDarkText(UserCache.getUserAccount());
        } else if (posInData == 1) {
            final String phone = UserCache.instance().getUserInfoBean().getPhone();
            if (TextUtils.isEmpty(phone)) {
                infoLayout.setItemDarkText(mActivity.getString(R.string.not_bind_phone));
            } else {
                infoLayout.setItemDarkText(phone);
            }
        } else if (posInData == 2) {
            if (UserCache.instance().getUserInfoBean().getIs_set_password() == 1) {
                infoLayout.setItemDarkText("");
                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(new SetPasswordUIView(true));
                    }
                });
            } else {
                infoLayout.setItemDarkText(mActivity.getString(R.string.not_set));
                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(new SetPasswordUIView(false));
                    }
                });
            }
        } else if (posInData == 3) {
            final int is_login_protect = UserCache.instance().getUserInfoBean().getIs_login_protect();
            if (is_login_protect == 1) {
                infoLayout.setItemDarkText(mActivity.getString(R.string.is_open));
            } else {
                infoLayout.setItemDarkText(mActivity.getString(R.string.not_open));
            }
            infoLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startIView(new LoginProtectUIView(is_login_protect == 1));
                }
            });
        }
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_info_layout;
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
        mRecyclerView.addItemDecoration(new RExItemDecoration(new RExItemDecoration.ItemDecorationCallback() {
            @Override
            public Rect getItemOffsets(LinearLayoutManager layoutManager, int position) {
                Rect rect = new Rect(0, 0, 0, 0);
                rect.top = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);
                return rect;
            }

            @Override
            public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {
            }
        }));
    }

    @Override
    public void onViewReShow(Bundle bundle) {
        super.onViewReShow(bundle);
        mRExBaseAdapter.notifyDataSetChanged();
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        items.add(new ViewItemInfo(mActivity.getString(R.string.id), null));
        items.add(new ViewItemInfo(mActivity.getString(R.string.phone_number2), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(new BindPhoneUIView());
            }
        }));
        items.add(new ViewItemInfo(mActivity.getString(R.string.password), null));
        items.add(new ViewItemInfo(mActivity.getString(R.string.login_safe), null));
    }
}

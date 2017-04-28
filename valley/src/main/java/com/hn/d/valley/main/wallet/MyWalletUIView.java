package com.hn.d.valley.main.wallet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.RApplication;
import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/27 17:20
 * 修改人员：hewking
 * 修改时间：2017/04/27 17:20
 * 修改备注：
 * Version: 1.0.0
 */
public class MyWalletUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo>{

    private WalletAccount mAccount;

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();

        rightItems.add(TitleBarPattern.TitleBarItem.build().setText(mActivity.getString(R.string.text_bill)).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(new BillUIView());
            }
        }));

        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_my_wallet)).setRightItems(rightItems);
    }

    @Override
    protected int getItemLayoutId(int viewType) {

        if (viewType ==  0) {
            return R.layout.item_wallet_desc;
        }

        if (viewType == 1 || viewType == 3) {
            return R.layout.item_info_layout;
        }

        if (viewType == 2
                || viewType == 4
                || viewType == 6) {
            return R.layout.item_single_main_text_view;
        }

        if (viewType == 5) {
            return R.layout.item_switch_view;
        }

        return R.layout.item_info_layout;
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        loadAccount();
    }

    private void loadAccount() {
        add(RRetrofit.create(WalletService.class)
                .account(Param.buildInfoMap("uid:" + UserCache.getUserAccount(),"device:" + RApplication.getIMEI()))
                .compose(Rx.transformer(WalletAccount.class))
                .subscribe(new BaseSingleSubscriber<WalletAccount>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoadView();
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                        hideLoadView();
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        showEmptyLayout();
                    }

                    @Override
                    public void onSucceed(WalletAccount bean) {
                        super.onSucceed(bean);
                        showContentLayout();
                        mAccount = bean;
                    }
                }));
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {

        final int top = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);


        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                bindBalance(holder);
            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(top) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText(mActivity.getString(R.string.text_pay_passwd));

            }
        }));

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {

                TextView text = parseTextView(holder, 12);
                text.setText(R.string.text_pay_passwd_notice);


            }
        }));

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText(mActivity.getString(R.string.text_bind_alipay));
                infoLayout.setItemDarkText(mActivity.getString(R.string.text_not_set));
            }
        }));

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                TextView text = parseTextView(holder, 12);
                text.setText(R.string.text_bind_alipay_notice);
            }
        }));

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                SwitchCompat switchCompat = holder.v(R.id.switch_view);
                itemInfoLayout.setItemText(mActivity.getString(R.string.text_finger_pay));
                switchCompat.setChecked(mAccount.getFingerprint() == 1);
            }
        }));

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                TextView text = parseTextView(holder, 12);
                text.setText(R.string.text_fingerprient_notice);
            }
        }));

    }

    private void bindBalance(RBaseViewHolder holder) {

        if (mAccount == null) {
            return;
        }

        final TextView tv_balance = holder.tv(R.id.tv_balance);
        TextView btn_recharge = holder.v(R.id.btn_recharge);
        TextView btn_crashout = holder.v(R.id.btn_crashout);

        tv_balance.setText("￥ " + mAccount.getMoney() / 100f);

    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }

    @NonNull
    private TextView parseTextView(RBaseViewHolder holder, int top) {
        TextView text = holder.tv(R.id.text_view);
        text.setTextColor(ContextCompat.getColor(mActivity,R.color.main_text_color_dark));
        text.setTextSize(top);
        return text;
    }
}

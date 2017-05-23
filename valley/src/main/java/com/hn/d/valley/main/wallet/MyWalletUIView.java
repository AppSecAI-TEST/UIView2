package com.hn.d.valley.main.wallet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.realm.LoginBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.me.setting.BindPhoneUIView;
import com.hn.d.valley.main.message.groupchat.RequestCallback;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hwangjr.rxbus.annotation.Subscribe;

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
                ) {
            return R.layout.item_single_main_text_view;
        }


        return R.layout.item_info_layout;
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        loadAccount();
    }

    private void loadAccount() {
        WalletHelper.getInstance().fetchWallet(new RequestCallback<WalletAccount>() {
            @Override
            public void onStart() {
                showLoadView();
            }

            @Override
            public void onSuccess(WalletAccount walletAccount) {
                hideLoadView();
                mAccount = walletAccount;
                showContentLayout();
            }

            @Override
            public void onError(String msg) {
                hideLoadView();
                showEmptyLayout();
            }
        });
    }

    private boolean isBindPhone(){
        LoginBean loginBean = UserCache.instance().getLoginBean();
        return TextUtils.isEmpty(loginBean.getPhone());
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
                if (mAccount == null) {
                    return;
                }
                infoLayout.setItemDarkText(mAccount.hasPin() ? "" : mActivity.getString(R.string.text_not_set));
                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAccount.hasPin()) {
                            startIView(new PayPwdSettingUIView());
                        } else {
                            if (!isBindPhone()) {
                                startIView(new SetPayPwdUIView(SetPayPwdUIView.SETPAYPWD));
                            } else {
                                toBindPhone();
                            }
                        }
                    }
                });

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
                if (mAccount == null) {
                    return;
                }
                if (mAccount.hasAlipay()) {
                    infoLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startIView(new BindAliPayTipUIView(true));
                        }
                    });
                } else {
                    infoLayout.setItemDarkText(mActivity.getString(R.string.text_not_bind));
                    infoLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (! mAccount.hasPin()) {
                                T_.show(mActivity.getString(R.string.text_no_setting_pwd));
                                return;
                            }
                            if (!isBindPhone()) {
                                startIView(new BindAliPayTipUIView(false));
                            } else {
                                toBindPhone();
                            }
                        }
                    });
                }
            }
        }));

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                TextView text = parseTextView(holder, 12);
                text.setText(R.string.text_bind_alipay_notice);
            }
        }));

    }

    private void toBindPhone() {
        UIDialog.build()
                .setDialogContent(mActivity.getString(R.string.text_please_bind_phone))
                .setOkText(mActivity.getString(R.string.text_bind_phone))
                .setCancelText(mActivity.getString(R.string.cancel))
                .setCancelListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setOkListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(new BindPhoneUIView());
                    }
                })
                .showDialog(mParentILayout);
    }

    private void bindBalance(RBaseViewHolder holder) {

        if (mAccount == null) {
            return;
        }

        final TextView tv_balance = holder.tv(R.id.tv_balance);
        TextView btn_recharge = holder.v(R.id.btn_recharge);
        TextView btn_crashout = holder.v(R.id.btn_crashout);

//        btn_crashout.setBackground(ResUtil.generateRoundBorderDrawable(mActivity.getResources().getDimensionPixelOffset(R.dimen.little_round_radius),
//                mActivity.getResources().getDimensionPixelOffset(R.dimen.base_line),
//                SkinHelper.getSkin().getThemeSubColor(),
//                ContextCompat.getColor(mActivity, R.color.base_text_color_dark)));

        if (mAccount.hasMoney() && mAccount.hasPin()) {
            btn_crashout.setEnabled(true);
        } else {
            btn_crashout.setEnabled(false);
        }

        tv_balance.setText("￥ " + mAccount.getMoney() / 100f);
        btn_recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(new RechargeUIView());
            }
        });

        btn_crashout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isBindPhone()) {
                    startIView(new RefundUIView());
                } else {
                    toBindPhone();
                }
            }
        });

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

    @Subscribe
    public void onEvent(WalletAccountUpdateEvent event) {
        loadAccount();
    }
}

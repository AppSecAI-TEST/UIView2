package com.hn.d.valley.main.wallet;

import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ExEditText;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.security.PublicKey;
import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/02 16:06
 * 修改人员：hewking
 * 修改时间：2017/05/02 16:06
 * 修改备注：
 * Version: 1.0.0
 */
public class VerifyAlipayUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    private boolean isBind;

    public VerifyAlipayUIView(boolean isBind) {
        this.isBind = isBind;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_verifyalipay));
    }

    private ExEditText et_account;
    private ExEditText et_name;

    @Override
    protected int getItemLayoutId(int viewType) {

        if (viewType == 0) {
            return R.layout.item_single_main_text_view;
        }

        if (mRExBaseAdapter.isLast(viewType)) {
            return R.layout.item_redpacket_button_view;
        }

        return R.layout.item_input_view;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {

        int top = mActivity.getResources().getDimensionPixelOffset(R.dimen.base_xhdpi);

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.text_view).setText(R.string.text_input_need_bind_info);
            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(top) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.input_tip_view).setText(R.string.text_account);
                et_account = holder.v(R.id.edit_text_view);
                et_account.setHint(R.string.text_input_full_account);
                et_account.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                et_account.setMaxLength(18);
            }
        }));

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.input_tip_view).setText(R.string.text_name);
                et_name = holder.v(R.id.edit_text_view);
                et_name.setHint(R.string.text_input_full_name);
                et_name.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                et_name.setMaxLength(18);
            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(3 * top) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                Button button = holder.v(R.id.btn_send);
                if (isBind) {
                    button.setText(R.string.text_immediately_bind);
                } else {
                    button.setText(R.string.text_not_immedately_bind);
                }
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isBind) {
                            bindAlipay();
                        } else {
                            unBindAlipay();
                        }
                    }
                });
            }
        }));

    }

    private void unBindAlipay() {
        String account = et_account.getText().toString();
        String realname = et_name.getText().toString();

        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(realname)) {
            return;
        }
        RRetrofit.create(WalletService.class)
                .cashaccountRemove(Param.buildInfoMap("uid:" + UserCache.getUserAccount(), "type:" + 0))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        T_.show(mActivity.getString(R.string.text_unbind_success));
                    }

                    @Override
                    public void onSucceed(String beans) {

                    }
                });
    }

    private void bindAlipay() {
        //params check
        String account = et_account.getText().toString();
        String realname = et_name.getText().toString();

        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(realname)) {
            return;
        }

        // 支付宝 type 账户类型【0支付宝、1微信，没有默认值，必填】
        RRetrofit.create(WalletService.class)
                .cashaccountSet(Param.buildInfoMap("uid:" + UserCache.getUserAccount(), "type:" + 0, "account:" + account, "realname:" + realname))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        T_.show(mActivity.getString(R.string.text_bind_success));
                    }

                    @Override
                    public void onSucceed(String beans) {

                    }
                });

    }
}

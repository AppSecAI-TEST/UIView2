package com.hn.d.valley.main.wallet;

import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ExEditText;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.utils.RBus;

import org.json.JSONException;
import org.json.JSONObject;

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

        items.add(ViewItemInfo.build(new ItemCallback() {
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
                        if (isBind) {
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
                .compose(WalletHelper.getTransformer())
                .subscribe(new BaseSingleSubscriber<String>() {

                    @Override
                    public void onSucceed(String beans) {
                        parseResult(beans, false);
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
                .compose(WalletHelper.getTransformer())
                .subscribe(new BaseSingleSubscriber<String>() {

                    @Override
                    public void onSucceed(String beans) {
                        parseResult(beans, true);
                    }
                });
    }

    /**
     * 400 参数缺失
     * 405 未绑定手机
     * 406 支付宝已经被其他用户绑定
     * 500 服务器错误
     * 701 当日操作次数已达上限
     * 601 账户还有余额，不允许删除提现账户
     *
     * @param beans
     */
    private void parseResult(String beans, boolean isBind) {

        int code = -1;
        int data = 0;
        try {
            JSONObject jsonObject = new JSONObject(beans);
            code = jsonObject.optInt("code");
            data = jsonObject.optInt("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (200 == code) {
            if (true) {
                T_.show(mActivity.getString(R.string.text_bind_success));
            } else {
                T_.show(mActivity.getString(R.string.text_unbind_success));
            }

            RBus.post(new WalletAccountUpdateEvent());

            if (200 == code) {
                T_.show(getString(R.string.text_refund_success));
                RBus.post(new WalletAccountUpdateEvent());
            } else if (400 == code) {
                T_.show(getString(R.string.text_params_lose));
            } else if (405 == code) {
                T_.show(getString(R.string.text_unbind_phone));
            } else if (406 == code) {
                T_.show(getString(R.string.text_alipay_had_bind));
            } else if (500 == code) {
                T_.show(getString(R.string.tex_server_error));
            } else if (701 == code) {
                T_.show(getString(R.string.text_operate_count_enough));
            } else if (601 == code) {
                T_.show(getString(R.string.text_cannot_delete_account));
            }
            finishIView();
        }
    }
}

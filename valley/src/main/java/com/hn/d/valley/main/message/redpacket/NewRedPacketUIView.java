package com.hn.d.valley.main.message.redpacket;

import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.github.utilcode.utils.SpannableStringUtils;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.UI;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.realm.LoginBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.me.setting.BindPhoneUIView;
import com.hn.d.valley.main.message.groupchat.RequestCallback;
import com.hn.d.valley.main.wallet.RechargeUIView;
import com.hn.d.valley.main.wallet.SetPayPwdUIView;
import com.hn.d.valley.main.wallet.WalletAccount;
import com.hn.d.valley.main.wallet.WalletAccountUpdateEvent;
import com.hn.d.valley.main.wallet.WalletHelper;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.utils.RBus;
import com.hn.d.valley.x5.X5WebUIView;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

import static com.hn.d.valley.main.message.redpacket.GrabPacketHelper.MAX_REDBAG;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/24 16:45
 * 修改人员：hewking
 * 修改时间：2017/04/24 16:45
 * 修改备注：
 * Version: 1.0.0
 */
public class NewRedPacketUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    private String to_uid;

    public NewRedPacketUIView(String to_uid) {
       this.to_uid = to_uid;
    }


    @Override
    protected TitleBarPattern getTitleBar() {

        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();

        rightItems.add(TitleBarPattern.TitleBarItem.build().setText(mActivity.getString(R.string.text_rp_rule)).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(new X5WebUIView(Constants.REDPACKET_PROTOCOL));
            }
        }));
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_send_redpacket)).setRightItems(rightItems);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_new_redpacket;

    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {

//        final int left = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi_5);
//        int line = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {

                final EditText etMoney = holder.v(R.id.et_money);
                final EditText etContent = holder.v(R.id.et_content);
                final Button btn_send = holder.v(R.id.btn_send);
                final TextView tv_cursor = holder.v(R.id.tv_cursor);

                TextView tv_notice = holder.v(R.id.item_notice);
                tv_notice.setMovementMethod(LinkMovementMethod.getInstance());
                tv_notice.setText(SpannableStringUtils.getBuilder(mActivity.getString(R.string.text_next_show_agree_klg_protocl))
                        .append(mActivity.getString(R.string.text__klg_protocl))
                        .setClickSpan(new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {
                                startIView(new X5WebUIView(Constants.WALLET_PROTOCOL));
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setColor(mActivity.getResources().getColor(R.color.main_text_color));
                                ds.setUnderlineText(false);
                                ds.clearShadowLayer();
                            }
                        }).append("\n").append(mActivity.getString(R.string.text_refound_to_wallet_24hour))
                        .create());


                UI.setViewHeight(etContent, mActivity.getResources().getDimensionPixelOffset(R.dimen.base_100dpi));

                TextWatcher textWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        boolean enable = etMoney.getText().toString().length() > 0;
                        if (!enable) {
                            btn_send.setEnabled(false);
                            return;
                        }
                        float money = Float.valueOf(etMoney.getText().toString());
                        tv_cursor.setVisibility(!enable ? View.VISIBLE : View.GONE);
                        if (money > MAX_REDBAG) {
                            enable = false;
                            T_.show(mActivity.getString(R.string.text_hongbao_lower_200));
                        }
                        if (money < 0.01f) {
                            enable = false;
//                            T_.show(mActivity.getString(R.string.text_hongbao_lower_001));
                        }

                        btn_send.setEnabled(enable);
                    }
                };

                etMoney.addTextChangedListener(textWatcher);

                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (WalletHelper.getInstance().getWalletAccount() == null) {
                            WalletHelper.getInstance().fetchWallet(new RequestCallback<WalletAccount>() {
                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onSuccess(WalletAccount o) {
                                    if (o.hasPin()) {
                                        performClick(etContent, etMoney,o);
                                    } else {
                                        showPinDialog();
                                    }
                                }

                                @Override
                                public void onError(String msg) {

                                }
                            });
                            return;
                        }

                        if (!WalletHelper.getInstance().getWalletAccount().hasPin()) {
                            showPinDialog();
                        } else {
                            performClick(etContent,etMoney,WalletHelper.getInstance().getWalletAccount());
                        }
                    }
                });
            }
        }));
    }

    private void showPinDialog() {
        UIDialog.build()
                .setDialogContent(mActivity.getString(R.string.text_no_set_pwd_please_set_pwd))
                .setOkText(mActivity.getString(R.string.text_set_pay_pwd))
                .setOkListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 先判断是否绑定手机
                        if (!UserCache.instance().isBindPhone()) {
                            startIView(new BindPhoneUIView());
                            T_.show(mActivity.getString(R.string.text_unselected_phonenumber));
                        } else {
                            startIView(new SetPayPwdUIView(SetPayPwdUIView.SETPAYPWD));
                        }
                    }
                })
                .setCancelText(getString(R.string.cancel))
                .showDialog(mParentILayout);
    }

    private void performClick(EditText etContent, EditText etMoney,WalletAccount account) {
        String content = etContent.getText().toString();
        if ("".equals(content)) {
            content = etContent.getHint().toString();
        }
        float money = Float.valueOf(etMoney.getText().toString()) * 100;

        PayUIDialog.Params params = new PayUIDialog.Params();
        params.setBalance(account.getMoney())
                .setMoney(money)
                .setNum(1)
                .setContent(content)
                .setTo_uid(to_uid)
                .setRandom(0)
                .setType(1);
        if (money > account.getMoney()) {
            T_.show(getString(R.string.text_balance_not_enough));

            params.enableBalance(false);
            startIView(new ThirdPayUIDialog(new Action1() {
                @Override
                public void call(Object o) {
                    finishIView();
                }
            },params, ThirdPayUIDialog.ALIPAY,1));

            return;
        }


        mParentILayout.startIView(new PayUIDialog(new Action1() {
            @Override
            public void call(Object o) {
                finishIView();
            }
        },params));
    }


}

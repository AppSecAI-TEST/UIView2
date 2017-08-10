package com.hn.d.valley.main.wallet;

import android.text.Editable;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.github.utilcode.utils.SpannableStringUtils;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.redpacket.Constants;
import com.hn.d.valley.main.message.redpacket.PayUIDialog;
import com.hn.d.valley.main.message.redpacket.RechargeAndRefundUIDialog;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.utils.RBus;
import com.hn.d.valley.widget.HnLoading;
import com.hn.d.valley.x5.X5WebUIView;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import rx.functions.Action1;

import static com.hn.d.valley.utils.MathUtils.decimal;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/03 9:28
 * 修改人员：hewking
 * 修改时间：2017/05/03 9:28
 * 修改备注：
 * Version: 1.0.0
 */
public class RefundUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    ExEditText et_money;
    Button btn_next;
    private TextView tv_note;

    private float INTEREST = 0.0055f;

    private boolean refundTotal;

    public RefundUIView() {
        INTEREST = WalletHelper.getInstance().getWalletAccount().getCashout_rate();
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_tixian));
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == 1) {
            return R.layout.item_wallet_refound_input;
        }

        if (viewType == 2) {
            return R.layout.item_redpacket_button_view;
        }

        if (mRExBaseAdapter.isLast(viewType)) {
            return R.layout.item_single_main_text_view;
        }

        return R.layout.item_info_layout;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        int top = mActivity.getResources().getDimensionPixelOffset(R.dimen.base_xhdpi);

        items.add(ViewItemInfo.build(new ItemOffsetCallback(top) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText(mActivity.getString(R.string.text_receive_account));

                if (WalletHelper.getInstance().getWalletAccount().hasAlipay()) {
                    int type = WalletHelper.getInstance().getWalletAccount().bindType();
                    String account ;
                    if (type == 0) {
                        account = WalletHelper.getInstance().getWalletAccount().getAlipay().split(";;;")[0];
                    } else {
                        account = WalletHelper.getInstance().getWalletAccount().getAlipay_userid().split(";;;")[1];
                    }
                    infoLayout.setItemDarkText(String.format(mActivity.getString(R.string.text_alipay_account), account));
                } else {
                    infoLayout.setItemDarkText(mActivity.getString(R.string.text_please_bind_alipay));
                }
                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (WalletHelper.getInstance().getWalletAccount().hasAlipay()) {
                            mParentILayout.startIView(new BindAliPayTipUIView(true));
                        } else {
                            mParentILayout.startIView(new BindAliPayTipUIView(false));
                        }
                    }
                });
            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(top) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                TextView tv_tip = holder.v(R.id.tv_tip);
                tv_note = holder.v(R.id.tv_note);
                et_money = holder.v(R.id.et_count);
                et_money.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
                et_money.setMaxNumber(10000);
//                et_money.setHint(R.string.text_not_over_10000);
                et_money.setDecimalCount(2);

                String prestr = mActivity.getString(R.string.text_can_refund_money) + WalletHelper.getInstance().getWalletAccount().getMoney() / 100f + " ,";
                final SpannableStringBuilder totalRefundBuilder = SpannableStringUtils.getBuilder(prestr)
                        .append(mActivity.getString(R.string.text_can_refund_money))
                        .setClickSpan(new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {
                                refundTotal = true;
                                et_money.setText(WalletHelper.getInstance().getWalletAccount().getMoney() / 100f + "");
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setUnderlineText(false);
                                ds.setColor(SkinHelper.getSkin().getThemeSubColor());
                            }
                        })
                        .create();

                et_money.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        refundTotal = false;
                        if (TextUtils.isEmpty(s.toString())) {
                            tv_note.setText(totalRefundBuilder);
                            return;
                        }

                        float amount = Float.valueOf(s.toString());
                        int doorsill = WalletHelper.getInstance().getWalletAccount().getCashout_doorsill() / 100;
                        if (amount < doorsill) {
                            tv_note.setText(SpannableStringUtils.getBuilder(String.format(getString(R.string.text_refund_money_10), doorsill))
                                    .setForegroundColor(getColor(R.color.base_red)).create());
                            btn_next.setEnabled(false);
                            return;
                        }

                        if (amount > 10000) {
                            tv_note.setText(getString(R.string.text_not_over_10000));
                            return;
                        }

                        if (WalletHelper.getInstance().getWalletAccount().getMoney() / 100f == amount) {
                            et_money.unableCallback();
                            et_money.setText(amount * (1 - INTEREST) + "");
                            // refuoundtotal 赋值在settext 后执行 因为settext 会执行回调 aftertextchange refoundtotal = false
                            refundTotal = true;
                            tv_note.setText(String.format(getString(R.string.text_refund_service_change_rate), decimal(amount * INTEREST,2)));
                        }
//                        amount * 0.0055  ,(1 - 0.0055) * amount;
                        tv_note.setText(String.format(getString(R.string.text_refund_service_change_rate), decimal(amount * INTEREST,2)));
                        btn_next.setEnabled(true);
                    }
                });

                tv_tip.setText(R.string.text_write_refund_money);
                tv_note.setMovementMethod(LinkMovementMethod.getInstance());
                tv_note.setText(totalRefundBuilder);

            }
        }));

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                btn_next = holder.v(R.id.btn_send);
                btn_next.setText(R.string.text_next_step);
                btn_next.setEnabled(false);
                btn_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String value = et_money.getText().toString();
                        if (TextUtils.isEmpty(value)) {
                            T_.show(getString(R.string.text_refun_input_none_tip));
                            btn_next.setEnabled(false);
                        } else if (WalletHelper.getInstance().getWalletAccount().getMoney() / 100f < Float.valueOf(value)) {
                            T_.show(getString(R.string.text_refun_money_tip));
                            btn_next.setEnabled(false);
                        } else {
                            btn_next.setEnabled(true);
                            cashoutRequest();
                        }
                    }
                });
            }
        }));

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.itemView.setPadding(0, 0, 0, 0);
                TextView tv_tip = holder.tv(R.id.text_view);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tv_tip.getLayoutParams();
                params.gravity = Gravity.CENTER_HORIZONTAL;
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                tv_tip.setLayoutParams(params);
                tv_tip.setTextColor(getResources().getColor(R.color.default_base_bg_dark));
                tv_tip.setGravity(Gravity.CENTER_HORIZONTAL);
                tv_tip.setText(R.string.text_next_show_agree_klg_protocl);

                tv_tip.setMovementMethod(LinkMovementMethod.getInstance());
                tv_tip.setText(SpannableStringUtils.getBuilder(mActivity.getString(R.string.text_next_show_agree_klg_protocl))
                        .append(mActivity.getString(R.string.text__klg_protocl))
                        .setClickSpan(new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {
                                startIView(new X5WebUIView(Constants.WALLET_PROTOCOL));
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
//                                ds.setColor(mActivity.getResources().getColor(R.color.main_text_color));
                                ds.setUnderlineText(false);
                                ds.clearShadowLayer();
                            }
                        })
                        .create());
            }
        }));

    }

    private void cashoutRequest() {
        final String money = et_money.getText().toString();
        if (TextUtils.isEmpty(money)) {
            T_.show(getString(R.string.text_input_error));
            return;
        }
//        float amount = Float.valueOf(money);
//        float totalAmount = amount - decimal(amount);
        float totalAmount = Float.valueOf(money);
        if (refundTotal) {
            totalAmount = WalletHelper.getInstance().getWalletAccount().getMoney() / 100f;
        }
//        UIDialog.build()
//                .setDialogContent(String.format(getString(R.string.text_notice_refund_shouxufei
//                        ,)
//                .setOkText(getString(R.string.text_crashout))
//                .setOkListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
        PayUIDialog.Params params = new PayUIDialog.Params();
        params.setMoney(totalAmount);
        params.setType(4);
        startIView(new RechargeAndRefundUIDialog(new Action1() {
            @Override
            public void call(Object o) {
                finishIView();
            }
        }, params));
//                    }
//                }).
//                showDialog(mILayout);
    }


}

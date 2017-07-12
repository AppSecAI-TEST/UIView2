package com.hn.d.valley.main.wallet;

import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.uiview.github.utilcode.utils.SpannableStringUtils;
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

import java.util.List;

import rx.functions.Action1;

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

    public RefundUIView() {

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
                    infoLayout.setItemDarkText(String.format(mActivity.getString(R.string.text_alipay_account), WalletHelper.getInstance().getWalletAccount().getAlipay().split(";;;")[0]));
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
                TextView tv_note = holder.v(R.id.tv_note);
                et_money = holder.v(R.id.et_count);

                et_money.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        btn_next.setEnabled(!TextUtils.isEmpty(s.toString()));
                    }
                });

                tv_tip.setText(R.string.text_write_refund_money);
                String prestr = mActivity.getString(R.string.text_can_refund_money) + WalletHelper.getInstance().getWalletAccount().getMoney() / 100f + " ,";
                tv_note.setMovementMethod(LinkMovementMethod.getInstance());
                tv_note.setText(SpannableStringUtils.getBuilder(prestr)
                        .append(mActivity.getString(R.string.text_can_refund_money))
                        .setClickSpan(new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {
                                et_money.setText(WalletHelper.getInstance().getWalletAccount().getMoney() / 100f + "");
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setUnderlineText(false);
                                ds.setColor(SkinHelper.getSkin().getThemeSubColor());
                            }
                        })
                        .create());

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
        String money = et_money.getText().toString();
        if (TextUtils.isEmpty(money)) {
            return;
        }
        PayUIDialog.Params params = new PayUIDialog.Params();
        params.setMoney(Float.valueOf(money));
        params.setType(4);
        startIView(new RechargeAndRefundUIDialog(new Action1() {
            @Override
            public void call(Object o) {
                finishIView();
            }
        },params));


    }




}

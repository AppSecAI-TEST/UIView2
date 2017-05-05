package com.hn.d.valley.main.wallet;

import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.List;

import rx.functions.Action1;

import static com.hn.d.valley.main.message.redpacket.NewGroupRedPacketUIView.buildClickSpan;
import static com.hn.d.valley.main.message.redpacket.NewGroupRedPacketUIView.wrapSpan;

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
                    infoLayout.setItemDarkText(String.format("支付宝账号: %s",WalletHelper.getInstance().getWalletAccount().getAlipay().split(";;;")[0]));
                } else {
                    infoLayout.setItemDarkText(mActivity.getString(R.string.text_please_bind_alipay));
                }
                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (WalletHelper.getInstance().getWalletAccount().hasAlipay()) {
                            mOtherILayout.startIView(new BindAliPayTipUIView(true));
                        }  else {
                            mOtherILayout.startIView(new BindAliPayTipUIView(false));
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
                final ExEditText et_money = holder.v(R.id.et_count);

                tv_tip.setText("填写提现金额");
                String prestr = "可提现金额 " + WalletHelper.getInstance().getWalletAccount().getMoney() / 100f + " ,";
                SpannableString clickSpan = buildClickSpan(prestr,
                        "全额提现", SkinHelper.getSkin().getThemeSubColor(), prestr.length(), prestr.length() + 4, new Action1() {
                            @Override
                            public void call(Object o) {
                                et_money.setText(WalletHelper.getInstance().getWalletAccount().getMoney() / 100f + "");
                            }
                        });
                tv_note.setText(clickSpan);
                tv_note.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(5 * top) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                Button btn_next = holder.v(R.id.btn_send);
                btn_next.setText(R.string.text_next_step);
                btn_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // TODO: 2017/5/5 弹出输入框 支付宝

                    }
                });
            }
        }));

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                TextView tv_tip = holder.tv(R.id.text_view);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tv_tip.getLayoutParams();
                params.gravity = Gravity.CENTER_HORIZONTAL;
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                tv_tip.setLayoutParams(params);
                tv_tip.setTextColor(getResources().getColor(R.color.default_base_bg_dark));
                tv_tip.setGravity(Gravity.CENTER_HORIZONTAL);
                tv_tip.setText(R.string.text_next_show_agree_klg_protocl);
            }
        }));

    }
}

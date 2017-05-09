package com.hn.d.valley.main.wallet;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.hn.d.valley.R;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/02 11:55
 * 修改人员：hewking
 * 修改时间：2017/05/02 11:55
 * 修改备注：
 * Version: 1.0.0
 */
public class BindAliPayTipUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    private boolean isBind;

    public BindAliPayTipUIView(boolean isBind) {
        this.isBind = isBind;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_bind_alipay));
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_bind_alipay_tip;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                Button btn_bind = holder.v(R.id.btn_send);
                TextView tv_tip = holder.v(R.id.tv_isbind);
                TextView tv_note = holder.v(R.id.tv_username);

                if (isBind) {
                    tv_tip.setVisibility(View.VISIBLE);
                    tv_tip.setText(R.string.text_have_bind);
                    tv_note.setText(String.format("支付宝账号: %s",WalletHelper.getInstance().getWalletAccount().getAlipay().split(";;;")[0]));
                    btn_bind.setText(R.string.text_not_immedately_bind);
                    btn_bind.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (WalletHelper.getInstance().getWalletAccount().getMoney() > 0) {
                                mOtherILayout.startIView(new UnableUnBindUIView());
                            } else {
                                replaceIView(new VerifyAlipayUIView(false));
                            }
                        }
                    });
                } else {
                    btn_bind.setText(R.string.text_immediately_bind);
                    btn_bind.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            replaceIView(new VerifyAlipayUIView(true));
                        }
                    });
                }

            }
        }));
    }


}

package com.hn.d.valley.main.wallet;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.angcyo.uiview.base.Item;
import com.angcyo.uiview.base.SingleItem;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.TimeUtil;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseItemUIView;
import com.hn.d.valley.main.me.setting.FeedBackUIView;
import com.hn.d.valley.main.message.redpacket.GrabedRDResultUIView;
import com.hn.d.valley.widget.HnGlideImageView;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：恐龙君 个人详情页
 * 创建人员：Robi
 * 创建时间：2017/04/10 09:25
 * 修改人员：Robi
 * 修改时间：2017/04/10 09:25
 * 修改备注：
 * Version: 1.0.0
 */
public class BillDetailUIView extends BaseItemUIView {

    private BillRecord billRecord;

    public BillDetailUIView(BillRecord billRecord) {
        this.billRecord = billRecord;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setShowBackImageView(true)
                .setTitleString(getString(R.string.text_bill_detail));
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_bill_detail_layout;
    }

    @Override
    protected void createItems(List<SingleItem> items) {
        items.add(new SingleItem() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                ItemInfoLayout info_transaction_type = holder.v(R.id.info_transaction_type);
                ItemInfoLayout info_amount = holder.v(R.id.info_amount);
                ItemInfoLayout info_rp_detail = holder.v(R.id.info_rp_detail);
                ItemInfoLayout info_transaction_time = holder.v(R.id.info_transaction_time);
                ItemInfoLayout info_transtaction_number = holder.v(R.id.info_transtaction_number);

                info_amount.getDarkTextView().setTextColor(SkinHelper.getSkin().getThemeDarkColor());
                info_amount.setItemDarkText(String.format(getString(R.string.text_yuan_amout),billRecord.getMoney() / 100f));
                info_transaction_type.setItemDarkText(billRecord.getDescription());

                //充值
                if (billRecord.getSub_type() == 1) {

                    info_rp_detail.setItemText("充值时间");
                    info_rp_detail.setItemDarkText(TimeUtil.getDatetime(billRecord.getCreated() * 1000l));
                    info_transaction_time.setItemText("支付方式");
                    info_transaction_time.setItemDarkText(billRecord.getTransferway());

                } else if (billRecord.getSub_type() == 2){
                    //提现
                    info_rp_detail.setItemText("提现时间");
                    info_rp_detail.setItemDarkText(TimeUtil.getDatetime(billRecord.getCreated() * 1000l));
                    info_transaction_time.setItemText("提现方式");
                    info_transaction_time.setItemDarkText(billRecord.getTransferway());
                } else if (billRecord.getSub_type() == 3) {
                    // 红包
                    info_rp_detail.setItemDarkText("查看");
                    info_rp_detail.getDarkTextView().setTextColor(SkinHelper.getSkin().getThemeDarkColor());
                    info_transaction_time.setItemDarkText(TimeUtil.getDatetime(billRecord.getCreated() * 1000l));
                    info_rp_detail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startIView(new GrabedRDResultUIView(Long.valueOf(billRecord.getPayid())));                        }
                    });
                }

                info_transtaction_number.setItemDarkText(billRecord.getPayid());


            }
        });
    }

    @Override
    public int getDefaultBackgroundColor() {
        return ContextCompat.getColor(mActivity,R.color.base_white);
    }
}

package com.hn.d.valley.main.wallet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import com.angcyo.github.utilcode.utils.ClipboardUtils;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.Item;
import com.angcyo.uiview.base.SingleItem;
import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.TimeUtil;
import com.angcyo.uiview.utils.string.StringUtil;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.google.gson.JsonObject;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseItemUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.BillRecord;
import com.hn.d.valley.bean.BillRecordDetailBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.redpacket.GrabedRDResultUIView;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import retrofit2.Retrofit;

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
    private String id;
    private BillRecordDetailBean billDetail;

    public BillDetailUIView(BillRecord billRecord) {
        this.billRecord = billRecord;
        this.id = String.valueOf(billRecord.getId());
    }

    public BillDetailUIView(String id) {
        this.id = id;
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
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
//        loadData();
    }

    private void loadData() {
        showLoadView();
        add(RRetrofit.create(WalletService.class)
        .recordDetail(Param.buildInfoMap("uid:" + UserCache.getUserAccount(),"id:" + id))
        .compose(Rx.transformer(BillRecordDetailBean.class))
                .subscribe(new BaseSingleSubscriber<BillRecordDetailBean>() {

                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        hideLoadView();
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                    }

                    @Override
                    public void onSucceed(BillRecordDetailBean bean) {
                        super.onSucceed(bean);
                        billDetail = bean;
                        showContentLayout();
                    }
                }));
    }

    @Override
    protected void createItems(List<SingleItem> items) {
//        L.d("billdetailUIview : " + billDetail.getSub_type()  + "   " + billDetail.getDescription());
        items.add(new SingleItem() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, Item dataBean) {
                ItemInfoLayout info_transaction_type = holder.v(R.id.info_transaction_type);
                ItemInfoLayout info_amount = holder.v(R.id.info_amount);
                ItemInfoLayout info_rp_detail = holder.v(R.id.info_rp_detail);
                ItemInfoLayout info_transaction_time = holder.v(R.id.info_transaction_time);
                ItemInfoLayout info_transtaction_number = holder.v(R.id.info_transtaction_number);
                ItemInfoLayout info_refund_time = holder.v(R.id.info_refund_time);
                ItemInfoLayout info_refund_number = holder.v(R.id.info_refund_number);
                ItemInfoLayout info_refund_account = holder.v(R.id.info_refund_account);

                info_amount.getDarkTextView().setTextColor(SkinHelper.getSkin().getThemeDarkColor());
                info_amount.setItemDarkText(String.format(getString(R.string.text_yuan_amout), billRecord.getMoney() / 100f));
                info_transaction_type.setItemDarkText(billRecord.getDescription());

                //充值
                if (billRecord.getSub_type() == 1) {
                    info_rp_detail.setItemText("充值时间");
                    info_rp_detail.setItemDarkText(TimeUtil.getDatetime(billRecord.getCreated() * 1000l));
                    info_transaction_time.setItemText("支付方式");
                    info_transaction_time.setItemDarkText(billRecord.getTransferway());
                } else if (billRecord.getSub_type() == 2) {
                    //提现
                    String extend = billRecord.getExtend();
                    try {
                        JSONObject jsonObject = new JSONObject(extend);
                        BigDecimal b = new BigDecimal(jsonObject.optDouble("poundage") / 100);
                        float amount = b.setScale(2, RoundingMode.HALF_UP).floatValue();
                        info_rp_detail.setItemText("手续费");
                        info_rp_detail.setItemDarkText(String.format("￥ %.2f",amount));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    info_refund_time.setVisibility(View.VISIBLE);
                    info_refund_number.setVisibility(View.VISIBLE);
                    info_refund_account.setVisibility(View.VISIBLE);

                    info_refund_time.setItemText("提现账户");
                    info_refund_number.setItemText("提现申请时间");
                    info_refund_account.setItemText("提现到账时间");

                    info_transaction_time.setItemText("交易状态");

                    if (billRecord.getCashout_time() == 0) {
                        info_transaction_time.setItemDarkText("提现处理中");
                        info_refund_account.setItemDarkText("----------");

                    } else {
                        info_transaction_time.setItemDarkText("提现成功");
                        info_refund_account.setItemDarkText(TimeUtil.getDatetime(billRecord.getCreated() * 1000l));
                    }

                    info_refund_time.setItemDarkText(billRecord.getTransferway());
                    info_refund_number.setItemDarkText(TimeUtil.getDatetime(billRecord.getCreated() * 1000l));



                } else if (billRecord.getSub_type() == 3
                        || billRecord.getSub_type() == 0
                        || billRecord.getSub_type() == 5) {
                    // 红包
                    info_rp_detail.setItemDarkText("查看");
                    info_rp_detail.getDarkTextView().setTextColor(SkinHelper.getSkin().getThemeDarkColor());
                    info_transaction_time.setItemDarkText(TimeUtil.getDatetime(billRecord.getCreated() * 1000l));
                    info_rp_detail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (TextUtils.isEmpty(billRecord.getPayid())) {
                                return;
                            }
                            startIView(new GrabedRDResultUIView(Long.valueOf(billRecord.getPayid())));
                        }
                    });
                }

                info_transtaction_number.setItemDarkText(billRecord.getPayid());
                info_transtaction_number.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardUtils.copyText(billRecord.getPayid());
                        T_.info("交易单号已复制.");
                    }
                });

            }
        });
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.CONTENT;
    }

    @Override
    public int getDefaultBackgroundColor() {
        return ContextCompat.getColor(mActivity, R.color.base_white);
    }
}

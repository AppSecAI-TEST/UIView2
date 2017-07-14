package com.hn.d.valley.main.wallet;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.dialog.UIBottomItemDialog;
import com.angcyo.uiview.github.utilcode.utils.SpannableStringUtils;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.utils.TimeUtil;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.KlgCoinBean;
import com.hn.d.valley.bean.ListModel;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/28 10:53
 * 修改人员：hewking
 * 修改时间：2017/04/28 10:53
 * 修改备注：
 * Version: 1.0.0
 */
public class KlGCoinBillUIView extends SingleRecyclerUIView<KlgCoinBean>{

    private int type = 0 ;//默认 全部

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();
        rightItems.add(TitleBarPattern.TitleBarItem.build().setText(mActivity.getString(R.string.text_suaixuan)).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreDialog();
            }
        }));
        return super.getTitleBar().setTitleString(getString(R.string.text_klgcoin_record)).setRightItems(rightItems);
    }

    @Override
    protected RExBaseAdapter<String, KlgCoinBean, String> initRExBaseAdapter() {
        return new BillRecordAdapter(mActivity);
    }

    private void showMoreDialog() {
        UIBottomItemDialog.build()
                .setUseWxStyle(true)
                .addItem(getString(R.string.text_all), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type = 0;
                        loadData();
                    }
                })
                .addItem(getString(R.string.text_income), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type = 1;
                        loadData();
                    }
                })
                .addItem(getString(R.string.text_outcome), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        type = 2;
                        loadData();
                    }
                })
                .showDialog(this);
    }

    @Override
    protected boolean isLoadInViewPager() {
        return false;
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);

        add(RRetrofit.create(WalletService.class)
                .records(Param.buildMap("uid:" + UserCache.getUserAccount(),"type:" + type))
                .compose(Rx.transformer(KLGCoinList.class))
                .subscribe(new BaseSingleSubscriber<KLGCoinList>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoadView();
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        hideLoadView();
                    }


                    @Override
                    public void onSucceed(KLGCoinList beans) {
                        hideLoadView();
                        if (beans == null || beans.getData_list().size() == 0) {
                            onUILoadDataEnd();
                        } else {
                            onUILoadDataEnd(beans.getData_list());
                        }
                    }
                }));

    }

    private class BillRecordAdapter extends RExBaseAdapter<String,KlgCoinBean,String> {

        public BillRecordAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_wallet_billrecord;
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, int posInData, KlgCoinBean dataBean) {
            super.onBindDataView(holder, posInData, dataBean);

            TextView tv_bill_type = holder.tv(R.id.tv_bill_type);
            TextView tv_time = holder.tv(R.id.tv_time);
            TextView tv_bill_moncy = holder.tv(R.id.tv_bill_moncy);

            tv_bill_type.setText(dataBean.getAction_desc());
            tv_time.setText(TimeUtil.getTimeShowString(Long.valueOf(dataBean.getDetail().getPay_time()) * 1000l,true));
            if (dataBean.getIn_out() .equals("1")) {
                tv_bill_moncy.setText(SpannableStringUtils.getBuilder("+" + dataBean.getDetail().getCoin())
                        .setForegroundColor(mActivity.getResources().getColor(R.color.base_red))
                        .create());
            } else if(dataBean.getIn_out() == "2") {
                tv_bill_moncy.setText("-" + dataBean.getDetail().getCoin());
            }

        }
    }

    @Override
    protected RecyclerView.ItemDecoration initItemDecoration() {
        return super.createBaseItemDecoration().setMarginStart(mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi));
    }

    public static class KLGCoinList extends ListModel<KlgCoinBean> {

    }
}

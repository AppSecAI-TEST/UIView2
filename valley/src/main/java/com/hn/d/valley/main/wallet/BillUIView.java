package com.hn.d.valley.main.wallet;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.utils.TimeUtil;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
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
public class BillUIView extends SingleRecyclerUIView<BillRecord>{

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();
        rightItems.add(TitleBarPattern.TitleBarItem.build().setText(mActivity.getString(R.string.text_suaixuan)).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }));
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_bill)).setRightItems(rightItems);
    }

    @Override
    protected RExBaseAdapter<String, BillRecord, String> initRExBaseAdapter() {
        return new BillRecordAdapter(mActivity);
    }

    @Override
    protected boolean isLoadInViewPager() {
        return false;
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);

        add(RRetrofit.create(WalletService.class)
                .recordCheck(Param.buildInfoMap("uid:" + UserCache.getUserAccount()))
                .compose(Rx.transformerList(BillRecord.class))
                .subscribe(new BaseSingleSubscriber<List<BillRecord>>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoadView();
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                        hideLoadView();
                    }

                    @Override
                    public void onSucceed(List<BillRecord> beans) {
                        if (beans == null || beans.size() == 0) {
                            onUILoadDataEnd();
                        } else {
                            onUILoadDataEnd(beans);
                        }
                    }
                }));

    }

    private class BillRecordAdapter extends RExBaseAdapter<String,BillRecord,String> {

        public BillRecordAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_wallet_billrecord;
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, int posInData, BillRecord dataBean) {
            super.onBindDataView(holder, posInData, dataBean);

            TextView tv_bill_type = holder.tv(R.id.tv_bill_type);
            TextView tv_time = holder.tv(R.id.tv_time);
            TextView tv_bill_moncy = holder.tv(R.id.tv_bill_moncy);

            tv_bill_type.setText(dataBean.getDescription());
            tv_time.setText(TimeUtil.getTimeShowString(dataBean.getCreated() * 1000l,true));
            if (dataBean.getType() == 0) {
                tv_bill_moncy.setText("+" + dataBean.getMoney());
            } else if(dataBean.getType() == 1) {
                tv_bill_moncy.setText("-" + dataBean.getMoney());

            }

        }
    }

    @Override
    protected RBaseItemDecoration initItemDecoration() {
        return super.initItemDecoration().setMarginStart(mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi));
    }
}
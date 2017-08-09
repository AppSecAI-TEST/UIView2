package com.hn.d.valley.main.wallet;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.angcyo.github.utilcode.utils.SpannableStringUtils;
import com.angcyo.uiview.dialog.UIItemDialog;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.utils.TimeUtil;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.BillRecord;
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
public class BillUIView extends SingleRecyclerUIView<BillRecord> {

    //0全部、1充值、2提现、3红包、4消费、5打赏
    private int type = 0;//默认 全部
    private int lastId;

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();
        rightItems.add(TitleBarPattern.TitleBarItem.build()
                .setText(mActivity.getString(R.string.text_suaixuan))
                .setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreDialog();
            }
        }));
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_bill)).setRightItems(rightItems);
    }

    @Override
    protected RExBaseAdapter<String, BillRecord, String> initRExBaseAdapter() {
        return new BillRecordAdapter(mActivity);
    }

    private void showMoreDialog() {
        UIItemDialog.build()
                .setUseFullItem(true)
                .addItem(getString(R.string.text_all), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastId = 0;
                        type = 0;
                        loadData();
                    }
                })
                .addItem("充值", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastId = 0;
                        type = 1;
                        loadData();
                    }
                })
                .addItem("提现", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastId = 0;
                        type = 2;
                        loadData();
                    }
                })
                .addItem("红包", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastId = 0;
                        type = 3;
                        loadData();
                    }
                })
                .addItem("消费", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastId = 0;
                        type = 4;
                        loadData();
                    }
                })
                .addItem("打赏", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastId = 0;
                        type = 5;
                        loadData();
                    }
                })
                .addItem("奖励", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastId = 0;
                        type = 6;
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
    protected void onEmptyData(boolean isEmpty) {
        super.onEmptyData(isEmpty);
        initOverEmptyLayout("暂无账单记录", R.drawable.image_nothing);
    }

    @Override
    protected void initOverEmptyLayout(String text, int topIco) {
        super.initOverEmptyLayout(text, topIco);
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(WalletService.class)
                .recordCheck(Param.buildInfoMap("uid:" + UserCache.getUserAccount(), "subtype:" + type, "limit:20", "lastid:" + lastId))
                .compose(Rx.transformerList(BillRecord.class))
                .subscribe(new BaseSingleSubscriber<List<BillRecord>>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoadView();
                    }

                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        hideLoadView();
                    }

                    @Override
                    public void onSucceed(List<BillRecord> beans) {
                        if (beans == null || beans.size() == 0) {
                            if (mRExBaseAdapter.isItemEmpty()) {
                                onUILoadDataEnd();
                            } else {
                                onUILoadDataFinish();
                                mRExBaseAdapter.setNoMore(true);
                            }
                        } else {
                            lastId = beans.get(beans.size() - 1).getId();
                            onUILoadDataEnd(beans);
                        }
                    }
                }));

    }

    @Override
    protected RecyclerView.ItemDecoration initItemDecoration() {
        return super.createBaseItemDecoration().setMarginStart(mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi));
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    private class BillRecordAdapter extends RExBaseAdapter<String, BillRecord, String> {

        public BillRecordAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_wallet_billrecord;
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, int posInData, final BillRecord dataBean) {
            super.onBindDataView(holder, posInData, dataBean);

            TextView tv_bill_type = holder.tv(R.id.tv_bill_type);
            TextView tv_time = holder.tv(R.id.tv_time);
            TextView tv_bill_moncy = holder.tv(R.id.tv_bill_moncy);

            tv_bill_type.setText(dataBean.getDescription());
            tv_time.setText(TimeUtil.getDatetime(dataBean.getCreated() * 1000l));
            if (dataBean.getType() == 0) {
                tv_bill_moncy.setText(SpannableStringUtils.getBuilder("+" + dataBean.getMoney() / 100f)
                        .setForegroundColor(mActivity.getResources().getColor(R.color.base_red))
                        .create());
            } else if (dataBean.getType() == 1) {
                tv_bill_moncy.setText("-" + dataBean.getMoney() / 100f);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startIView(new BillDetailUIView(dataBean));
                }
            });

        }
    }
}

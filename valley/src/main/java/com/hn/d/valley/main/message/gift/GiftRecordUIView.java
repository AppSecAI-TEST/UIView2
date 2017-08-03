package com.hn.d.valley.main.message.gift;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.TextViewCompat;
import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.utils.TimeUtil;
import com.angcyo.uiview.widget.GlideImageView;
import com.angcyo.uiview.widget.RTextView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.GiftRecordBean;
import com.hn.d.valley.bean.ListModel;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;
import com.hn.d.valley.widget.HnGlideImageView;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：cjh
 * 创建时间：2017/8/2
 * 修改人员：cjh
 * 修改时间：2017/8/2
 * 修改备注：
 * Version: 1.0.0
 */
public class GiftRecordUIView extends SingleRecyclerUIView<GiftRecordBean> {

    private String uid;
    private int type;

    public GiftRecordUIView(String uid,int type) {
        this.uid = uid;
        this.type = type;
    }


    @Override
    protected TitleBarPattern getTitleBar() {
        if (isInSubUIView()) {
            return null;
        }
        return super.getTitleBar().setTitleString(getString(R.string.text_gift_record));
    }

    private boolean isInSubUIView() {
        return mParentILayout != mILayout;
    }

    @Override
    protected RExBaseAdapter<String, GiftRecordBean, String> initRExBaseAdapter() {
        return new GiftRecordAdapter(mActivity);
    }


    @Override
    public void loadMoreData() {
        super.loadMoreData();
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);

        add(RRetrofit.create(GiftService.class).
                giftHistory(Param.buildMap("uid:" + uid,"type:" + type,"page:" + page))
                .compose(Rx.transformer(GiftRecordList.class))
                .subscribe(new BaseSingleSubscriber<GiftRecordList>() {
                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        hideLoadView();
                        if (isError) {
                            showNonetLayout(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    loadData();
                                }
                            });
                        }
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoadView();
                    }

                    @Override
                    public void onSucceed(GiftRecordList beans) {
                        super.onSucceed(beans);
                        if (beans == null || beans.getData_list() == null || beans.getData_list().isEmpty()) {
                            onUILoadDataEnd();
                        } else {
                            onUILoadDataEnd(beans.getData_list());
                        }
                    }
                }));

    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }

    private class GiftRecordAdapter extends RExBaseAdapter<String, GiftRecordBean, String> {

        public GiftRecordAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_gift_record;
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, int posInData, GiftRecordBean dataBean) {
            super.onBindDataView(holder, posInData, dataBean);

            HnGlideImageView iv_item_head = holder.v(R.id.iv_item_head);
            TextView tv_name = holder.v(R.id.tv_name);
            TextView tv_time = holder.v(R.id.tv_time);
            RTextView username = holder.v(R.id.username);
            TextView time_view = holder.v(R.id.time_view);
            HnGlideImageView image_view = holder.v(R.id.image_view);

            iv_item_head.setImageThumbUrl(dataBean.getUser_info().getAvatar());
            image_view.setImageThumbUrl(dataBean.getThumb());
            tv_name.setText(dataBean.getUser_info().getUsername());
            tv_time.setText(TimeUtil.getDatetime(Long.valueOf(dataBean.getCreated()) * 1000));
            username.setText(dataBean.getName());
            time_view.setText(String.format("魅力值: +%s",dataBean.getCharm()));
        }
    }

    static class GiftRecordList extends ListModel<GiftRecordBean>{

    }
}

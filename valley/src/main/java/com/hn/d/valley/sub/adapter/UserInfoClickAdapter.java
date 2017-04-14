package com.hn.d.valley.sub.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.dialog.UIBottomItemDialog;
import com.angcyo.uiview.dialog.UIItemDialog;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.service.UserInfoService;

import rx.subscriptions.CompositeSubscription;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/04/14 11:16
 * 修改人员：Robi
 * 修改时间：2017/04/14 11:16
 * 修改备注：
 * Version: 1.0.0
 */
public class UserInfoClickAdapter extends UserInfoAdapter {
    CompositeSubscription mCompositeSubscription;

    public UserInfoClickAdapter(Context context, ILayout ILayout, CompositeSubscription subscription) {
        super(context, ILayout);
        mCompositeSubscription = subscription;
        setModel(RModelAdapter.MODEL_MULTI);
    }

    @Override
    protected void onBindDataView(RBaseViewHolder holder, final int posInData, final LikeUserInfoBean dataBean) {
        super.onBindDataView(holder, posInData, dataBean);
        //关注
        final ImageView followView = holder.v(R.id.follow_image_view);
        final String to_uid = dataBean.getUid();

        View.OnClickListener clickListener = null;
        String titleString = "";
        if (isContact(dataBean)) {
            titleString = mContext.getString(R.string.unattention_tip2);
        } else {
            if (isAttention(dataBean)) {
                titleString = mContext.getString(R.string.unattention_tip1);
            } else {
                //未关注
                clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setSelectorPosition(posInData);
                        mCompositeSubscription.add(RRetrofit.create(UserInfoService.class)
                                .attention(Param.buildMap("uid:" + UserCache.getUserAccount(), "to_uid:" + to_uid))
                                .compose(Rx.transformer(String.class))
                                .subscribe(new BaseSingleSubscriber<String>() {

                                    @Override
                                    public void onSucceed(String bean) {
                                        //dataBean.setIs_attention(1);
                                        onSetDataBean(dataBean, true);
                                        setSelectorPosition(posInData);
                                    }

                                    @Override
                                    public void onError(int code, String msg) {
                                        super.onError(code, msg);
                                        setSelectorPosition(posInData);
                                    }
                                }));
                    }
                };
            }
        }

        if (isContact(dataBean) || isAttention(dataBean)) {
            final String finalTitleString = titleString;
            clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //取消关注
                    UIBottomItemDialog.build()
                            .setTitleString(finalTitleString)
                            .addItem(new UIItemDialog.ItemInfo(mContext.getResources().getString(R.string.base_ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    setSelectorPosition(posInData);
                                    mCompositeSubscription.add(RRetrofit.create(UserInfoService.class)
                                            .unAttention(Param.buildMap("uid:" + UserCache.getUserAccount(), "to_uid:" + to_uid))
                                            .compose(Rx.transformer(String.class))
                                            .subscribe(new BaseSingleSubscriber<String>() {

                                                @Override
                                                public void onSucceed(String bean) {
                                                    //dataBean.setIs_attention(0);
                                                    onSetDataBean(dataBean, false);
                                                    setSelectorPosition(posInData);
                                                }

                                                @Override
                                                public void onError(int code, String msg) {
                                                    super.onError(code, msg);
                                                    setSelectorPosition(posInData);
                                                }
                                            }));
                                }
                            })).showDialog(mILayout);
                }
            };
        }

        followView.setOnClickListener(clickListener);
    }

    @Override
    protected boolean onUnSelectorPosition(RBaseViewHolder viewHolder, int position, boolean isSelector) {
        if (!isSelector) {
            onBindDataView(viewHolder, position, getAllDatas().get(position));
        }
        return super.onUnSelectorPosition(viewHolder, position, isSelector);
    }
}

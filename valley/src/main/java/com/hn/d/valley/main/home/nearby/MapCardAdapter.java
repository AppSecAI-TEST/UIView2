package com.hn.d.valley.main.home.nearby;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.dialog.UIBottomItemDialog;
import com.angcyo.uiview.dialog.UIItemDialog;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RModelAdapter;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.widget.RTextView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.me.UserDetailUIView;
import com.hn.d.valley.service.UserInfoService;
import com.hn.d.valley.sub.adapter.UserInfoAdapter;

import rx.subscriptions.CompositeSubscription;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/02/27 11:10
 * 修改人员：Robi
 * 修改时间：2017/02/27 11:10
 * 修改备注：
 * Version: 1.0.0
 */
public class MapCardAdapter extends UserInfoAdapter {
    CompositeSubscription mSubscription;

    public MapCardAdapter(Context context, ILayout ILayout, CompositeSubscription subscription) {
        super(context, ILayout);
        mSubscription = subscription;
        setModel(RModelAdapter.MODEL_MULTI);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_map_info_card_layout;
    }

    @Override
    protected void onBindDataView(RBaseViewHolder holder, final int posInData, final LikeUserInfoBean dataBean) {
        super.onBindDataView(holder, posInData, dataBean);

        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.width = ScreenUtil.screenWidth - 3 * mContext.getResources().getDimensionPixelOffset(R.dimen.base_xhdpi);
        holder.itemView.setLayoutParams(layoutParams);

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
                        mSubscription.add(RRetrofit.create(UserInfoService.class)
                                .attention(Param.buildMap("uid:" + UserCache.getUserAccount(), "to_uid:" + to_uid))
                                .compose(Rx.transformer(String.class))
                                .subscribe(new BaseSingleSubscriber<String>() {

                                    @Override
                                    public void onSucceed(String bean) {
                                        dataBean.setIs_attention(1);
                                        //onSetDataBean(dataBean, true);
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
                                    mSubscription.add(RRetrofit.create(UserInfoService.class)
                                            .unAttention(Param.buildMap("uid:" + UserCache.getUserAccount(), "to_uid:" + to_uid))
                                            .compose(Rx.transformer(String.class))
                                            .subscribe(new BaseSingleSubscriber<String>() {

                                                @Override
                                                public void onSucceed(String bean) {
                                                    dataBean.setIs_attention(0);
                                                    //onSetDataBean(dataBean, false);
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

        //时间, 距离
        View controlLayout = holder.v(R.id.time_control_layout);
        TextView timeView = holder.v(R.id.show_time);
        TextView distanceView = holder.v(R.id.show_distance);
        controlLayout.setVisibility(View.VISIBLE);
        timeView.setText(dataBean.getShow_time());
        distanceView.setText(dataBean.getShow_distance());

        TextView oldSignatureView = holder.v(R.id.signature);
        oldSignatureView.setVisibility(View.GONE);

        //认证
        TextView signatureView = holder.v(R.id.signature_view);
        if ("1".equalsIgnoreCase(dataBean.getIs_auth())) {
            holder.v(R.id.auth).setVisibility(View.VISIBLE);
            signatureView.setText(dataBean.getCompany() + dataBean.getJob());
        } else {
            holder.v(R.id.auth).setVisibility(View.GONE);
            String signature = dataBean.getSignature();
            if (TextUtils.isEmpty(signature)) {
                signatureView.setText(R.string.signature_empty_tip);
            } else {
                signatureView.setText(signature);
            }
        }

        //粉丝数
        RTextView textView = holder.v(R.id.follower_num_view);
        textView.setText("1000");

        //语音介绍
        View voiceView = holder.v(R.id.voice_view);
        voiceView.setVisibility(View.VISIBLE);

        //用户个人详情
        holder.itemView.setClickable(false);
        holder.v(R.id.item_root_layout).setClickable(false);
        holder.v(R.id.item_root_layout).setBackgroundColor(Color.TRANSPARENT);

        //用户个人详情
        holder.v(R.id.card_root_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mILayout.startIView(new UserDetailUIView(dataBean.getUid()));
            }
        });
    }


    @Override
    protected boolean onUnSelectorPosition(RBaseViewHolder viewHolder, int position, boolean isSelector) {
        if (!isSelector) {
            onBindDataView(viewHolder, position, getAllDatas().get(position));
        }
        return super.onUnSelectorPosition(viewHolder, position, isSelector);
    }
}

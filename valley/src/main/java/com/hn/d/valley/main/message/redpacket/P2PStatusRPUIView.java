package com.hn.d.valley.main.message.redpacket;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.model.Text;
import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.github.utilcode.utils.SpannableStringUtils;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.service.RedPacketService;
import com.hn.d.valley.main.wallet.MyWalletUIView;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.sub.other.SingleRSubscriber;
import com.hn.d.valley.widget.HnGlideImageView;

import java.util.List;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.hn.d.valley.main.message.redpacket.GrabPacketHelper.parseResult;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：红包发放结果
 * 创建人员：hewking
 * 创建时间：2017/04/24 19:48
 * 修改人员：hewking
 * 修改时间：2017/04/24 19:48
 * 修改备注：
 * Version: 1.0.0
 */
public class P2PStatusRPUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    private long redId;
    private GrabedRDDetail grabedRDDetail;

    private int statuCode = -1;
    private String mSessionId;
    private boolean isReceiver;

    public P2PStatusRPUIView(String sessionId,long redid,boolean isReceiver) {
        this.redId = redid;
        this.mSessionId = sessionId;
        this.isReceiver = isReceiver;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleBarBGColor(ContextCompat.getColor(mActivity, R.color.base_red_d85940))
                .setTitleString(mActivity.getString(R.string.text_klg_rp));
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_redpacket_result;
    }

    @Override
    protected void inflateRecyclerRootLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        baseContentLayout.setBackgroundResource(R.color.base_red_d85940);
        super.inflateRecyclerRootLayout(baseContentLayout, inflater);
        mRecyclerView.setBackgroundResource(R.color.default_base_white);
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);

        add(RRetrofit.create(RedPacketService.class)
                .detail(Param.buildInfoMap("redid:" + redId))
                .compose(Rx.transformRedPacket(GrabedRDDetail.class))
                .flatMap(new Func1<GrabedRDDetail, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(GrabedRDDetail bean) {
                        grabedRDDetail = bean;
                        return RRetrofit.create(RedPacketService.class)
                                .status(Param.buildInfoMap("uid:" + mSessionId, "redid:" + redId))
                                .subscribeOn(Schedulers.io())
                                .map(new Func1<ResponseBody, Integer>() {
                                    @Override
                                    public Integer call(ResponseBody responseBody) {
                                        return parseResult(responseBody);
                                    }
                                }).observeOn(AndroidSchedulers.mainThread());
                    }
                }).subscribe(new BaseSingleSubscriber<Integer>() {
                    @Override
                    public void onSucceed(Integer code) {
                        statuCode = code;
                        if (grabedRDDetail == null || grabedRDDetail.getResult().size() == 0) {
                            showContentLayout();
                        } else {
                            showContentLayout();
                        }

                    }

                }));
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                HnGlideImageView iv_head = holder.v(R.id.iv_icon_head);
                iv_head.setImageUrl(grabedRDDetail.getAvatar());

                TextView tv_username = holder.tv(R.id.tv_username);
                TextView tv_tip = holder.tv(R.id.tv_tip);
                TextView tv_money = holder.tv(R.id.tv_money);
                TextView tv_status = holder.tv(R.id.tv_status);

                tv_username.setText(grabedRDDetail.getUsername() + " 的红包");
                tv_tip.setText(grabedRDDetail.getContent());
                tv_money.setText(SpannableStringUtils.getBuilder(grabedRDDetail.getMoney() / 100f + " ")
                        .append("元").setProportion(0.5f)
                        .create());

                NimUserInfoCache userInfoCache = NimUserInfoCache.getInstance();

                if (Constants.ALREADY_GRAB == statuCode) {
                    if (isReceiver) {
                        tv_status.setText(R.string.text_have_save_klg_wallet);
                        tv_status.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startIView(new MyWalletUIView());
                            }
                        });
                    } else {
                        tv_status.setText(String.format(mActivity.getString(R.string.text_rp_haven_send_you),userInfoCache.getUserDisplayName(mSessionId)));
                    }

                } else if (Constants.CAN_BE_GRAB == statuCode) {
                    tv_status.setText(String.format(mActivity.getString(R.string.tv_send_waite_receive),userInfoCache.getUserDisplayName(mSessionId)));
                } else if (Constants.EXPORE == statuCode) {
                    tv_status.setText(R.string.text_rp_expore);
                } else if (Constants.LOOT_OUT == statuCode) {
                    tv_status.setText(R.string.text_rp_already_grabed);
                }

            }
        }));

    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }
}

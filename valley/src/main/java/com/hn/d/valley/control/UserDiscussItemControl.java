package com.hn.d.valley.control;

import android.view.View;
import android.widget.TextView;

import com.angcyo.library.facebook.DraweeViewUtil;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.T_;
import com.hn.d.valley.base.Transform;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.sub.user.service.UserInfoService;
import com.hn.d.valley.widget.HnItemTextView;

import java.util.List;

import rx.functions.Action0;
import rx.subscriptions.CompositeSubscription;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/07 12:02
 * 修改人员：Robi
 * 修改时间：2017/01/07 12:02
 * 修改备注：
 * Version: 1.0.0
 */
public class UserDiscussItemControl {
    public static void initItem(CompositeSubscription subscription, RBaseViewHolder holder,
                                UserDiscussListBean.DataListBean dataListBean, final Action0 commandAction) {
        initItem(holder, dataListBean);
        bindCommandItemView(subscription, holder, dataListBean, commandAction);
    }

    public static void initItem(RBaseViewHolder holder, UserDiscussListBean.DataListBean dataListBean) {
        holder.fillView(dataListBean, true);
        holder.fillView(dataListBean.getUser_info(), true);

        final TextView mediaCountView = holder.tV(R.id.media_count_view);
        final View mediaControlLayout = holder.v(R.id.media_control_layout);
        final SimpleDraweeView mediaImageType = holder.v(R.id.media_image_view);

        final List<String> medias = Utils.split(dataListBean.getMedia());
        if (medias.isEmpty()) {
            mediaControlLayout.setVisibility(View.GONE);
        } else {
            mediaControlLayout.setVisibility(View.VISIBLE);
            mediaCountView.setText("" + medias.size());
            if ("3".equalsIgnoreCase(dataListBean.getMedia_type())) {
                mediaImageType.setVisibility(View.VISIBLE);
                DraweeViewUtil.setDraweeViewHttp(mediaImageType, medias.get(0));
            } else {
                mediaImageType.setVisibility(View.GONE);
            }
        }

        HnItemTextView fav_cnt = holder.v(R.id.fav_cnt);
        HnItemTextView like_cnt = holder.v(R.id.fav_cnt);

        if (dataListBean.getIs_collect() == 1) {
            //是否收藏
            fav_cnt.setLeftIco(R.drawable.collection_icon_s);
        } else {
            fav_cnt.setLeftIco(R.drawable.collection_icon_n);
        }

        if (dataListBean.getIs_like() == 1) {
            //是否点赞
            like_cnt.setLeftIco(R.drawable.thumb_up_icon_s);
        } else {
            like_cnt.setLeftIco(R.drawable.thumb_up_icon_n);
        }

        TextView commandItemView = holder.v(R.id.command_item_view);
        commandItemView.setVisibility(View.VISIBLE);
        if (dataListBean.getUser_info().getIs_attention() == 1) {
            commandItemView.setText("取消关注");
            commandItemView.setBackgroundResource(R.drawable.base_dark_color_border_selector);
            commandItemView.setTextColor(commandItemView.getResources().
                    getColorStateList(R.color.base_dark_color_border_selector_color));
        } else {
            commandItemView.setText("+关注");
            commandItemView.setBackgroundResource(R.drawable.base_main_color_border_selector);
            commandItemView.setTextColor(commandItemView.getResources().
                    getColorStateList(R.color.base_main_color_border_selector_color));
        }
    }

    /**
     * 关注和取消关注
     */
    private static void bindCommandItemView(final CompositeSubscription subscription,
                                            RBaseViewHolder holder,
                                            UserDiscussListBean.DataListBean tBean,
                                            final Action0 commandAction) {

        final String uid = UserCache.getUserAccount();
        final String to_uid = tBean.getUser_info().getUid();

        TextView commandItemView = holder.v(R.id.command_item_view);
        commandItemView.setVisibility(View.VISIBLE);


        if (tBean.getUser_info().getIs_attention() == 1) {
            commandItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subscription.add(RRetrofit.create(UserInfoService.class)
                            .unAttention(Param.buildMap("uid:" + uid, "to_uid:" + to_uid))
                            .compose(Transform.defaultStringSchedulers(String.class))
                            .subscribe(new BaseSingleSubscriber<String>() {

                                @Override
                                public void onNext(String bean) {
                                    T_.show(bean);
                                    commandAction.call();
                                }
                            }));
                }
            });
        } else {
            commandItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subscription.add(RRetrofit.create(UserInfoService.class)
                            .attention(Param.buildMap("uid:" + uid, "to_uid:" + to_uid))
                            .compose(Transform.defaultStringSchedulers(String.class))
                            .subscribe(new BaseSingleSubscriber<String>() {

                                @Override
                                public void onNext(String bean) {
                                    T_.show(bean);
                                    commandAction.call();
                                }
                            }));
                }
            });
        }
    }
}

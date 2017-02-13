package com.hn.d.valley.sub.user;

import android.view.View;

import com.angcyo.uiview.github.goodview.GoodView;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.CommentListBean;
import com.hn.d.valley.service.SocialService;
import com.hn.d.valley.widget.HnItemTextView;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/14 14:36
 * 修改人员：Robi
 * 修改时间：2017/01/14 14:36
 * 修改备注：
 * Version: 1.0.0
 */
public class DynamicCommentControl {

    /**
     * 点赞
     */
    private static void initLikeView(final HnItemTextView itemTextView,
                                     final CommentListBean.DataListBean tBean,
                                     final CompositeSubscription subscription, final Action1<Boolean> likeAction) {

        itemTextView.setLeftIco(R.drawable.thumb_up_icon_s);
        itemTextView.setText(tBean.getLike_cnt());

        itemTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (likeAction != null) {
                    //取消点赞
                    likeAction.call(false);
                }

                tBean.setIs_like(0);
                tBean.setLike_cnt(String.valueOf(Integer.valueOf(tBean.getLike_cnt()) - 1));
                initUnLikeView(itemTextView, tBean, subscription, likeAction);

                subscription.add(RRetrofit.create(SocialService.class)
                        .dislike(Param.buildMap("type:comment", "item_id:" + tBean.getComment_id()))
                        .compose(Rx.transformer(String.class))
                        .subscribe(new BaseSingleSubscriber<String>() {

                            @Override
                            public void onSucceed(String bean) {
                                //T_.show(bean);

                            }
                        }));
            }
        });
    }

    /**
     * 取消点赞
     */
    private static void initUnLikeView(final HnItemTextView itemTextView,
                                       final CommentListBean.DataListBean tBean,
                                       final CompositeSubscription subscription, final Action1<Boolean> likeAction) {

        itemTextView.setLeftIco(R.drawable.thumb_up_icon_n);
        itemTextView.setText(tBean.getLike_cnt());

        itemTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (likeAction != null) {
                    //点赞
                    likeAction.call(true);
                }

                GoodView.build(itemTextView);
                tBean.setIs_like(1);
                tBean.setLike_cnt(String.valueOf(Integer.valueOf(tBean.getLike_cnt()) + 1));
                initLikeView(itemTextView, tBean, subscription, likeAction);

                subscription.add(RRetrofit.create(SocialService.class)
                        .like(Param.buildMap("type:comment", "item_id:" + tBean.getComment_id()))
                        .compose(Rx.transformer(String.class))
                        .subscribe(new BaseSingleSubscriber<String>() {

                            @Override
                            public void onSucceed(String bean) {
                                //T_.show(bean);
                            }
                        }));
            }
        });
    }

    /**
     * 点赞和取消点赞
     */
    public static void bindLikeItemView(final CompositeSubscription subscription,
                                        RBaseViewHolder holder,
                                        CommentListBean.DataListBean tBean, Action1<Boolean> likeAction) {

        HnItemTextView like_cnt = holder.v(R.id.like_cnt);

        if (tBean.getIs_like() == 1) {
            //是否点赞
            initLikeView(like_cnt, tBean, subscription, likeAction);
        } else {
            initUnLikeView(like_cnt, tBean, subscription, likeAction);
        }
    }
}

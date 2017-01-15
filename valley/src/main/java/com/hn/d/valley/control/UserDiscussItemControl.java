package com.hn.d.valley.control;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.angcyo.library.facebook.DraweeViewUtil;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.github.goodview.GoodView;
import com.angcyo.uiview.github.utilcode.utils.SpannableStringUtils;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.T_;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.iview.ImagePagerUIView;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.sub.user.PublishDynamicUIView;
import com.hn.d.valley.sub.user.service.SocialService;
import com.hn.d.valley.sub.user.service.UserInfoService;
import com.hn.d.valley.utils.PhotoPager;
import com.hn.d.valley.widget.HnGenderView;
import com.hn.d.valley.widget.HnItemTextView;
import com.jakewharton.rxbinding.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.functions.Action0;
import rx.functions.Action1;
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
                                UserDiscussListBean.DataListBean dataListBean,
                                final Action0 commandAction, final Action0 itemRootAction,
                                final ILayout iLayout) {
        initItem(holder, dataListBean, itemRootAction, iLayout);
        bindAttentionItemView(subscription, holder, dataListBean, commandAction);
        bindFavItemView(subscription, holder, dataListBean);
        bindLikeItemView(subscription, holder, dataListBean, null);
    }

    public static void initItem(RBaseViewHolder holder, final UserDiscussListBean.DataListBean dataListBean,
                                final Action0 itemRootAction, final ILayout iLayout) {
        LikeUserInfoBean user_info = dataListBean.getUser_info();

        holder.fillView(dataListBean, true);
        holder.fillView(user_info, true);

        final TextView mediaCountView = holder.tV(R.id.media_count_view);//媒体数量
        final View mediaControlLayout = holder.v(R.id.media_control_layout);
        final SimpleDraweeView mediaImageType = holder.v(R.id.media_image_view);//
        final HnGenderView genderView = holder.v(R.id.grade);//性别,等级
        if (user_info != null) {
            genderView.setVisibility(View.VISIBLE);
            genderView.setGender(user_info.getSex(), user_info.getGrade());
        } else {
            genderView.setVisibility(View.GONE);
        }

        final List<String> medias = RUtils.split(dataListBean.getMedia());
        if (medias.isEmpty()) {
            mediaControlLayout.setVisibility(View.GONE);
        } else {
            mediaControlLayout.setVisibility(View.VISIBLE);
            mediaCountView.setText("" + medias.size());
            if ("3".equalsIgnoreCase(dataListBean.getMedia_type())) {
                mediaImageType.setVisibility(View.VISIBLE);
                Object tag = mediaImageType.getTag();
                if (tag == null || !tag.toString().equalsIgnoreCase(medias.get(0))) {
                    mediaImageType.setTag(medias.get(0));
                    DraweeViewUtil.resize(mediaImageType, medias.get(0));
                }

                //点击预览全部图片
                final List<String> previewMedia = RUtils.split(dataListBean.getMedia());
                mediaImageType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImagePagerUIView.start(iLayout, v, PhotoPager.getImageItems(medias), 0);
                    }
                });
            } else if ("2".equalsIgnoreCase(dataListBean.getMedia_type())) {
                mediaImageType.setVisibility(View.VISIBLE);
                DraweeViewUtil.setDraweeViewRes(mediaImageType, R.drawable.video_release);
                mediaImageType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        T_.info("暂不支持视频的播放.");
                    }
                });
            } else {
                mediaImageType.setVisibility(View.GONE);
            }
        }

        HnItemTextView fav_cnt = holder.v(R.id.fav_cnt);
        HnItemTextView like_cnt = holder.v(R.id.like_cnt);

        if (dataListBean.getIs_collection() == 1) {
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
        final View forwardView = holder.v(R.id.forward_cnt);
        if (user_info != null) {
            if (user_info.getIs_attention() == 1) {
                commandItemView.setText("取消关注");
                commandItemView.setBackgroundResource(R.drawable.base_dark_color_border_fill_selector);
                commandItemView.setTextColor(commandItemView.getResources().
                        getColorStateList(R.color.base_dark_color_border_selector_color));
            } else {
                commandItemView.setText("+关注");
                commandItemView.setBackgroundResource(R.drawable.base_main_color_border_fill_selector);
                commandItemView.setTextColor(commandItemView.getResources().
                        getColorStateList(R.color.base_main_color_border_selector_color));
            }

            if (UserCache.getUserAccount().equalsIgnoreCase(user_info.getUid())) {
                //自己的动态不允许转发
                forwardView.setClickable(false);
            } else {
                forwardView.setClickable(true);
                forwardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iLayout.startIView(new PublishDynamicUIView(dataListBean));
                    }
                });
            }
        } else {
            commandItemView.setVisibility(View.GONE);
            forwardView.setClickable(false);
        }

        TextView infoView = holder.v(R.id.copy_info_view);
        infoView.setVisibility(View.GONE);
        UserDiscussListBean.DataListBean.OriginalInfo originalInfo = dataListBean.getOriginal_info();
        if (!"0".equalsIgnoreCase(dataListBean.getShare_original_item_id()) && originalInfo != null) {
            infoView.setVisibility(View.VISIBLE);
            String content = originalInfo.getContent();
            SpannableStringUtils.Builder builder = SpannableStringUtils.getBuilder(originalInfo.getUsername() + ": ")
                    .setForegroundColor(infoView.getResources().getColor(R.color.colorAccent));
            SpannableStringBuilder stringBuilder;
            if (TextUtils.isEmpty(content)) {
                stringBuilder = builder.create();
            } else {
                stringBuilder = builder.append(content)
                        .setForegroundColor(infoView.getResources().getColor(R.color.main_text_color_dark))
                        .create();
            }
            infoView.setText(stringBuilder);
        }

        if (itemRootAction != null) {
            holder.v(R.id.item_root_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemRootAction.call();
                }
            });

            holder.v(R.id.comment_cnt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemRootAction.call();
                }
            });
        } else {
            holder.v(R.id.item_root_layout).setClickable(false);
            holder.v(R.id.comment_cnt).setClickable(false);//不允许评价
        }

    }

    /**
     * 关注和取消关注
     */
    private static void bindAttentionItemView(final CompositeSubscription subscription,
                                              RBaseViewHolder holder,
                                              UserDiscussListBean.DataListBean tBean,
                                              final Action0 commandAction) {

        final String uid = UserCache.getUserAccount();
        final String to_uid = tBean.getUser_info().getUid();

        TextView commandItemView = holder.v(R.id.command_item_view);
        commandItemView.setVisibility(TextUtils.equals(uid, to_uid) ? View.GONE : View.VISIBLE);

        if (tBean.getUser_info().getIs_attention() == 1) {
            RxView.clicks(commandItemView)
                    .debounce(Constant.DEBOUNCE_TIME_300, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            subscription.add(RRetrofit.create(UserInfoService.class)
                                    .unAttention(Param.buildMap("uid:" + uid, "to_uid:" + to_uid))
                                    .compose(Rx.transformer(String.class))
                                    .subscribe(new BaseSingleSubscriber<String>() {

                                        @Override
                                        public void onNext(String bean) {
                                            //T_.show(bean);
                                            if (commandAction != null) {
                                                commandAction.call();
                                            }
                                        }
                                    }));
                        }
                    });
//            commandItemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    subscription.add(RRetrofit.create(UserInfoService.class)
//                            .unAttention(Param.buildMap("uid:" + uid, "to_uid:" + to_uid))
//                            .compose(Rx.transformer(String.class))
//                            .subscribe(new BaseSingleSubscriber<String>() {
//
//                                @Override
//                                public void onNext(String bean) {
//                                    //T_.show(bean);
//                                    commandAction.call();
//                                }
//                            }));
//                }
//            });
        } else {
            RxView.clicks(commandItemView)
                    .debounce(Constant.DEBOUNCE_TIME_300, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            subscription.add(RRetrofit.create(UserInfoService.class)
                                    .attention(Param.buildMap("uid:" + uid, "to_uid:" + to_uid))
                                    .compose(Rx.transformer(String.class))
                                    .subscribe(new BaseSingleSubscriber<String>() {

                                        @Override
                                        public void onNext(String bean) {
                                            //T_.show(bean);
                                            if (commandAction != null) {
                                                commandAction.call();
                                            }
                                        }
                                    }));
                        }
                    });

//            commandItemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    subscription.add(RRetrofit.create(UserInfoService.class)
//                            .attention(Param.buildMap("uid:" + uid, "to_uid:" + to_uid))
//                            .compose(Rx.transformer(String.class))
//                            .subscribe(new BaseSingleSubscriber<String>() {
//
//                                @Override
//                                public void onNext(String bean) {
//                                    //T_.show(bean);
//                                    commandAction.call();
//                                }
//                            }));
//                }
//            });
        }
    }


    /**
     * 收藏
     */
    private static void initFavView(final HnItemTextView itemTextView,
                                    final UserDiscussListBean.DataListBean tBean,
                                    final CompositeSubscription subscription) {
        final String uid = UserCache.getUserAccount();

        itemTextView.setLeftIco(R.drawable.collection_icon_s);
        itemTextView.setText(tBean.getFav_cnt());

//        RxView.clicks(itemTextView)
//                .debounce(Constant.DEBOUNCE_TIME_300, TimeUnit.MILLISECONDS)
//                .subscribe(new Action1<Void>() {
//                    @Override
//                    public void call(Void aVoid) {
//                        subscription.add(RRetrofit.create(SocialService.class)
//                                .unCollect(Param.buildMap("uid:" + uid, "type:discuss", "item_id:" + tBean.getDiscuss_id()))
//                                .compose(Rx.transformer(String.class))
//                                .subscribe(new BaseSingleSubscriber<String>() {
//
//                                    @Override
//                                    public void onNext(String bean) {
//                                        //T_.show(bean);
//                                        tBean.setIs_collection(0);
//                                        tBean.setFav_cnt(String.valueOf(Integer.valueOf(tBean.getFav_cnt()) - 1));
//                                        initUnFavView(itemTextView, tBean, subscription);
//                                    }
//                                }));
//                    }
//                });

        itemTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tBean.setIs_collection(0);
                tBean.setFav_cnt(String.valueOf(Integer.valueOf(tBean.getFav_cnt()) - 1));
                initUnFavView(itemTextView, tBean, subscription);

                subscription.add(RRetrofit.create(SocialService.class)
                        .unCollect(Param.buildMap("uid:" + uid, "type:discuss", "item_id:" + tBean.getDiscuss_id()))
                        .compose(Rx.transformer(String.class))
                        .subscribe(new BaseSingleSubscriber<String>() {

                            @Override
                            public void onNext(String bean) {
                                //T_.show(bean);
                            }
                        }));
            }
        });
    }

    /**
     * 取消收藏
     */
    private static void initUnFavView(final HnItemTextView itemTextView,
                                      final UserDiscussListBean.DataListBean tBean,
                                      final CompositeSubscription subscription) {
        final String uid = UserCache.getUserAccount();

        itemTextView.setLeftIco(R.drawable.collection_icon_n);
        itemTextView.setText(tBean.getFav_cnt());

//        RxView.clicks(itemTextView)
//                .debounce(Constant.DEBOUNCE_TIME_300, TimeUnit.MILLISECONDS)
//                .subscribe(new Action1<Void>() {
//                    @Override
//                    public void call(Void aVoid) {
//                        subscription.add(RRetrofit.create(SocialService.class)
//                                .collect(Param.buildMap("uid:" + uid, "type:discuss", "item_id:" + tBean.getDiscuss_id()))
//                                .compose(Rx.transformer(String.class))
//                                .subscribe(new BaseSingleSubscriber<String>() {
//
//                                    @Override
//                                    public void onNext(String bean) {
//                                        //T_.show(bean);
//                                        GoodView.build(itemTextView);
//                                        tBean.setIs_collection(1);
//                                        tBean.setFav_cnt(String.valueOf(Integer.valueOf(tBean.getFav_cnt()) + 1));
//                                        initFavView(itemTextView, tBean, subscription);
//                                    }
//                                }));
//                    }
//                });

        itemTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoodView.build(itemTextView);
                tBean.setIs_collection(1);
                tBean.setFav_cnt(String.valueOf(Integer.valueOf(tBean.getFav_cnt()) + 1));
                initFavView(itemTextView, tBean, subscription);

                subscription.add(RRetrofit.create(SocialService.class)
                        .collect(Param.buildMap("uid:" + uid, "type:discuss", "item_id:" + tBean.getDiscuss_id()))
                        .compose(Rx.transformer(String.class))
                        .subscribe(new BaseSingleSubscriber<String>() {

                            @Override
                            public void onNext(String bean) {
                                //T_.show(bean);
//                                GoodView.build(itemTextView);
//                                tBean.setIs_collection(1);
//                                tBean.setFav_cnt(String.valueOf(Integer.valueOf(tBean.getFav_cnt()) + 1));
//                                initFavView(itemTextView, tBean, subscription);
                            }
                        }));
            }
        });
    }

    /**
     * 收藏和取消收藏
     */
    private static void bindFavItemView(final CompositeSubscription subscription,
                                        RBaseViewHolder holder,
                                        UserDiscussListBean.DataListBean tBean) {

        HnItemTextView fav_cnt = holder.v(R.id.fav_cnt);

        if (tBean.getIs_collection() == 1) {
            //是否收藏
            initFavView(fav_cnt, tBean, subscription);
        } else {
            initUnFavView(fav_cnt, tBean, subscription);
        }
    }

    /**
     * 点赞
     */
    private static void initLikeView(final HnItemTextView itemTextView,
                                     final UserDiscussListBean.DataListBean tBean,
                                     final CompositeSubscription subscription, final Action1<Boolean> likeAction) {
        final String uid = UserCache.getUserAccount();

        itemTextView.setLeftIco(R.drawable.thumb_up_icon_s);
        itemTextView.setText(tBean.getLike_cnt());

//        RxView.clicks(itemTextView)
//                .debounce(Constant.DEBOUNCE_TIME_300, TimeUnit.MILLISECONDS)
//                .subscribe(new Action1<Void>() {
//                    @Override
//                    public void call(Void aVoid) {
//                        subscription.add(RRetrofit.create(SocialService.class)
//                                .dislike(Param.buildMap("uid:" + uid, "type:discuss", "item_id:" + tBean.getDiscuss_id()))
//                                .compose(Rx.transformer(String.class))
//                                .subscribe(new BaseSingleSubscriber<String>() {
//
//                                    @Override
//                                    public void onNext(String bean) {
//                                        //T_.show(bean);
//                                        tBean.setIs_like(0);
//                                        tBean.setLike_cnt(String.valueOf(Integer.valueOf(tBean.getLike_cnt()) - 1));
//                                        initUnLikeView(itemTextView, tBean, subscription);
//                                    }
//                                }));
//                    }
//                });

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
                        .dislike(Param.buildMap("uid:" + uid, "type:discuss", "item_id:" + tBean.getDiscuss_id()))
                        .compose(Rx.transformer(String.class))
                        .subscribe(new BaseSingleSubscriber<String>() {

                            @Override
                            public void onNext(String bean) {
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
                                       final UserDiscussListBean.DataListBean tBean,
                                       final CompositeSubscription subscription, final Action1<Boolean> likeAction) {
        final String uid = UserCache.getUserAccount();

        itemTextView.setLeftIco(R.drawable.thumb_up_icon_n);
        itemTextView.setText(tBean.getLike_cnt());

//        RxView.clicks(itemTextView)
//                .debounce(Constant.DEBOUNCE_TIME_300, TimeUnit.MILLISECONDS)
//                .subscribe(new Action1<Void>() {
//                    @Override
//                    public void call(Void aVoid) {
//                        subscription.add(RRetrofit.create(SocialService.class)
//                                .like(Param.buildMap("uid:" + uid, "type:discuss", "item_id:" + tBean.getDiscuss_id()))
//                                .compose(Rx.transformer(String.class))
//                                .subscribe(new BaseSingleSubscriber<String>() {
//
//                                    @Override
//                                    public void onNext(String bean) {
//                                        //T_.show(bean);
//                                        GoodView.build(itemTextView);
//                                        tBean.setIs_like(1);
//                                        tBean.setLike_cnt(String.valueOf(Integer.valueOf(tBean.getLike_cnt()) + 1));
//                                        initLikeView(itemTextView, tBean, subscription);
//                                    }
//                                }));
//                    }
//                });

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
                        .like(Param.buildMap("uid:" + uid, "type:discuss", "item_id:" + tBean.getDiscuss_id()))
                        .compose(Rx.transformer(String.class))
                        .subscribe(new BaseSingleSubscriber<String>() {

                            @Override
                            public void onNext(String bean) {
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
                                        UserDiscussListBean.DataListBean tBean, Action1<Boolean> likeAction) {

        HnItemTextView like_cnt = holder.v(R.id.like_cnt);

        if (tBean.getIs_like() == 1) {
            //是否点赞
            initLikeView(like_cnt, tBean, subscription, likeAction);
        } else {
            initUnLikeView(like_cnt, tBean, subscription, likeAction);
        }
    }
}

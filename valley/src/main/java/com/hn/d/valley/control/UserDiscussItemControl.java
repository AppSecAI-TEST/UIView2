package com.hn.d.valley.control;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.angcyo.library.glide.NineImageLayout;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.github.goodview.GoodView;
import com.angcyo.uiview.github.utilcode.utils.SpannableStringUtils;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.T_;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.iview.ImagePagerUIView;
import com.hn.d.valley.base.oss.OssHelper;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.me.UserDetailUIView;
import com.hn.d.valley.sub.user.PublishDynamicUIView;
import com.hn.d.valley.sub.user.service.SocialService;
import com.hn.d.valley.sub.user.service.UserInfoService;
import com.hn.d.valley.utils.PhotoPager;
import com.hn.d.valley.widget.HnGenderView;
import com.hn.d.valley.widget.HnItemTextView;
import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
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

        /**头像*/
        final View avatarView = holder.v(R.id.avatar);
        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iLayout != null) {
                    iLayout.startIView(new UserDetailUIView(dataListBean.getUid()));
                }
            }
        });


        final TextView mediaCountView = holder.tV(R.id.media_count_view);//媒体数量
        final View mediaControlLayout = holder.v(R.id.media_control_layout);
//        final SimpleDraweeView mediaImageTypeView = holder.v(R.id.media_image_view);//
        final NineImageLayout mediaImageTypeView = holder.v(R.id.media_image_view);//
        final HnGenderView genderView = holder.v(R.id.grade);//性别,等级
        if (user_info != null) {
            genderView.setVisibility(View.VISIBLE);
            genderView.setGender(user_info.getSex(), user_info.getGrade());
        } else {
            genderView.setVisibility(View.GONE);
        }

        //图片视频处理
        updateMediaLayout(dataListBean, iLayout, mediaCountView, mediaControlLayout, mediaImageTypeView);

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

    private static void updateMediaLayout(UserDiscussListBean.DataListBean dataListBean, final ILayout iLayout,
                                          TextView mediaCountView, View mediaControlLayout, NineImageLayout mediaImageTypeView) {
        final List<String> medias = RUtils.split(dataListBean.getMedia());
        if (medias.isEmpty()) {
            mediaControlLayout.setVisibility(View.GONE);
        } else {
            mediaControlLayout.setVisibility(View.VISIBLE);
            mediaCountView.setText("" + medias.size());
            if ("3".equalsIgnoreCase(dataListBean.getMedia_type())) {
                //图片类型
                mediaImageTypeView.setVisibility(View.VISIBLE);
                Object tag = mediaImageTypeView.getTag();

                final String url = medias.get(0);
                if (tag == null || !tag.toString().equalsIgnoreCase(url)) {
                    mediaImageTypeView.setTag(url);

                    //DraweeViewUtil.resize(mediaImageTypeView, medias.get(0));
                    //OssHelper.setViewSize(mediaControlLayout, url);
                    //DraweeViewUtil.setDraweeViewHttp(mediaImageTypeView, OssHelper.getImageThumb(url));
                    mediaImageTypeView.setNineImageConfig(new NineImageLayout.NineImageConfig() {
                        @Override
                        public int[] getWidthHeight(int imageSize) {
                            return OssHelper.getImageThumbSize(url);
                        }

                        @Override
                        public void displayImage(final ImageView imageView, String url, int width, int height) {
                            Glide.with(imageView.getContext())
                                    .load(OssHelper.getImageThumb(url, width, height))
                                    .asBitmap()
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                            if (imageView == null) {
                                                return;
                                            }
                                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                            imageView.setImageBitmap(resource);
                                        }

                                        @Override
                                        public void onLoadStarted(Drawable placeholder) {
                                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                            imageView.setImageResource(R.drawable.zhanweitu_1);
                                        }
                                    });
                        }

                        @Override
                        public void onImageItemClick(ImageView imageView, List<String> urlList, List<ImageView> imageList, int index) {
                            //点击预览全部图片
                            ImagePagerUIView.start(iLayout, imageView,
                                    PhotoPager.getImageItems(medias, imageList), index);
                        }
                    });
                    mediaImageTypeView.setImagesList(medias);
                }
            } else if ("2".equalsIgnoreCase(dataListBean.getMedia_type())) {
                //视频类型
                mediaImageTypeView.setVisibility(View.VISIBLE);
                //DraweeViewUtil.setDraweeViewRes(mediaImageTypeView, R.drawable.video_release);

                List<String> thumbUrl = new ArrayList<>();
                thumbUrl.add("");
                mediaImageTypeView.setNineImageConfig(new NineImageLayout.NineImageConfig() {
                    @Override
                    public int[] getWidthHeight(int imageSize) {
                        return new int[]{-1, -1};
                    }

                    @Override
                    public void displayImage(ImageView imageView, String url, int width, int height) {
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        imageView.setImageResource(R.drawable.video_release);
                    }

                    @Override
                    public void onImageItemClick(ImageView imageView, List<String> urlList, List<ImageView> imageList, int index) {
                        T_.info("暂不支持视频的播放.");
                    }
                });
                mediaImageTypeView.setImagesList(thumbUrl);
            } else {
                mediaImageTypeView.setVisibility(View.GONE);
            }
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
        fav_cnt.setVisibility(View.GONE);//星期五 2017-2-10 不显示

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

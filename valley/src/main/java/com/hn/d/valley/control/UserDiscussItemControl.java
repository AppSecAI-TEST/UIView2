package com.hn.d.valley.control;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.dialog.UIItemDialog;
import com.angcyo.uiview.github.goodview.GoodView;
import com.angcyo.uiview.github.utilcode.utils.SpannableStringUtils;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.RImageView;
import com.angcyo.uiview.widget.RNineImageLayout;
import com.bumptech.glide.GifRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hn.d.valley.R;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.iview.ImagePagerUIView;
import com.hn.d.valley.base.iview.VideoPlayUIView;
import com.hn.d.valley.base.oss.OssHelper;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.me.UserDetailUIView;
import com.hn.d.valley.service.ContactService;
import com.hn.d.valley.service.DiscussService;
import com.hn.d.valley.service.SettingService;
import com.hn.d.valley.service.SocialService;
import com.hn.d.valley.service.UserInfoService;
import com.hn.d.valley.sub.user.PublishDynamicUIView;
import com.hn.d.valley.sub.user.ReportUIView;
import com.hn.d.valley.utils.PhotoPager;
import com.hn.d.valley.widget.HnGenderView;
import com.hn.d.valley.widget.HnItemTextView;
import com.jakewharton.rxbinding.view.RxView;
import com.lzy.imagepicker.ImageUtils;

import java.io.File;
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
                                final Action1<UserDiscussListBean.DataListBean> commandAction, final Action0 itemRootAction,
                                final ILayout iLayout) {
        initItem(holder, dataListBean, itemRootAction, iLayout);
//        bindAttentionItemView(subscription, holder, dataListBean, commandAction);
        bindAttentionItemView2(subscription, holder, dataListBean, commandAction, iLayout);
        bindFavItemView(subscription, holder, dataListBean);
        bindLikeItemView(subscription, holder, dataListBean, null);
    }

    public static void initItem(final RBaseViewHolder holder, final UserDiscussListBean.DataListBean dataListBean,
                                final Action0 itemRootAction, final ILayout iLayout) {
        LikeUserInfoBean user_info = dataListBean.getUser_info();

        holder.fillView(dataListBean, true);
        holder.fillView(user_info, true);

        TextView showTimeView = holder.v(R.id.show_time);
        showTimeView.setVisibility(View.VISIBLE);
        showTimeView.setText(dataListBean.getShow_time());

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

        final HnGenderView genderView = holder.v(R.id.grade);//性别,等级
        if (user_info != null) {
            genderView.setVisibility(View.VISIBLE);
            genderView.setGender(user_info.getSex(), user_info.getGrade());
        } else {
            genderView.setVisibility(View.GONE);
        }

        //图片视频处理
        updateMediaLayout(dataListBean, iLayout, holder);

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

        View commandItemView = holder.v(R.id.command_item_view);
        commandItemView.setVisibility(View.VISIBLE);
        final View forwardView = holder.v(R.id.forward_cnt);
        if (user_info != null) {
//            if (user_info.getIs_attention() == 1) {
//                commandItemView.setText("取消关注");
//                commandItemView.setBackgroundResource(R.drawable.base_dark_color_border_fill_selector);
//                commandItemView.setTextColor(commandItemView.getResources().
//                        getColorStateList(R.color.base_dark_color_border_selector_color));
//            } else {
//                commandItemView.setText("+关注");
//                commandItemView.setBackgroundResource(R.drawable.base_main_color_border_fill_selector);
//                commandItemView.setTextColor(commandItemView.getResources().
//                        getColorStateList(R.color.base_main_color_border_selector_color));
//            }
//
            if (UserCache.getUserAccount().equalsIgnoreCase(user_info.getUid())) {
                //自己的动态不允许转发
                forwardView.setClickable(false);
                forwardView.setEnabled(false);
            } else {
                forwardView.setEnabled(true);
                forwardView.setClickable(true);
                forwardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(dataListBean.uuid)) {
                            iLayout.startIView(new PublishDynamicUIView(dataListBean));
                        } else {
                            T_.show(holder.itemView.getResources().getString(R.string.publishing_tip));
                        }
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

        View rootLayout = holder.v(R.id.item_root_layout);
        if (itemRootAction != null) {
            if (TextUtils.isEmpty(dataListBean.uuid)) {
                rootLayout.setBackgroundResource(R.drawable.base_white_bg_selector);
            } else {
                rootLayout.setBackgroundColor(rootLayout.getContext().getResources().getColor(R.color.theme_color_primary_dark_tran2));
            }

            rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(dataListBean.uuid)) {
                        itemRootAction.call();
                    } else {
                        T_.show(holder.itemView.getResources().getString(R.string.publishing_tip));
                    }
                }
            });

            holder.v(R.id.comment_cnt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(dataListBean.uuid)) {
                        itemRootAction.call();
                    } else {
                        T_.show(holder.itemView.getResources().getString(R.string.publishing_tip));
                    }
                }
            });
        } else {
            rootLayout.setClickable(false);
            holder.v(R.id.comment_cnt).setClickable(false);//不允许评价
        }

    }

    /**
     * 动态是否置顶
     */
    private static void showTopView(RBaseViewHolder holder, UserDiscussListBean.DataListBean dataListBean) {
        holder.v(R.id.top_image_view).setVisibility("1".equalsIgnoreCase(dataListBean.getIs_top()) ? View.VISIBLE : View.GONE);
    }

    private static void updateMediaLayout(UserDiscussListBean.DataListBean dataListBean, final ILayout iLayout, RBaseViewHolder holder) {
        final TextView mediaCountView = holder.tV(R.id.media_count_view);//媒体数量
        final View mediaControlLayout = holder.v(R.id.media_control_layout);
//        final SimpleDraweeView mediaImageTypeView = holder.v(R.id.media_image_view);//
        final RNineImageLayout mediaImageTypeView = holder.v(R.id.media_image_view);//
        final View videoPlayView = holder.v(R.id.video_play_view);
        final TextView videoTimeView = holder.v(R.id.video_time_view);

        final List<String> medias = RUtils.split(dataListBean.getMedia());
        if (medias.isEmpty()) {
            mediaControlLayout.setVisibility(View.GONE);
        } else {
            mediaControlLayout.setVisibility(View.VISIBLE);
            mediaCountView.setText("" + medias.size());
            final String url = medias.get(0);

            if ("3".equalsIgnoreCase(dataListBean.getMedia_type())) {
                //图片类型
                mediaImageTypeView.setVisibility(View.VISIBLE);
                videoTimeView.setVisibility(View.INVISIBLE);
                videoPlayView.setVisibility(View.INVISIBLE);
//                Object tag = mediaImageTypeView.getTag();

//                if (tag == null || !tag.toString().equalsIgnoreCase(url)) {
//                    mediaImageTypeView.setTag(url);

                //DraweeViewUtil.resize(mediaImageTypeView, medias.get(0));
                //OssHelper.setViewSize(mediaControlLayout, url);
                //DraweeViewUtil.setDraweeViewHttp(mediaImageTypeView, OssHelper.getImageThumb(url));
                mediaImageTypeView.setNineImageConfig(new RNineImageLayout.NineImageConfig() {
                    @Override
                    public int[] getWidthHeight(int imageSize) {
                        return OssHelper.getImageThumbSize2(url);
                    }

                    @Override
                    public void displayImage(final ImageView imageView, String url, int width, int height) {
                        UserDiscussItemControl.displayImage(imageView, url, width, height);
                    }

                    @Override
                    public void onImageItemClick(ImageView imageView, List<String> urlList, List<RImageView> imageList, int index) {
                        //点击预览全部图片
                        ImagePagerUIView.start(iLayout, imageView,
                                PhotoPager.getImageItems(medias, imageList), index);
                    }
                });
                mediaImageTypeView.setImagesList(medias);
//                }
            } else if ("2".equalsIgnoreCase(dataListBean.getMedia_type())) {
                //视频类型
                mediaImageTypeView.setVisibility(View.VISIBLE);
                videoTimeView.setVisibility(View.VISIBLE);
                videoPlayView.setVisibility(View.VISIBLE);
                //DraweeViewUtil.setDraweeViewRes(mediaImageTypeView, R.drawable.video_release);

                String[] split = url.split("\\?");
                final String thumbUrl = split[0];
                final String videoUrl = split[1];

                try {
                    videoTimeView.setText(getVideoTime(videoUrl));
                } catch (Exception e) {
                    videoTimeView.setTextColor(Color.RED);
                    videoTimeView.setText("video time format error");
                }

                mediaImageTypeView.setNineImageConfig(new RNineImageLayout.NineImageConfig() {
                    @Override
                    public int[] getWidthHeight(int imageSize) {
                        return OssHelper.getImageThumbSize2(thumbUrl);
                    }

                    @Override
                    public void displayImage(ImageView imageView, String url, int width, int height) {
                        UserDiscussItemControl.displayImage(imageView, url, width, height);
                    }

                    @Override
                    public void onImageItemClick(ImageView imageView, List<String> urlList, List<RImageView> imageList, int index) {
                        //T_.info(videoUrl);
                        iLayout.startIView(new VideoPlayUIView(videoUrl, imageView.getDrawable(), OssHelper.getWidthHeightWithUrl(thumbUrl)));
                    }
                });
                mediaImageTypeView.setImage(thumbUrl);
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
                                        public void onSucceed(String bean) {
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
                                        public void onSucceed(String bean) {
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
     * 更多按钮
     */
    private static void bindAttentionItemView2(final CompositeSubscription subscription, final RBaseViewHolder holder,
                                               final UserDiscussListBean.DataListBean tBean,
                                               final Action1<UserDiscussListBean.DataListBean> commandAction,
                                               final ILayout iLayout) {
        //是否置顶
        showTopView(holder, tBean);

        View commandItemView = holder.v(R.id.command_item_view);
        final String uid = UserCache.getUserAccount();
        final String to_uid = tBean.getUser_info().getUid();
        final View.OnClickListener listener;

        final UIItemDialog.ItemInfo topItem, favItem, deleteItem, followItem, notSeeItem, reportItem;

        String topItemString;
        View.OnClickListener topItemClickListener;
        if ("1".equalsIgnoreCase(tBean.getIs_top())) {
            topItemString = ValleyApp.getApp().getString(R.string.cancel_top);
            topItemClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //取消置顶
                    subscription.add(RRetrofit.create(DiscussService.class)
                            .top(Param.buildMap("discuss_id:" + tBean.getDiscuss_id(), "is_top:0"))
                            .compose(Rx.transformer(String.class))
                            .subscribe(new BaseSingleSubscriber<String>() {
                                @Override
                                public void onSucceed(String bean) {
                                    try {
                                        T_.show(bean);
                                        tBean.setIs_top("0");
                                        bindAttentionItemView2(subscription, holder, tBean, commandAction, iLayout);
                                    } catch (Exception e) {
                                    }
                                }
                            }));
                }
            };
        } else {
            topItemString = ValleyApp.getApp().getString(R.string.top);
            topItemClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //置顶
                    subscription.add(RRetrofit.create(DiscussService.class)
                            .top(Param.buildMap("discuss_id:" + tBean.getDiscuss_id(), "is_top:1"))
                            .compose(Rx.transformer(String.class))
                            .subscribe(new BaseSingleSubscriber<String>() {
                                @Override
                                public void onSucceed(String bean) {
                                    try {
                                        T_.show(bean);
                                        tBean.setIs_top("1");
                                        bindAttentionItemView2(subscription, holder, tBean, commandAction, iLayout);
                                    } catch (Exception e) {
                                    }
                                }
                            }));
                }
            };
        }
        topItem = new UIItemDialog.ItemInfo(topItemString, topItemClickListener);

        String favItemString;
        View.OnClickListener favItemClickListener;
        if (tBean.getIs_collection() == 1) {
            favItemString = ValleyApp.getApp().getString(R.string.cancel_fav);
            favItemClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subscription.add(RRetrofit.create(SocialService.class)
                            .unCollect(Param.buildMap("type:discuss", "item_id:" + tBean.getDiscuss_id()))
                            .compose(Rx.transformer(String.class))
                            .subscribe(new BaseSingleSubscriber<String>() {

                                @Override
                                public void onSucceed(String bean) {
                                    try {
                                        T_.show(bean);
                                        tBean.setIs_collection(0);
                                        tBean.setFav_cnt(String.valueOf(Integer.valueOf(tBean.getFav_cnt()) - 1));
                                        bindAttentionItemView2(subscription, holder, tBean, commandAction, iLayout);
                                    } catch (Exception e) {

                                    }
                                }
                            }));
                }
            };
        } else {
            favItemString = ValleyApp.getApp().getString(R.string.fav);
            favItemClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subscription.add(RRetrofit.create(SocialService.class)
                            .collect(Param.buildMap("type:discuss", "item_id:" + tBean.getDiscuss_id()))
                            .compose(Rx.transformer(String.class))
                            .subscribe(new BaseSingleSubscriber<String>() {

                                @Override
                                public void onSucceed(String bean) {
                                    try {
                                        T_.show(bean);
                                        tBean.setIs_collection(1);
                                        tBean.setFav_cnt(String.valueOf(Integer.valueOf(tBean.getFav_cnt()) + 1));
                                        bindAttentionItemView2(subscription, holder, tBean, commandAction, iLayout);
                                    } catch (Exception e) {

                                    }
                                }
                            }));
                }
            };
        }
        favItem = new UIItemDialog.ItemInfo(favItemString, favItemClickListener);

        deleteItem = new UIItemDialog.ItemInfo(ValleyApp.getApp().getString(R.string.delete_text), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscription.add(RRetrofit.create(DiscussService.class)
                        .delete(Param.buildMap("discuss_id:" + tBean.getDiscuss_id()))
                        .compose(Rx.transformer(String.class))
                        .subscribe(new BaseSingleSubscriber<String>() {

                            @Override
                            public void onSucceed(String bean) {
                                try {
                                    T_.show(bean);
                                    commandAction.call(tBean);
                                } catch (Exception e) {

                                }
                            }
                        }));
            }
        });

        String followItemString;
        View.OnClickListener followItemClickListener;
        if (tBean.getUser_info().getIs_contact() == 1) {
            //是联系人
            followItemString = ValleyApp.getApp().getString(R.string.cancel_friend);
            followItemClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subscription.add(RRetrofit.create(ContactService.class)
                            .delFriend(Param.buildMap("to_uid:" + to_uid))
                            .compose(Rx.transformer(String.class))
                            .subscribe(new BaseSingleSubscriber<String>() {
                                @Override
                                public void onSucceed(String bean) {
                                    super.onSucceed(bean);
                                    try {
                                        T_.show(bean);
                                        commandAction.call(tBean);
                                    } catch (Exception e) {

                                    }
                                }
                            }));
                }
            };
        } else {
            if (tBean.getUser_info().getIs_attention() == 1) {
                //非联系人, 但是已经关注
                followItemString = ValleyApp.getApp().getString(R.string.cancel_followers);
                followItemClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        subscription.add(RRetrofit.create(UserInfoService.class)
                                .unAttention(Param.buildMap("to_uid:" + to_uid))
                                .compose(Rx.transformer(String.class))
                                .subscribe(new BaseSingleSubscriber<String>() {

                                    @Override
                                    public void onSucceed(String bean) {
                                        super.onSucceed(bean);
                                        T_.show(bean);
                                        if (commandAction != null) {
                                            commandAction.call(tBean);
                                        }
                                    }
                                }));
                    }
                };
            } else {
                followItemString = ValleyApp.getApp().getString(R.string.followers_title);
                followItemClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        subscription.add(RRetrofit.create(UserInfoService.class)
                                .attention(Param.buildMap("to_uid:" + to_uid))
                                .compose(Rx.transformer(String.class))
                                .subscribe(new BaseSingleSubscriber<String>() {

                                    @Override
                                    public void onSucceed(String bean) {
                                        T_.show(bean);
                                        if (commandAction != null) {
                                            commandAction.call(tBean);
                                        }
                                    }
                                }));
                    }
                };
            }
        }

        followItem = new UIItemDialog.ItemInfo(followItemString, followItemClickListener);

        notSeeItem = new UIItemDialog.ItemInfo(ValleyApp.getApp().getString(R.string.not_see_status), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscription.add(RRetrofit.create(SettingService.class)
                        .personal(Param.buildMap("to_uid:" + to_uid, "key:1002", "val:0"))
                        .compose(Rx.transformer(String.class))
                        .subscribe(new BaseSingleSubscriber<String>() {

                            @Override
                            public void onSucceed(String bean) {
                                T_.show(bean);
                                if (commandAction != null) {
                                    commandAction.call(tBean);
                                }
                            }
                        }));
            }
        });

        reportItem = new UIItemDialog.ItemInfo(ValleyApp.getApp().getString(R.string.report), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iLayout.startIView(new ReportUIView(tBean));
            }
        });

        if (TextUtils.equals(uid, to_uid)) {
            //自己发布的动态
            listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iLayout.startIView(UIItemDialog.build()
                            .addItem(topItem)
                            .addItem(favItem)
                            .addItem(deleteItem));
                }
            };
        } else {
            //其他人发布的动态
            listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iLayout.startIView(UIItemDialog.build()
                            .addItem(followItem)
                            .addItem(favItem)
                            .addItem(notSeeItem)
                            .addItem(reportItem));
                }
            };
        }


        commandItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(tBean.uuid)) {
                    listener.onClick(v);
                } else {
                    T_.show(holder.itemView.getResources().getString(R.string.publishing_tip));
                }
            }
        });
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
                            public void onSucceed(String bean) {
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
                            public void onSucceed(String bean) {
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
                if (!TextUtils.isEmpty(tBean.uuid)) {
                    T_.show(itemTextView.getResources().getString(R.string.publishing_tip));
                    return;
                }


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

                if (!TextUtils.isEmpty(tBean.uuid)) {
                    T_.show(itemTextView.getResources().getString(R.string.publishing_tip));
                    return;
                }

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
                                        UserDiscussListBean.DataListBean tBean, Action1<Boolean> likeAction) {

        HnItemTextView like_cnt = holder.v(R.id.like_cnt);

        if (tBean.getIs_like() == 1) {
            //是否点赞
            initLikeView(like_cnt, tBean, subscription, likeAction);
        } else {
            initUnLikeView(like_cnt, tBean, subscription, likeAction);
        }
    }

    public static String getVideoTime(int time) {
        final int videoTime = time;
        int min = videoTime / 60;
        int second = videoTime % 60;

        StringBuilder builder = new StringBuilder();
        builder.append(min >= 10 ? min : ("0" + min));
        builder.append(":");
        builder.append(second >= 10 ? second : ("0" + second));

        return builder.toString();
    }

    public static String getVideoTime(String url) {
        return getVideoTime(Integer.parseInt(url.split("t_")[1]));
    }

    public static void displayImage(final ImageView imageView, String url, int width, int height) {
//        ImagePicker.getInstance().getImageLoader().displayImage((Activity) imageView.getContext(),
//                "", "", OssHelper.getImageThumb(url, width, height), imageView, 0, 0);

        File file = new File(url);
        if (file.exists()) {
            if ("GIF".equalsIgnoreCase(ImageUtils.getImageType(file))) {
                GifRequestBuilder<File> gifRequestBuilder = Glide.with(imageView.getContext())                             //配置上下文
                        .load(file)      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                        //.error(R.mipmap.default_image)           //设置错误图片
                        //.fitCenter()
                        .asGif()
                        //.centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE);

                gifRequestBuilder.into(imageView);
            } else {
                Glide.with(imageView.getContext())                             //配置上下文
                        .load(file)
                        .placeholder(R.drawable.zhanweitu_1)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);
            }
        } else {
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
    }
}

package com.hn.d.valley.control;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.angcyo.library.glide.GlideBlurTransformation;
import com.angcyo.library.okhttp.Ok;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.dialog.UIBottomItemDialog;
import com.angcyo.uiview.dialog.UIItemDialog;
import com.angcyo.uiview.github.goodview.GoodView;
import com.angcyo.uiview.github.utilcode.utils.ClipboardUtils;
import com.angcyo.uiview.github.utilcode.utils.PhoneUtils;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.RExTextView;
import com.angcyo.uiview.widget.RImageView;
import com.angcyo.uiview.widget.RNineImageLayout;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.GifRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
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
import com.hn.d.valley.bean.ILikeData;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.me.UserDetailUIView2;
import com.hn.d.valley.service.ContactService;
import com.hn.d.valley.service.DiscussService;
import com.hn.d.valley.service.SettingService;
import com.hn.d.valley.service.SocialService;
import com.hn.d.valley.service.UserInfoService;
import com.hn.d.valley.sub.other.ReadListUserUIView;
import com.hn.d.valley.sub.user.DynamicDetailUIView2;
import com.hn.d.valley.sub.user.PublishDynamicUIView2;
import com.hn.d.valley.sub.user.ReportUIView;
import com.hn.d.valley.utils.PhotoPager;
import com.hn.d.valley.widget.HnExTextView;
import com.hn.d.valley.widget.HnGenderView;
import com.hn.d.valley.widget.HnGlideImageView;
import com.hn.d.valley.widget.HnItemTextView;
import com.hn.d.valley.widget.HnPlayTimeView;
import com.hn.d.valley.widget.HnTagsNameTextView;
import com.hn.d.valley.widget.HnVideoPlayView;
import com.hn.d.valley.x5.X5WebUIView;
import com.jakewharton.rxbinding.view.RxView;
import com.lzy.imagepicker.ImageUtils;

import java.io.File;
import java.util.List;
import java.util.Locale;
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
        initItem(subscription, holder, dataListBean, commandAction, itemRootAction, iLayout, false);
    }

    public static void initItem(CompositeSubscription subscription, RBaseViewHolder holder,
                                UserDiscussListBean.DataListBean dataListBean,
                                final Action1<UserDiscussListBean.DataListBean> commandAction, final Action0 itemRootAction,
                                final ILayout iLayout, boolean isInDetail) {
        initItem(holder, dataListBean, itemRootAction, iLayout, isInDetail);
//        bindAttentionItemView(subscription, holder, dataListBean, commandAction);
        bindAttentionItemView2(subscription, holder, dataListBean, commandAction, iLayout);
        bindFavItemView(subscription, holder, dataListBean);
        bindLikeItemView(subscription, holder, dataListBean, null);
    }

    /**
     * @see com.hn.d.valley.main.home.UserDiscussAdapter
     */
    public static void initItem(final RBaseViewHolder holder, final UserDiscussListBean.DataListBean dataListBean,
                                final Action0 itemRootAction, final ILayout iLayout, boolean isInDetail) {
        LikeUserInfoBean user_info = dataListBean.getUser_info();

        holder.fillView(dataListBean, true);
        holder.fillView(user_info, true);

        //时间
        TextView showTimeView = holder.v(R.id.show_time);
        showTimeView.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(dataListBean.uuid)) {
            showTimeView.setText(dataListBean.getShow_time());
        } else {
            showTimeView.setText(R.string.sending);
        }

        /**头像*/
        final HnGlideImageView avatarView = holder.v(R.id.avatar);
        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iLayout != null) {
                    iLayout.startIView(new UserDetailUIView2(dataListBean.getUid()));
                }
            }
        });
        avatarView.setAuth(user_info.getIs_auth());
        avatarView.setImageThumbUrl(user_info.getAvatar());

        final HnGenderView genderView = holder.v(R.id.grade);//性别,等级
        if (user_info != null) {
            genderView.setVisibility(View.VISIBLE);
            genderView.setGender(user_info.getSex(), user_info.getGrade());
        } else {
            genderView.setVisibility(View.GONE);
        }


        //内容
        HnExTextView hnExTextView = holder.v(R.id.content_ex_view);
        if (isInDetail) {
            hnExTextView.setMaxShowLine(-1);
        } else {
            hnExTextView.setMaxShowLine(10);
        }
        hnExTextView.setFoldString(holder.getContext().getString(R.string.see_all2));
        //需要先设置监听, 再设置内容.
        hnExTextView.setOnImageSpanClick(createSpanClick(iLayout));
        hnExTextView.setText(dataListBean.getContent());
        hnExTextView.setVisibility(TextUtils.isEmpty(dataListBean.getContent()) ? View.GONE : View.VISIBLE);
        //hnExTextView.setTextIsSelectable(true);
//        hnExTextView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                ClipboardUtils.copyText(dataListBean.getContent());
//                return true;
//            }
//        });

        //阅读人数
        holder.v(R.id.view_cnt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iLayout != null) {
                    iLayout.startIView(new ReadListUserUIView(dataListBean.getDiscuss_id()));
                }
            }
        });

        //标签处理
        HnTagsNameTextView tagsNameTextView = holder.v(R.id.tags_name);
        tagsNameTextView.setTags(dataListBean.getTags_name());

        //图片视频处理
        updateMediaLayout(dataListBean, iLayout, holder, isInDetail);

        HnItemTextView fav_cnt = holder.v(R.id.fav_cnt);
        View like_cnt = holder.v(R.id.like_cnt);

        if (dataListBean.getIs_collection() == 1) {
            //是否收藏
            fav_cnt.setLeftIco(R.drawable.collection_icon_s);
        } else {
            fav_cnt.setLeftIco(R.drawable.collection_icon_n);
        }

        if (like_cnt instanceof HnItemTextView) {
            if (dataListBean.getIs_like() == 1) {
                //是否点赞
                ((HnItemTextView) like_cnt).setLeftIco(R.drawable.thumb_up_icon_s);
            } else {
                ((HnItemTextView) like_cnt).setLeftIco(R.drawable.thumb_up_icon_n);
            }
        }

        //更多按钮
        View commandItemView = holder.v(R.id.command_item_view);
        commandItemView.setVisibility(View.VISIBLE);

        //转发按钮
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
            //语音动态不允许转发
            forwardView.setVisibility("4".equalsIgnoreCase(dataListBean.getMedia_type()) ? View.GONE : View.VISIBLE);

//            if (UserCache.getUserAccount().equalsIgnoreCase(user_info.getUid())) {
//                //自己的动态不允许转发
//                forwardView.setClickable(false);
//                forwardView.setEnabled(false);
//            } else {
                forwardView.setEnabled(true);
                forwardView.setClickable(true);
                forwardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(dataListBean.uuid)) {
                            iLayout.startIView(new PublishDynamicUIView2(dataListBean));
                        } else {
                            T_.show(holder.itemView.getResources().getString(R.string.publishing_tip));
                        }
                    }
                });
//            }
        } else {
            commandItemView.setVisibility(View.GONE);
            forwardView.setClickable(false);
        }

        //转发的动态处理
//        TextView infoView = holder.v(R.id.copy_info_view);
//        infoView.setVisibility(View.GONE);
        UserDiscussListBean.DataListBean.OriginalInfo originalInfo = dataListBean.getOriginal_info();
        if (!"0".equalsIgnoreCase(dataListBean.getShare_original_item_id()) && originalInfo != null) {
//            infoView.setVisibility(View.VISIBLE);
//            String content = originalInfo.getContent();
//            SpannableStringUtils.Builder builder = SpannableStringUtils.getBuilder(originalInfo.getUsername() + ": ")
//                    .setForegroundColor(infoView.getResources().getColor(R.color.colorAccent));
//            SpannableStringBuilder stringBuilder;
//            if (TextUtils.isEmpty(content)) {
//                stringBuilder = builder.create();
//            } else {
//                stringBuilder = builder.append(content)
//                        .setForegroundColor(infoView.getResources().getColor(R.color.main_text_color_dark))
//                        .create();
//            }
//            infoView.setText(stringBuilder);
            initForwardLayout(holder, originalInfo, iLayout, isInDetail);
        } else {
            holder.v(R.id.forward_control_layout).setVisibility(View.GONE);
        }

        View rootLayout = holder.v(R.id.item_root_layout);
        if (itemRootAction != null) {
            if (TextUtils.isEmpty(dataListBean.uuid)) {
                rootLayout.setBackgroundResource(R.drawable.base_white_bg_selector);
            } else {
                rootLayout.setBackgroundColor(SkinHelper.getSkin().getThemeTranColor(0x20));
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
     * 转发动态布局处理
     */
    private static void initForwardLayout(RBaseViewHolder holder,
                                          final UserDiscussListBean.DataListBean.OriginalInfo original_info,
                                          final ILayout iLayout, boolean isInDetail) {
        holder.v(R.id.forward_control_layout).setVisibility(View.VISIBLE);
        HnExTextView exTextView = holder.v(R.id.forward_content_ex_view);
        exTextView.setOnImageSpanClick(createSpanClick(iLayout));
        exTextView.setText(createMention(original_info.getUid(),
                "@" + original_info.getUsername()) +
                original_info.getContent());

        if (isInDetail) {
            exTextView.setMaxShowLine(-1);
        } else {
            exTextView.setMaxShowLine(6);
        }

        holder.v(R.id.forward_control_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iLayout.startIView(new DynamicDetailUIView2(original_info.getDiscuss_id()));
            }
        });

        final List<String> medias = RUtils.split(original_info.getMedia());
        initMediaLayout(original_info.getMedia_type(), medias,
                holder.v(R.id.forward_media_control_layout),
                iLayout, isInDetail);
    }

    /**
     * 创建@功能的Span文本
     */
    public static String createMention(String id, String name) {
        return String.format(Locale.CHINA, "<m id='%s'>%s</m>", id, name);
    }

    public static RExTextView.ImageTextSpan.OnImageSpanClick createSpanClick(final ILayout iLayout) {
        return new RExTextView.ImageTextSpan.OnImageSpanClick() {
            @Override
            public void onUrlClick(TextView view, String url) {
                iLayout.startIView(new X5WebUIView(url));
            }

            @Override
            public void onPhoneClick(final TextView view, final String phone) {
                final Resources resources = view.getContext().getResources();
                UIBottomItemDialog.build()
                        .setTitleString(resources.getString(R.string.phone_title_tip, phone))
                        .addItem(resources.getString(R.string.call), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PhoneUtils.dial(phone);
                            }
                        })
                        .addItem(resources.getString(R.string.copy), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ClipboardUtils.copyText(phone);
                                T_.show(resources.getString(R.string.copy_tip));
                            }
                        })
                        .addItem(resources.getString(R.string.add_friend), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                iLayout.startIView(new UserDetailUIView2(phone));
                            }
                        })
                        .showDialog(iLayout);
            }

            @Override
            public void onMentionClick(TextView view, String mention) {
                iLayout.startIView(new UserDetailUIView2(mention));
            }
        };
    }

    /**
     * 动态是否置顶
     */
    private static void showTopView(RBaseViewHolder holder, UserDiscussListBean.DataListBean dataListBean) {
        holder.v(R.id.top_image_view).setVisibility("1".equalsIgnoreCase(dataListBean.getIs_top()) ? View.VISIBLE : View.GONE);
    }

    private static void updateMediaLayout(UserDiscussListBean.DataListBean dataListBean,
                                          final ILayout iLayout, RBaseViewHolder holder, boolean isInDetail) {
        final TextView mediaCountView = holder.tV(R.id.media_count_view);//媒体数量
        final View mediaControlLayout = holder.v(R.id.media_control_layout);
//        final SimpleDraweeView mediaImageTypeView = holder.v(R.id.media_image_view);//

        final List<String> medias = RUtils.split(dataListBean.getMedia());
        initMediaLayout(dataListBean.getMedia_type(), medias, mediaControlLayout, iLayout, isInDetail);
    }

    /**
     * 设置媒体信息
     */
    public static void initMediaLayout(String mediaType, final List<String> medias,
                                       View mediaControlLayout,
                                       final ILayout iLayout,
                                       final boolean isInDetail) {

        if (mediaControlLayout == null) {
            return;
        }

        RNineImageLayout mediaImageTypeView = (RNineImageLayout) mediaControlLayout.findViewById(R.id.media_image_view);
        TextView videoTimeView = (TextView) mediaControlLayout.findViewById(R.id.video_time_view);
        View voiceTipView = mediaControlLayout.findViewById(R.id.voice_tip_view);
        final View videoPlayView = mediaControlLayout.findViewById(R.id.video_play_view);

        if (medias.isEmpty()) {
            mediaControlLayout.setVisibility(View.GONE);
        } else {
            mediaControlLayout.setVisibility(View.VISIBLE);

            final String url = medias.get(0);

            if ("3".equalsIgnoreCase(mediaType)) {
                //图片类型
                mediaImageTypeView.setVisibility(View.VISIBLE);
                videoTimeView.setVisibility(View.INVISIBLE);
                videoPlayView.setVisibility(View.INVISIBLE);
                voiceTipView.setVisibility(View.INVISIBLE);
                mediaImageTypeView.setDrawMask(false);
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
                        UserDiscussItemControl.displayImage(imageView, url, width, height, !isInDetail);
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
            } else if ("2".equalsIgnoreCase(mediaType)) {
                //视频类型
                mediaImageTypeView.setVisibility(View.VISIBLE);
                videoTimeView.setVisibility(View.VISIBLE);
                videoPlayView.setVisibility(View.VISIBLE);
                voiceTipView.setVisibility(View.INVISIBLE);
                ((HnVideoPlayView) videoPlayView).setPlayType(HnVideoPlayView.PlayType.VIDEO);
                mediaImageTypeView.setDrawMask(false);
                //DraweeViewUtil.setDraweeViewRes(mediaImageTypeView, R.drawable.video_release);

                String[] split = url.split("\\?");
                final String thumbUrl = split[0];
                String videoUrl = "";
                try {
                    videoUrl = split[1];
                    videoTimeView.setText(getVideoTime(videoUrl));
                } catch (Exception e) {
                    videoTimeView.setText("error");
                }
                final String finalUrl = videoUrl;
                mediaImageTypeView.setNineImageConfig(new RNineImageLayout.NineImageConfig() {
                    @Override
                    public int[] getWidthHeight(int imageSize) {
                        return OssHelper.getImageThumbSize2(thumbUrl);
                    }

                    @Override
                    public void displayImage(ImageView imageView, String url, int width, int height) {
                        UserDiscussItemControl.displayImage(imageView, url, width, height, true);
                    }

                    @Override
                    public void onImageItemClick(ImageView imageView, List<String> urlList, List<RImageView> imageList, int index) {
                        //T_.info(videoUrl);
                        if (!TextUtils.isEmpty(finalUrl)) {
                            iLayout.startIView(new VideoPlayUIView(finalUrl, imageView.getDrawable(), OssHelper.getWidthHeightWithUrl(thumbUrl)));
                        }
                    }
                });
                mediaImageTypeView.setImage(thumbUrl);
            } else if ("4".equalsIgnoreCase(mediaType)) {
                //语音类型
                videoPlayView.setVisibility(View.VISIBLE);
                videoTimeView.setVisibility(View.VISIBLE);
                voiceTipView.setVisibility(View.VISIBLE);
                mediaImageTypeView.setDrawMask(true);

                String[] split = url.split("\\?");
                String thumbUrl = "";
                String videoUrl = "";
                try {
                    thumbUrl = split[0];
                    videoUrl = split[1];
                    videoTimeView.setText(getVideoTime(videoUrl));
                } catch (Exception e) {
                    videoTimeView.setText("error");
                }

                //语音播放时长的展示
                if (isInDetail) {
                    ((HnPlayTimeView) videoTimeView).setPlayTime(0);
                } else {
                    ((HnPlayTimeView) videoTimeView).setPlayTime(-1);
                }

                //详情里面, 左下角显示播放按钮
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) videoPlayView.getLayoutParams();
                if (isInDetail) {
                    params.gravity = Gravity.START | Gravity.BOTTOM;
                    if (MusicControl.isPlaying(videoUrl)) {
                        ((HnVideoPlayView) videoPlayView).setPlayType(HnVideoPlayView.PlayType.VOICE_PAUSE);
                    } else {
                        ((HnVideoPlayView) videoPlayView).setPlayType(HnVideoPlayView.PlayType.VOICE);
                    }
                } else {
                    //其他界面居中显示
                    params.gravity = Gravity.CENTER;
                    ((HnVideoPlayView) videoPlayView).setPlayType(HnVideoPlayView.PlayType.VOICE_HOME);
                }
                videoPlayView.setLayoutParams(params);

                final String finalUrl = videoUrl;
                mediaImageTypeView.setNineImageConfig(new RNineImageLayout.NineImageConfig() {
                    @Override
                    public int[] getWidthHeight(int imageSize) {
                        float density = videoPlayView.getContext().getResources().getDisplayMetrics().density;
                        int size = (int) (density * 150);
                        return new int[]{size, size};
                    }

                    @Override
                    public void displayImage(ImageView imageView, String url, int width, int height) {
                        UserDiscussItemControl.displayVoiceImage(imageView, url, width, height, !isInDetail);
                    }

                    @Override
                    public void onImageItemClick(ImageView imageView, List<String> urlList, List<RImageView> imageList, int index) {
                        //T_.info(videoUrl);
                        if (!TextUtils.isEmpty(finalUrl)) {

                        }
                    }
                });
                mediaImageTypeView.setImage(thumbUrl);
            } else {
                mediaImageTypeView.setVisibility(View.GONE);
            }
        }
    }

    public static void initVideoTimeView(TextView videoTimeView, String url) {
        if (new File(url).exists()) {
            videoTimeView.setText(getVideoTime(url));
        } else {
            String[] split = url.split("\\?");
            final String thumbUrl = split[0];
            String videoUrl = "";
            try {
                videoUrl = split[1];
                videoTimeView.setText(getVideoTime(videoUrl));
            } catch (Exception e) {
                videoTimeView.setTextColor(Color.RED);
                videoTimeView.setText("video time format error");
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
                    iLayout.startIView(UIBottomItemDialog.build()
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
                    iLayout.startIView(UIBottomItemDialog.build()
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
    private static void initLikeView(final View itemTextView,
                                     final ILikeData tBean,
                                     final CompositeSubscription subscription,
                                     final String type,
                                     final Action1<Boolean> likeAction) {
        final String uid = UserCache.getUserAccount();

        if (itemTextView instanceof HnItemTextView) {
            ((HnItemTextView) itemTextView).setLeftIco(R.drawable.love_icon_s);
            ((HnItemTextView) itemTextView).setText(tBean.getLikeCount());
        } else if (itemTextView instanceof ImageView) {
            ((ImageView) itemTextView).setImageResource(R.drawable.love_icon_s);
        }

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
                if (tBean instanceof UserDiscussListBean.DataListBean) {
                    if (!TextUtils.isEmpty(((UserDiscussListBean.DataListBean) tBean).uuid)) {
                        T_.show(itemTextView.getResources().getString(R.string.publishing_tip));
                        return;
                    }
                }


                if (likeAction != null) {
                    //取消点赞
                    likeAction.call(false);
                }

                tBean.setIsLike(0);
                tBean.setLikeCount(String.valueOf(Integer.valueOf(tBean.getLikeCount()) - 1));
                initUnLikeView(itemTextView, tBean, subscription, type, likeAction);

                subscription.add(RRetrofit.create(SocialService.class)
                        .dislike(Param.buildMap("type:" + type, "item_id:" + tBean.getDiscussId(type)))
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
    private static void initUnLikeView(final View itemTextView,
                                       final ILikeData tBean,
                                       final CompositeSubscription subscription,
                                       final String type,
                                       final Action1<Boolean> likeAction) {
        final String uid = UserCache.getUserAccount();

        if (itemTextView instanceof HnItemTextView) {
            ((HnItemTextView) itemTextView).setLeftIco(R.drawable.love_icon_n);
            ((HnItemTextView) itemTextView).setText(tBean.getLikeCount());
        } else if (itemTextView instanceof ImageView) {
            ((ImageView) itemTextView).setImageResource(R.drawable.love_icon_n);
        }

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

                if (tBean instanceof UserDiscussListBean.DataListBean) {
                    if (!TextUtils.isEmpty(((UserDiscussListBean.DataListBean) tBean).uuid)) {
                        T_.show(itemTextView.getResources().getString(R.string.publishing_tip));
                        return;
                    }
                }

                if (likeAction != null) {
                    //点赞
                    likeAction.call(true);
                }

                GoodView.build(itemTextView);
                tBean.setIsLike(1);
                tBean.setLikeCount(String.valueOf(Integer.valueOf(tBean.getLikeCount()) + 1));
                initLikeView(itemTextView, tBean, subscription, type, likeAction);

                subscription.add(RRetrofit.create(SocialService.class)
                        .like(Param.buildMap("type:" + type, "item_id:" + tBean.getDiscussId(type)))
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
                                        ILikeData tBean,
                                        Action1<Boolean> likeAction) {

        bindLikeItemView(subscription, holder, tBean, "discuss", likeAction);
    }

    public static void bindLikeItemView(final CompositeSubscription subscription,
                                        RBaseViewHolder holder,
                                        ILikeData tBean,
                                        String likeType,
                                        Action1<Boolean> likeAction) {

        View like_cnt = holder.v(R.id.like_cnt);

        if (tBean.getIsLike() == 1) {
            //是否点赞
            initLikeView(like_cnt, tBean, subscription, likeType, likeAction);
        } else {
            initUnLikeView(like_cnt, tBean, subscription, likeType, likeAction);
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
        return getVideoTime(Integer.parseInt(url.substring(0, url.lastIndexOf('.')).split("t_")[1]));
    }

    public static void displayImage(final ImageView imageView, final String url,
                                    final int width, final int height) {
        displayImage(imageView, url, width, height, true);
    }

    public static void displayImage(final ImageView imageView, final String url,
                                    final int width, final int height, final boolean noGif) {
//        ImagePicker.getInstance().getImageLoader().displayImage((Activity) imageView.getContext(),
//                "", "", OssHelper.getImageThumb(url, width, height), imageView, 0, 0);

        File file = new File(url);
        if (file.exists()) {
            boolean isGif = "GIF".equalsIgnoreCase(ImageUtils.getImageType(file));
            if (noGif && imageView instanceof RImageView) {
                ((RImageView) imageView).setShowGifTip(isGif);
            }
            if (!noGif && isGif) {
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
//            if (noGif) {
//                displayJpeg(imageView, url, width, height);
//            } else
            {
                Ok.instance().type(url, new Ok.OnImageTypeListener() {
                    @Override
                    public void onImageType(Ok.ImageType imageType) {
                        L.e("call: onImageType([imageType])-> " + url + " : " + imageType);

                        if (imageType != Ok.ImageType.UNKNOWN) {
                            if (!noGif && imageType == Ok.ImageType.GIF) {
                                Glide.with(imageView.getContext())
                                        .load(url)
                                        .asGif()
                                        .placeholder(R.drawable.zhanweitu_1)
                                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                        .into(new SimpleTarget<GifDrawable>() {
                                            @Override
                                            public void onResourceReady(GifDrawable resource, GlideAnimation<? super GifDrawable> glideAnimation) {
                                                if (imageView == null || resource == null) {
                                                    return;
                                                }
                                                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                                imageView.setImageDrawable(resource);
                                                resource.start();
                                            }
                                        });
                            } else {
                                if (imageView instanceof RImageView) {
                                    ((RImageView) imageView).setShowGifTip(imageType == Ok.ImageType.GIF);
                                }
                                displayJpeg(imageView, url, width, height);
                            }
                        }
                    }

                    @Override
                    public void onLoadStart() {
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setImageResource(R.drawable.zhanweitu_1);
                    }
                });
            }
        }
    }

    public static void displayJpeg(final ImageView imageView, final String url, int width, int height) {
        Glide.with(imageView.getContext())
                .load(OssHelper.getImageThumb(url, width, height))
                .asBitmap()
                .placeholder(R.drawable.zhanweitu_1)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (imageView == null || resource == null) {
                            return;
                        }

                        int w = resource.getWidth();
                        int h = resource.getHeight();
                        L.e("call: onResourceReady([resource, glideAnimation])-> " + url + " w:" +
                                w + " H:" + h);

                        int abs = Math.abs(w - h);

                        //自动根据图片的长宽差, 选择缩放类型
                        if (abs < Math.min(w / 2, h / 2)) {
                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        } else {
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }

                        if (imageView instanceof RImageView) {
                            ((RImageView) imageView).setImageBitmap(imageView.getDrawable(), resource);
                        } else {
                            imageView.setImageBitmap(resource);
                        }
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setImageResource(R.drawable.zhanweitu_1);
                    }
                });
    }

    public static void displayVoiceImage(final ImageView imageView, String url, int width, int height, boolean blur) {
        if (imageView instanceof RImageView) {
            ((RImageView) imageView).setShowGifTip(false);
        }
        File file = new File(url);
        if (file.exists()) {
            Glide.with(imageView.getContext())                             //配置上下文
                    .load(file)
                    .placeholder(R.drawable.default_vociecover)
                    .bitmapTransform(new GlideBlurTransformation(imageView.getContext()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        } else {
            DrawableRequestBuilder<String> builder = Glide.with(imageView.getContext())
                    .load(OssHelper.getImageThumb(url, width, height))
                    .placeholder(R.drawable.default_vociecover)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            if (blur) {
                builder.bitmapTransform(new GlideBlurTransformation(imageView.getContext()))
                        .into(imageView);
            } else {
                builder.into(imageView);
            }
        }
    }
}

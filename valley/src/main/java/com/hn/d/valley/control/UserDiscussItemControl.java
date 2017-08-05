package com.hn.d.valley.control;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.angcyo.github.utilcode.utils.ClipboardUtils;
import com.angcyo.github.utilcode.utils.PhoneUtils;
import com.angcyo.library.glide.GlideBlurTransformation;
import com.angcyo.library.okhttp.Ok;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.dialog.UIBottomItemDialog;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.dialog.UIItemDialog;
import com.angcyo.uiview.github.goodview.GoodView;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.viewgroup.RLinearLayout;
import com.angcyo.uiview.widget.GlideImageView;
import com.angcyo.uiview.widget.ImageTextView;
import com.angcyo.uiview.widget.RExTextView;
import com.angcyo.uiview.widget.RImageView;
import com.angcyo.uiview.widget.RNineImageLayout;
import com.angcyo.uiview.widget.RTextView;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.GifRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.m3b.Audio;
import com.hn.d.valley.BuildConfig;
import com.hn.d.valley.R;
import com.hn.d.valley.ValleyApp;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.iview.ImagePagerUIView;
import com.hn.d.valley.base.iview.RelayPhotoLongClickListener;
import com.hn.d.valley.base.iview.RelayVideoLongClickListener;
import com.hn.d.valley.base.iview.VideoPlayUIView;
import com.hn.d.valley.base.oss.OssHelper;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.HotInfoListBean;
import com.hn.d.valley.bean.ILikeData;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.found.sub.InformationDetailUIView;
import com.hn.d.valley.main.me.UserDetailUIView2;
import com.hn.d.valley.main.message.gift.GiftListUIView2;
import com.hn.d.valley.main.message.redpacket.OpenRedPacketUIDialog;
import com.hn.d.valley.main.message.redpacket.RewardUIVIew;
import com.hn.d.valley.service.ContactService;
import com.hn.d.valley.service.DiscussService;
import com.hn.d.valley.service.SettingService;
import com.hn.d.valley.service.SocialService;
import com.hn.d.valley.service.UserService;
import com.hn.d.valley.sub.other.ReadListUserUIView;
import com.hn.d.valley.sub.user.DynamicDetailUIView2;
import com.hn.d.valley.sub.user.DynamicType;
import com.hn.d.valley.sub.user.ReportUIView;
import com.hn.d.valley.sub.user.dialog.DynamicShareDialog;
import com.hn.d.valley.utils.PhotoPager;
import com.hn.d.valley.widget.HnExTextView;
import com.hn.d.valley.widget.HnGenderView;
import com.hn.d.valley.widget.HnGlideImageView;
import com.hn.d.valley.widget.HnItemTextView;
import com.hn.d.valley.widget.HnPlayTimeView;
import com.hn.d.valley.widget.HnTagsNameTextView;
import com.hn.d.valley.widget.HnVideoPlayView;
import com.hn.d.valley.widget.groupView.AutoPlayVideoLayout;
import com.hn.d.valley.x5.X5WebUIView;
import com.jakewharton.rxbinding.view.RxView;
import com.lzy.imagepicker.ImageUtils;
import com.lzy.imagepicker.YImageControl;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
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
    /**
     * 已经更新数量的动态
     */
    private static Set<String> updateReadCountList = new HashSet<>();

    /**
     * @see com.hn.d.valley.R.layout#item_search_user_item_layout
     */
    public static void initItem(CompositeSubscription subscription, RBaseViewHolder holder,
                                UserDiscussListBean.DataListBean dataListBean,
                                final Action1<UserDiscussListBean.DataListBean> commandAction, final Action1<Boolean> itemRootAction,
                                final ILayout iLayout) {
        initItem(subscription, holder, dataListBean, commandAction, itemRootAction, iLayout, false);
    }

    public static void initItem(CompositeSubscription subscription, RBaseViewHolder holder,
                                UserDiscussListBean.DataListBean dataListBean,
                                final Action1<UserDiscussListBean.DataListBean> commandAction, final Action1<Boolean> itemRootAction,
                                final ILayout iLayout, boolean isInDetail) {
        initItem(holder, dataListBean, subscription, itemRootAction, iLayout, isInDetail, "1".equalsIgnoreCase(dataListBean.getAllow_download()));
//        bindAttentionItemView(subscription, holder, dataListBean, commandAction);
        bindAttentionItemView2(subscription, holder, dataListBean, commandAction, iLayout, isInDetail);//更多按钮初始化
        bindFavItemView(subscription, holder, dataListBean, isInDetail);//收藏按钮初始化
        bindLikeItemView(subscription, holder, dataListBean, new InitLikeViewCallback() {
        }, isInDetail);//点赞按钮
        bindRewardItemView(iLayout, holder, dataListBean, isInDetail);//打赏按钮
    }

    /**
     * @see com.hn.d.valley.main.home.UserDiscussAdapter
     */
    public static void initItem(final RBaseViewHolder holder, final UserDiscussListBean.DataListBean dataListBean,
                                @NonNull final CompositeSubscription subscription,
                                final Action1<Boolean> itemRootAction, final ILayout iLayout, boolean isInDetail, final boolean allowDownload) {
        LikeUserInfoBean user_info = dataListBean.getUser_info();

        //holder.fillView(dataListBean, true);
        //holder.fillView(user_info, true);

        //推荐动态
        holder.v(R.id.top_image_view).setVisibility("1".equalsIgnoreCase(dataListBean.getIs_recommend()) ? View.VISIBLE : View.GONE);

        //用户名
        holder.tv(R.id.username).setText(user_info.getUsername());
        //多少人阅读
        holder.tv(R.id.view_cnt).setText(dataListBean.getView_cnt());
        //地址
        holder.tv(R.id.address).setText(dataListBean.getAddress());
        holder.tv(R.id.address).setVisibility(TextUtils.isEmpty(dataListBean.getAddress()) ? View.GONE : View.VISIBLE);

        holder.tv(R.id.fav_cnt).setText(dataListBean.getFav_cnt());
        holder.tv(R.id.forward_cnt).setText(dataListBean.getForward_cnt());
        View likeCntView = holder.v(R.id.like_cnt);
        if (likeCntView instanceof TextView) {
            ((TextView) likeCntView).setText(dataListBean.getLike_cnt());
        }
        holder.tv(R.id.comment_cnt).setText(dataListBean.getComment_cnt());

        //时间
        RTextView showTimeView = holder.v(R.id.show_time);
        showTimeView.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(dataListBean.uuid)) {
            showTimeView.setText(dataListBean.getShow_time());

            if ("2".equalsIgnoreCase(dataListBean.getScan_type())) {
                showTimeView.setRightIco(R.drawable.private_gary);
            } else if ("1".equalsIgnoreCase(dataListBean.getScan_type())) {
                showTimeView.setRightIco(-1);
            } else {
                showTimeView.setRightIco(R.drawable.partiallyvisible_gray);
            }
        } else {
            if (PublishControl.instance().getTaskStatus(dataListBean.uuid) == PublishTaskRealm.STATUS_ERROR) {
                showTimeView.setText(R.string.send_error);
            } else {
                showTimeView.setText(R.string.sending);
            }
        }

        /*头像*/
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
        tagsNameTextView.setVisibility(View.GONE);//不需要标签啦, 星期一 2017-6-26

        //红包id
        String hotPackageId = null;
        try {
            hotPackageId = dataListBean.getPackage_id();
            holder.v(R.id.hot_package_view).setVisibility(TextUtils.isEmpty(hotPackageId) ? View.INVISIBLE : View.VISIBLE);
            if ((BuildConfig.DEBUG /*|| BuildConfig.SHOW_DEBUG*/) && !TextUtils.isEmpty(hotPackageId)) {
                OpenRedPacketUIDialog.grabRedBag(Long.valueOf(hotPackageId), "{\"discuss_id\":\"" + dataListBean.getDiscuss_id() + "\"}")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseSingleSubscriber<Integer>() {

                            @Override
                            public void onSucceed(Integer beans) {
                                switch (beans) {
                                    case 0:
                                        L.e("抢红包:SUCCESS");
                                        break;
                                    case 5:
                                        L.e("抢红包:已经抢过");
                                        break;
                                    case 7:
                                        L.e("抢红包:已经被抢光");
                                        break;
                                    case 8:
                                        L.e("抢红包:没有权限");
                                        break;
                                    default:
                                        L.e("抢红包:失败" + beans);
                                        break;
                                }
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //图片视频处理
        updateMediaLayout(dataListBean, iLayout, holder, isInDetail, allowDownload, hotPackageId);

        HnItemTextView fav_cnt = holder.v(R.id.fav_cnt);

        if (dataListBean.getIs_collection() == 1) {
            //是否收藏
            fav_cnt.setLeftIco(R.drawable.collection_icon_s);
        } else {
            fav_cnt.setLeftIco(R.drawable.collection_icon_n);
        }

        if (likeCntView instanceof HnItemTextView) {
            if (dataListBean.getIs_like() == 1) {
                //是否点赞
                ((HnItemTextView) likeCntView).setLeftIco(R.drawable.thumb_up_icon_s);
            } else {
                ((HnItemTextView) likeCntView).setLeftIco(R.drawable.thumb_up_icon_n);
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
            //语音动态不允许转发, 允许转发星期三 2017-8-2
            //forwardView.setVisibility("4".equalsIgnoreCase(dataListBean.getMedia_type()) ? View.GONE : View.VISIBLE);
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

                    iLayout.startIView(new DynamicShareDialog(dataListBean, subscription)
                            .setCanShare(dataListBean.canForward()));


//                    if (dataListBean.canForward()) {
//                        if (dataListBean.isForwardInformation()) {
//                            iLayout.startIView(new PublishDynamicUIView2(HotInfoListBean.from(dataListBean.getOriginal_info())));
//                        } else if (TextUtils.isEmpty(dataListBean.uuid)) {
//                            iLayout.startIView(new PublishDynamicUIView2(dataListBean));
//                        } else {
//                            T_.show(holder.itemView.getResources().getString(R.string.publishing_tip));
//                        }
//                    } else {
//                        T_.error(holder.itemView.getResources().getString(R.string.cant_forward_tip));
//                    }
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
        if (originalInfo != null && !"0".equalsIgnoreCase(dataListBean.getShare_original_item_id())) {
            originalInfo.setForwardInformation(dataListBean.isForwardInformation());
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
            initForwardLayout(holder, originalInfo, iLayout, isInDetail, allowDownload);
        } else {
            holder.v(R.id.forward_control_layout).setVisibility(View.GONE);
        }

        //动态未发布
        View rootLayout = holder.v(R.id.item_root_layout);
        if (itemRootAction != null) {
            if (TextUtils.isEmpty(dataListBean.uuid)) {
                rootLayout.setBackgroundResource(R.drawable.base_bg_selector);
            } else {
                rootLayout.setBackgroundColor(SkinHelper.getSkin().getThemeTranColor(0x20));
            }

            rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(dataListBean.uuid)) {
                        itemRootAction.call(false);
                    } else {
                        T_.show(holder.itemView.getResources().getString(R.string.publishing_tip));
                    }
                }
            });

            holder.v(R.id.comment_cnt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(dataListBean.uuid)) {
                        itemRootAction.call(true);
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
                                          final ILayout iLayout,
                                          final boolean isInDetail, final boolean allowDownload) {
        holder.v(R.id.forward_control_layout).setVisibility(View.VISIBLE);
        View forwardContentLayout = holder.v(R.id.forward_content_layout);
        HnExTextView exTextView = holder.v(R.id.forward_content_ex_view);
        TextView dynamicStateTipView = holder.v(R.id.dynamic_state_tip_view);

        if ("1".equalsIgnoreCase(original_info.getStatus())) {
            //贴子正常
            dynamicStateTipView.setVisibility(View.GONE);
            forwardContentLayout.setVisibility(View.VISIBLE);

            holder.v(R.id.forward_control_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (original_info.isForwardInformation()) {
                        iLayout.startIView(new InformationDetailUIView(HotInfoListBean.from(original_info)));
                    } else {
                        jumpToDynamicDetailUIView(iLayout, original_info.getDiscuss_id(), true, isInDetail, false);

                        if (!isInDetail) {
                            updateDiscussReadCnt(original_info.getDiscuss_id());
                        }
                    }
                }
            });
        } else {
            //帖子异常
            dynamicStateTipView.setVisibility(View.VISIBLE);
            forwardContentLayout.setVisibility(View.GONE);
            if ("2".equalsIgnoreCase(original_info.getStatus())) {
                dynamicStateTipView.setText(R.string.dynamic_delete_tip);
            } else {
                dynamicStateTipView.setText(R.string.dynamic_shield_tip);
            }

            holder.v(R.id.forward_control_layout).setOnClickListener(null);
        }

        //转发内容显示
        if (original_info.isForwardInformation()) {
            exTextView.setText(original_info.getTitle());

            if ("article".equalsIgnoreCase(original_info.getMedia_type())) {
                if (!original_info.getMediaList().isEmpty()) {
                    original_info.setMedia_type("picture");
                }
            }
        } else {
            exTextView.setOnImageSpanClick(createSpanClick(iLayout));
            exTextView.setText(createMention(original_info.getUid(),
                    "@" + original_info.getUsername()) + " " +
                    original_info.getContent());
        }

        if (isInDetail) {
            exTextView.setMaxShowLine(-1);
        } else {
            exTextView.setMaxShowLine(6);
        }

        //图片, 视频处理
        RImageView forwardInformationImageView = (RImageView) holder.v(R.id.forward_control_layout).findViewById(R.id.forward_information_image_view);
        ImageTextView forwardAuthorView = (ImageTextView) holder.v(R.id.forward_control_layout).findViewById(R.id.image_text_view);

        final List<String> medias = RUtils.split(original_info.getMedia());
        String media_type = original_info.getMedia_type();

        if (original_info.isForwardInformation()) {
            //转发资讯
            forwardInformationImageView.setVisibility(View.VISIBLE);
            forwardAuthorView.setVisibility(View.VISIBLE);
            holder.v(R.id.forward_media_control_layout).setVisibility(View.GONE);
            forwardInformationImageView.setShowGifTip(false);
            forwardInformationImageView.setPlayDrawable(null);

            //资讯作者
            String author = original_info.getAuthor();
            if (TextUtils.isEmpty(author)) {
                forwardAuthorView.setShowText(holder.getContext().getString(R.string.information_klg));
            } else {
                forwardAuthorView.setShowText(author);
            }

            //资讯logo
            String logo = original_info.getLogo();
            if (TextUtils.isEmpty(logo)) {
                forwardAuthorView.getImageView().setImageResource(R.drawable.logo_20170525);
            } else {
                forwardAuthorView.getImageView().setTag(R.id.tag_url, logo);
                UserDiscussItemControl.displayImage(forwardAuthorView.getImageView(), logo, 0, 0, true, 9);
            }

            if (medias.isEmpty()) {
                forwardInformationImageView.setImageResource(R.drawable.zhuanfa_zhixun_zhanweitu);
            } else if (original_info.isPicture()) {
                String url = medias.get(0);
                forwardInformationImageView.setTag(R.id.tag_url, url);
                if (YImageControl.isYellowImage(url)) {
                    YImageControl.showYellowImageXiao(forwardInformationImageView);
                } else {
                    UserDiscussItemControl.displayImage(forwardInformationImageView, url, 0, 0, true, 9);
                }
            } else if (original_info.isVideo()) {
                forwardInformationImageView.setPlayDrawable(R.drawable.image_picker_play);

                String url = medias.get(0);
                String[] videoParams = getVideoParams(url);
                final String thumbUrl = videoParams[0];
                final String videoUrl = videoParams[1];
                forwardInformationImageView.setTag(R.id.tag_url, thumbUrl);
                if (YImageControl.isYellowImage(thumbUrl)) {
                    YImageControl.showYellowImageXiao(forwardInformationImageView);
                } else {
                    UserDiscussItemControl.displayImage(forwardInformationImageView, thumbUrl, 0, 0, true, 9);
                }
            } else {
                forwardInformationImageView.setImageResource(R.drawable.zhuanfa_zhixun_zhanweitu);
            }
        } else {
            //转发动态
            forwardInformationImageView.setVisibility(View.GONE);
            forwardAuthorView.setVisibility(View.GONE);
            holder.v(R.id.forward_media_control_layout).setVisibility(View.VISIBLE);

            initMediaLayout(media_type, medias,
                    holder.v(R.id.forward_media_control_layout),
                    iLayout, isInDetail, original_info.isForwardInformation(), allowDownload, "");
        }
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
     * 动态是否置顶,
     */
    public static void showTopView(RBaseViewHolder holder, UserDiscussListBean.DataListBean dataListBean) {
        //置顶动态
        if ("1".equalsIgnoreCase(dataListBean.getIs_top())) {
            ((RLinearLayout) holder.itemView).setRBackgroundDrawable(ContextCompat.getColor(holder.getContext(), R.color.top_background_color));
        } else {
            ((RLinearLayout) holder.itemView).setRBackgroundDrawable(ContextCompat.getColor(holder.getContext(), R.color.base_white));
        }
    }

    private static void updateMediaLayout(UserDiscussListBean.DataListBean dataListBean,
                                          final ILayout iLayout, RBaseViewHolder holder,
                                          boolean isInDetail, final boolean allowDownload,
                                          final String hotPackageId) {
        //final TextView mediaCountView = holder.tV(R.id.media_count_view);//媒体数量
        final View mediaControlLayout = holder.v(R.id.media_control_layout);
//        final SimpleDraweeView mediaImageTypeView = holder.v(R.id.media_image_view);//

        final List<String> medias = RUtils.split(dataListBean.getMedia());
        initMediaLayout(dataListBean.getMedia_type(), medias, mediaControlLayout, iLayout,
                isInDetail, dataListBean.getDiscuss_id(), false, allowDownload, hotPackageId);
    }

    public static void initMediaLayout(String mediaType, final List<String> medias,
                                       View mediaControlLayout,
                                       final ILayout iLayout,
                                       final boolean isInDetail,
                                       final boolean isFromInformation,
                                       final boolean allowDownload,
                                       final String hotPackageId) {
        initMediaLayout(mediaType, medias, mediaControlLayout, iLayout, isInDetail, "", isFromInformation, allowDownload, hotPackageId);
    }

    /**
     * 从url中, 读取视频缩略图和时间参数
     */
    public static String[] getVideoParams(String url) {
        String[] result = new String[]{"", ""};
        if (TextUtils.isEmpty(url)) {
            return result;
        }

        try {
            result[0] = url.substring(0, url.lastIndexOf('?'));
            result[1] = url.substring(url.lastIndexOf('?') + 1, url.length());
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 设置媒体信息
     */
    public static void initMediaLayout(String mediaType, final List<String> medias,
                                       View mediaControlLayout,
                                       final ILayout iLayout,
                                       final boolean isInDetail /*是否来自动态详情*/,
                                       final String discuss_id,
                                       final boolean isFromInformation /*是否来自资讯*/,
                                       final boolean allowDownload /*是否允许下载媒体文件*/,
                                       final String hotPackageId /*红包id,没有为空*/) {

        if (mediaControlLayout == null) {
            return;
        }

        if (mediaControlLayout instanceof AutoPlayVideoLayout) {
            ((AutoPlayVideoLayout) mediaControlLayout).setVideoPath("");
        }

        RNineImageLayout mediaImageTypeView = (RNineImageLayout) mediaControlLayout.findViewById(R.id.media_image_view);
        mediaImageTypeView.setContainVideo(false);
        TextView videoTimeView = (TextView) mediaControlLayout.findViewById(R.id.video_time_view);
        TextView bottomVideoTimeView = (TextView) mediaControlLayout.findViewById(R.id.bottom_video_time_view);

        View voiceTipView = mediaControlLayout.findViewById(R.id.voice_tip_view);
        View bottomVoiceTipView = mediaControlLayout.findViewById(R.id.bottom_voice_tip_view);
        final HnVideoPlayView videoPlayView = (HnVideoPlayView) mediaControlLayout.findViewById(R.id.video_play_view);
        bottomVoiceTipView.setVisibility(View.GONE);
        bottomVideoTimeView.setVisibility(View.GONE);

        if (medias.isEmpty()) {
            mediaControlLayout.setVisibility(View.GONE);
        } else {
            mediaControlLayout.setVisibility(View.VISIBLE);

            final String url = medias.get(0);

            if ("picture".equalsIgnoreCase(mediaType) || DynamicType.isImage(mediaType)) {
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
//                        final int[] widthHeightWithUrl = OssHelper.getWidthHeightWithUrl(YImageControl.url(url));
//                        int[] displaySize2 = OssHelper.getThumbDisplaySize3(widthHeightWithUrl[0], widthHeightWithUrl[1]);
//                        L.e("call: getWidthHeight([imageSize])-> url:" + url + " " + widthHeightWithUrl[0] + " " + widthHeightWithUrl[1] +
//                                " display:" + displaySize2[0] + " " + displaySize2[1]);
//                        return displaySize2;
                        return OssHelper.getImageThumbSize2(YImageControl.url(url));
                    }

                    @Override
                    public void displayImage(final GlideImageView imageView, String url, int width, int height, int imageSize) {
                        imageView.reset();
                        imageView.setCheckGif(true);
                        imageView.setShowGifTip(true);
                        imageView.setShowAsGifImage(isInDetail);
                        imageView.setAnimType(GlideImageView.AnimType.NONE);

//                        imageView.setOverride(false);
                        //imageView.setPlaceholderRes(R.drawable.zhanweitu_1);
                        if (imageSize == 1) {
                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        } else {
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        }

                        if (YImageControl.isYellowImage(url)) {
                            YImageControl.showYellowImageXiao(imageView);
                        } else {
//                            UserDiscussItemControl.displayImage(imageView, url,
//                                    isFromInformation ? 0 : ScreenUtil.screenWidth / 3,
//                                    isFromInformation ? 0 : ScreenUtil.screenWidth / 3,
//                                    !isInDetail, imageSize);
//                            imageView.setImageResource(R.drawable.zhanweitu_1);

//                            imageView.setUrl((width > 0 && height > 0) ?
//                                    OssHelper.getImageThumb(url, ScreenUtil.screenWidth / 3, ScreenUtil.screenWidth / 3) :
//                                    url);
                            //L.e(url + ":" + imageSize + " -> " + width + " " + height);
                            imageView.setUrl((width > 0 && height > 0) ? OssHelper.getImageThumb(url, width, height) : url);
//                            imageView.setUrl(url);
                        }
                    }

                    @Override
                    public void onImageItemClick(GlideImageView imageView, List<String> urlList, List<GlideImageView> imageList, int index) {
                        //点击预览全部图片
                        ImagePagerUIView.start(iLayout, imageView,
                                PhotoPager.getImageItems(medias, imageList, allowDownload), index)
                                .setPhotoViewLongClickListener(new RelayPhotoLongClickListener(iLayout));

                        if (!isInDetail) {
                            updateDiscussReadCnt(discuss_id);
                        }
                    }
                });
                mediaImageTypeView.setImagesList(medias);
//                }
            } else if ("video".equalsIgnoreCase(mediaType) || DynamicType.isVideo(mediaType)) {
                //视频类型
                mediaImageTypeView.setVisibility(View.VISIBLE);
                videoTimeView.setVisibility(View.VISIBLE);
                videoPlayView.setVisibility(View.VISIBLE);
                voiceTipView.setVisibility(View.INVISIBLE);
                videoPlayView.setPlayType(HnVideoPlayView.PlayType.VIDEO);
                mediaImageTypeView.setDrawMask(false);
                //DraweeViewUtil.setDraweeViewRes(mediaImageTypeView, R.drawable.video_release);
                mediaImageTypeView.setContainVideo(true);

                String[] videoParams = getVideoParams(url);
                final String thumbUrl = videoParams[0];
                final String videoUrl = videoParams[1];

                try {
                    videoTimeView.setText(getVideoTime(videoUrl));
                } catch (Exception e) {
                    videoTimeView.setText("error");
                }

                mediaImageTypeView.setNineImageConfig(new RNineImageLayout.NineImageConfig() {
                    @Override
                    public int[] getWidthHeight(int imageSize) {
                        //return OssHelper.getImageThumbSize2(thumbUrl);
                        return new int[]{(int) (250 * ScreenUtil.density), (int) (150 * ScreenUtil.density)};
                    }

                    @Override
                    public void displayImage(GlideImageView imageView, String url, int width, int height, int imageSize) {
                        imageView.reset();
//                        if (YImageControl.isYellowImage(url)) {
//                            YImageControl.showYellowImageXiao(imageView);
//                        } else {
//                            UserDiscussItemControl.displayImage(imageView, url,
//                                    isFromInformation ? 0 : width, isFromInformation ? 0 : height, true, 9);
//                        }

                        //imageView.setPlaceholderRes(R.drawable.zhanweitu_1);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        if (YImageControl.isYellowImage(url)) {
                            YImageControl.showYellowImageXiao(imageView);
                        } else {
//                            UserDiscussItemControl.displayImage(imageView, url,
//                                    isFromInformation ? 0 : ScreenUtil.screenWidth / 3,
//                                    isFromInformation ? 0 : ScreenUtil.screenWidth / 3,
//                                    !isInDetail, imageSize);
//                            imageView.setImageResource(R.drawable.zhanweitu_1);
                            if (isFromInformation) {
                                imageView.setUrl(url);
                            } else {
                                imageView.setUrl((width > 0 && height > 0) ? OssHelper.getImageThumb(url, width, height) : url);
                            }
                        }
                    }

                    @Override
                    public void onImageItemClick(GlideImageView imageView, List<String> urlList, List<GlideImageView> imageList, int index) {
                        //T_.info(videoUrl);
                        if (!TextUtils.isEmpty(videoUrl)) {
                            iLayout.startIView(new VideoPlayUIView(videoUrl,
                                    thumbUrl, imageView.copyDrawable(),
                                    OssHelper.getWidthHeightWithUrl(thumbUrl))
                                    .resetViewLocation(imageView)
                                    .setHotPackageId(hotPackageId)
                                    .setDiscuss_id(discuss_id)
                                    .setRelayVideoLongClickListener(new RelayVideoLongClickListener(iLayout, allowDownload)));
                        }
                        if (!isInDetail) {
                            updateDiscussReadCnt(discuss_id);
                        }
                    }
                });
                mediaImageTypeView.setImage(thumbUrl);

                if (!isInDetail && mediaControlLayout instanceof AutoPlayVideoLayout) {
                    ((AutoPlayVideoLayout) mediaControlLayout).setVideoPath(videoUrl);
                    ((AutoPlayVideoLayout) mediaControlLayout).setVideoSize(OssHelper.getImageThumbSize2(thumbUrl));
                }
            } else if (DynamicType.isVoice(mediaType)) {
                //语音类型
                videoPlayView.setVisibility(View.VISIBLE);
                videoTimeView.setVisibility(View.VISIBLE);
                voiceTipView.setVisibility(View.VISIBLE);
                mediaImageTypeView.setDrawMask(true);
                mediaImageTypeView.setContainVideo(true);

                String[] split = getVideoParams(url);
                String thumbUrl = split[0];
                String videoUrl = split[1];
                try {
                    videoTimeView.setText(getVideoTime(videoUrl));
                } catch (Exception e) {
                    videoTimeView.setText("error");
                }

                //语音播放时长的展示
                if (isInDetail) {
                    ((HnPlayTimeView) videoTimeView).setPlayTime(Audio.instance().getCurrentPosition(videoUrl) / 1000);
                } else {
                    ((HnPlayTimeView) videoTimeView).setPlayTime(-1);
                }

                //详情里面, 左下角显示播放按钮
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) videoPlayView.getLayoutParams();
                if (isInDetail) {
                    //在详情中显示
                    params.gravity = Gravity.START | Gravity.BOTTOM;
                    if (MusicControl.isPlaying(videoUrl)) {
                        videoPlayView.setPlayType(HnVideoPlayView.PlayType.VOICE_PAUSE);
                    } else {
                        videoPlayView.setPlayType(HnVideoPlayView.PlayType.VOICE);
                    }
                } else {
                    //其他界面居中显示
                    params.gravity = Gravity.CENTER;
                    videoPlayView.setPlayType(HnVideoPlayView.PlayType.VOICE_HOME);

                    videoTimeView.setVisibility(View.GONE);
                    voiceTipView.setVisibility(View.GONE);
                    bottomVoiceTipView.setVisibility(View.VISIBLE);
                    bottomVideoTimeView.setVisibility(View.VISIBLE);
                    bottomVideoTimeView.setText(videoTimeView.getText());
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
                    public void displayImage(GlideImageView imageView, String url, int width, int height, int imageSize) {
                        imageView.reset();
                        imageView.setPlaceholderRes(R.drawable.default_vociecover);
                        imageView.setImageResource(R.drawable.default_vociecover);

                        if (YImageControl.isYellowImage(url)) {
                            YImageControl.showYellowImageXiao(imageView);
                        } else {
//                            UserDiscussItemControl.displayVoiceImage(imageView, url,
//                                    isFromInformation ? 0 : width, isFromInformation ? 0 : height, !isInDetail);

                            if (!isInDetail) {
                                imageView.setBitmapTransform(new GlideBlurTransformation(imageView.getContext()));
                            }

                            if (isFromInformation) {
                                imageView.setUrl(url);
                            } else {
                                imageView.setUrl((width > 0 && height > 0) ? OssHelper.getImageThumb(url, width, height) : url);
                            }
                        }
                    }

                    @Override
                    public void onImageItemClick(GlideImageView imageView, List<String> urlList, List<GlideImageView> imageList, int index) {
                        //T_.info(videoUrl);

                        if (!TextUtils.isEmpty(finalUrl)) {
                            if (isInDetail) {
                                if (MusicControl.isPlaying(finalUrl)) {
                                    if (isInDetail) {
                                        videoPlayView.setPlayType(HnVideoPlayView.PlayType.VOICE);
                                    } else {
                                        videoPlayView.setPlayType(HnVideoPlayView.PlayType.VOICE_HOME);
                                    }
                                    Audio.instance().pause();
                                } else {
                                    if (isInDetail) {
                                        videoPlayView.setPlayType(HnVideoPlayView.PlayType.VOICE_PAUSE);
                                    } else {
                                        videoPlayView.setPlayType(HnVideoPlayView.PlayType.VOICE_HOME_PAUSE);
                                    }
                                    Audio.instance().play(finalUrl);
                                }
                            } else {
                                if (!TextUtils.isEmpty(discuss_id)) {
                                    jumpToDynamicDetailUIView(iLayout, discuss_id, false, isInDetail, true);
                                }
                            }

                            if (!isInDetail) {
                                updateDiscussReadCnt(discuss_id);
                            }
                        }
                    }
                });
                mediaImageTypeView.setImage(thumbUrl);
            } else {
                mediaImageTypeView.setVisibility(View.GONE);
            }
        }
    }

    public static void jumpToDynamicDetailUIView(ILayout iLayout, String discuss_id,
                                                 boolean isForward, boolean isInDetail,
                                                 boolean autoPlayAudio) {
        jumpToDynamicDetailUIView(iLayout, discuss_id, isForward, isInDetail, autoPlayAudio, false);
    }

    public static void jumpToDynamicDetailUIView(ILayout iLayout, String discuss_id,
                                                 boolean isForward, boolean isInDetail,
                                                 boolean autoPlayAudio /*是否自动播放语音*/,
                                                 boolean autoShowInputDialog /*是否自动弹出输入对话框*/) {
        iLayout.startIView(new DynamicDetailUIView2(discuss_id)
                .setAutoPlayAudio(autoPlayAudio)
                .setAutoShowInputDialog(autoShowInputDialog));
        if (!isForward && !isInDetail) {
            updateDiscussReadCnt(discuss_id);
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
                            subscription.add(RRetrofit.create(UserService.class)
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
//                    subscription.add(RRetrofit.create(UserService.class)
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
                            subscription.add(RRetrofit.create(UserService.class)
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
//                    subscription.add(RRetrofit.create(UserService.class)
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
                                               final ILayout iLayout, final boolean isInDetail) {
        //是否置顶
        //showTopView(holder, tBean);//只在用户详情里面,显示置顶

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
                                        bindAttentionItemView2(subscription, holder, tBean, commandAction, iLayout, isInDetail);
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
                    subscription.add(TopControl.Companion.canTop().subscribe(new BaseSingleSubscriber<Boolean>() {
                        @Override
                        public void onSucceed(Boolean bean) {
                            super.onSucceed(bean);
                            if (bean) {
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
                                                    bindAttentionItemView2(subscription, holder, tBean, commandAction, iLayout, isInDetail);
                                                } catch (Exception e) {
                                                }
                                            }
                                        }));
                            } else {
                                UIDialog.build().setDialogTitle(ValleyApp.getApp().getResources().getString(R.string.tip))
                                        .setDialogContent(ValleyApp.getApp().getResources().getString(R.string.max_top_tip, TopControl.Companion.getTopBean().getTotal() + ""))
                                        .setCancelText("").setOkText(ValleyApp.getApp().getResources().getString(R.string.known))
                                        .setCanCanceledOnOutside(false)
                                        .showDialog(iLayout);
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
                                        bindAttentionItemView2(subscription, holder, tBean, commandAction, iLayout, isInDetail);
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
                                        bindAttentionItemView2(subscription, holder, tBean, commandAction, iLayout, isInDetail);
                                    } catch (Exception e) {

                                    }
                                }
                            }));

                    if (!isInDetail) {
                        updateDiscussReadCnt(tBean.getDiscuss_id());
                    }
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
                        subscription.add(RRetrofit.create(UserService.class)
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
                        subscription.add(RRetrofit.create(UserService.class)
                                .attention(Param.buildMap("to_uid:" + to_uid))
                                .compose(Rx.transformer(String.class))
                                .subscribe(new BaseSingleSubscriber<String>() {

                                    @Override
                                    public void onSucceed(String bean) {
                                        T_.show(ValleyApp.getApp().getString(R.string.handle_success));
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
                            .setShowCancelButton(false)
                            .addItem(topItem)
                            .addItem(favItem)
                            .addItem(deleteItem)
                    );
                }
            };
        } else {
            //其他人发布的动态
            listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iLayout.startIView(UIBottomItemDialog.build()
                            .setShowCancelButton(false)
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
                                    final CompositeSubscription subscription,
                                    final boolean isInDetail) {
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
                initUnFavView(itemTextView, tBean, subscription, isInDetail);

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
                                      final CompositeSubscription subscription,
                                      final boolean isInDetail) {
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
                initFavView(itemTextView, tBean, subscription, isInDetail);

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

                if (!isInDetail) {
                    updateDiscussReadCnt(tBean.getDiscuss_id());
                }
            }
        });
    }

    /**
     * 收藏和取消收藏
     */
    private static void bindFavItemView(final CompositeSubscription subscription,
                                        RBaseViewHolder holder,
                                        UserDiscussListBean.DataListBean tBean,
                                        boolean isInDetail) {

        HnItemTextView fav_cnt = holder.v(R.id.fav_cnt);
        fav_cnt.setVisibility(View.GONE);//星期五 2017-2-10 不显示

        if (tBean.getIs_collection() == 1) {
            //是否收藏
            initFavView(fav_cnt, tBean, subscription, isInDetail);
        } else {
            initUnFavView(fav_cnt, tBean, subscription, isInDetail);
        }
    }

    /**
     * 打赏按钮
     */
    private static void bindRewardItemView(final ILayout iLayout,
                                           RBaseViewHolder holder,
                                           final UserDiscussListBean.DataListBean tBean,
                                           final boolean isInDetail) {

        final HnItemTextView reward_cnt = holder.v(R.id.reward_cnt);
        reward_cnt.setText(tBean.getReward_cnt());

        //打赏成功之后, 需要数值加1. reward_cnt.setText(tBean.getReward_cnt());
        reward_cnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRewardDialog(iLayout, isInDetail,
                        tBean.getUser_info().getAvatar(),
                        tBean.getUser_info().getUsername(),
                        tBean.getUid(),
                        tBean.getDiscuss_id(),
                        new Runnable() {
                            @Override
                            public void run() {
                                reward_cnt.setText(Integer.valueOf(tBean.getReward_cnt()) + 1 + "");
                                tBean.setReward_cnt(Integer.valueOf(tBean.getReward_cnt()) + 1 + "");
                            }
                        });
            }
        });
    }

    /**
     * 显示打赏对话框, 在动态列表和动态详情 会被调用
     */
    public static void showRewardDialog(final ILayout iLayout,
                                        final boolean isInDetail, //在详情页面, 打赏不更新阅读数量
                                        final String avatar,
                                        final String username,
                                        final String uid,
                                        final String item_id,
                                        final Runnable onRewardSuccess /*打赏成功的回调*/) {
        final Runnable onSuccess = new Runnable() {
            @Override
            public void run() {
                //打赏成功, 更新阅读数
                if (!isInDetail) {
                    updateDiscussReadCnt(item_id);
                }
                if (onRewardSuccess != null) {
                    onRewardSuccess.run();
                }
            }
        };

        UIItemDialog.build()
                .setShowCancelButton(true)
                .setUseFullItem(true)
                .addItem("打赏红包", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iLayout.startIView(new RewardUIVIew(avatar, username, uid, item_id).setAction(new Action0() {
                            @Override
                            public void call() {
                                onSuccess.run();
                            }
                        }));
                    }
                })
                .addItem("打赏礼物", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iLayout.startIView(new GiftListUIView2(uid, SessionTypeEnum.P2P)
                                .setType(1)
                                .setDiscussid(item_id)
                                .setAction0(new Action0() {
                                    @Override
                                    public void call() {
                                        onSuccess.run();
                                    }
                                }));
                    }
                })
                .showDialog(iLayout);
    }

    /**
     * 点赞
     */
    private static void initLikeView(final View itemTextView,
                                     final ILikeData tBean,
                                     final CompositeSubscription subscription,
                                     final String type,
                                     final InitLikeViewCallback likeAction,
                                     final boolean isInDetail) {
        final String uid = UserCache.getUserAccount();

        if (itemTextView instanceof HnItemTextView) {
            ((HnItemTextView) itemTextView).setLeftIco(likeAction.getLikeIcoRes());
            ((HnItemTextView) itemTextView).setText(tBean.getLikeCount());
        } else if (itemTextView instanceof ImageView) {
            ((ImageView) itemTextView).setImageResource(likeAction.getLikeIcoRes());
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
                    likeAction.onLikeCall(false);
                }

                tBean.setIsLike(0);
                tBean.setLikeCount(String.valueOf(Integer.valueOf(tBean.getLikeCount()) - 1));
                initUnLikeView(itemTextView, tBean, subscription, type, likeAction, isInDetail);

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
                                       final InitLikeViewCallback likeAction,
                                       final boolean isInDetail) {
        final String uid = UserCache.getUserAccount();

        if (itemTextView instanceof HnItemTextView) {
            ((HnItemTextView) itemTextView).setLeftIco(likeAction.getUnLikeIcoRes());
            ((HnItemTextView) itemTextView).setText(tBean.getLikeCount());
        } else if (itemTextView instanceof ImageView) {
            ((ImageView) itemTextView).setImageResource(likeAction.getUnLikeIcoRes());
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
                    likeAction.onLikeCall(true);
                }

                GoodView.build(itemTextView);
                tBean.setIsLike(1);
                tBean.setLikeCount(String.valueOf(Integer.valueOf(tBean.getLikeCount()) + 1));
                initLikeView(itemTextView, tBean, subscription, type, likeAction, isInDetail);

                subscription.add(RRetrofit.create(SocialService.class)
                        .like(Param.buildMap("type:" + type, "item_id:" + tBean.getDiscussId(type)))
                        .compose(Rx.transformer(String.class))
                        .subscribe(new BaseSingleSubscriber<String>() {

                            @Override
                            public void onSucceed(String bean) {
                                //T_.show(bean);
                            }
                        }));

                if (!isInDetail) {
                    updateDiscussReadCnt(tBean.getDiscussId(type));
                }
            }
        });
    }

    /**
     * 点赞和取消点赞
     */
    public static void bindLikeItemView(final CompositeSubscription subscription,
                                        RBaseViewHolder holder,
                                        ILikeData tBean,
                                        InitLikeViewCallback likeAction,
                                        boolean isInDetail) {

        bindLikeItemView(subscription, holder, tBean, "discuss", likeAction, isInDetail);
    }

    public static void bindLikeItemView(final CompositeSubscription subscription,
                                        RBaseViewHolder holder,
                                        ILikeData tBean,
                                        String likeType,
                                        InitLikeViewCallback likeAction,
                                        boolean isInDetail) {

        View like_cnt = holder.v(R.id.like_cnt);

        if (likeAction == null) {
            likeAction = new InitLikeViewCallback() {
                @Override
                public void onLikeCall(boolean isLike) {
                    super.onLikeCall(isLike);
                }
            };
        }

        if (tBean.getIsLike() == 1) {
            //是否点赞
            initLikeView(like_cnt, tBean, subscription, likeType, likeAction, isInDetail);
        } else {
            initUnLikeView(like_cnt, tBean, subscription, likeType, likeAction, isInDetail);
        }
    }

    public static String getVideoTime(int time) {
        if (time <= 0) {
            return "";
        }

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
        return getVideoTime(getVideoTimeLong(url));
    }

    public static int getVideoTimeLong(String url) {
        float videoTime = -1f;
        try {
            videoTime = Float.parseFloat(url.substring(0, url.lastIndexOf('.')).split("t_")[1]);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return (int) videoTime;
    }

    public static void displayImage(final ImageView imageView, final String url,
                                    final int width, final int height, int imageSize) {
        displayImage(imageView, url, width, height, true, imageSize);
    }

    public static void displayImage(final ImageView imageView, final String url) {
        displayImage(imageView, url, -1, -1, true, Integer.MAX_VALUE);
    }

    /**
     * @param height
     * @param width     当宽高有一个小于0时, 则不会调用OSS 的 {@link OssHelper#getImageThumb(String, int, int)}方法
     * @param imageSize 如果size为1时, 会使用 {@link android.widget.ImageView.ScaleType#FIT_XY},
     *                  否则使用{@link android.widget.ImageView.ScaleType#CENTER_CROP}
     */
    public static void displayImage(final ImageView imageView, final String url,
                                    final int width, final int height,
                                    final boolean noGif, final int imageSize) {
//        ImagePicker.getInstance().getImageLoader().displayImage((Activity) imageView.getContext(),
//                "", "", OssHelper.getImageThumb(url, width, height), imageView, 0, 0);

        if (imageView instanceof RImageView) {
            ((RImageView) imageView).setShowGifTip(false);
        }

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
                        .placeholder(R.drawable.base_image_placeholder_shape)
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
                    public void onImageType(final String imageUrl, Ok.ImageType imageType) {
                        L.d("call: onImageType([imageType])-> " + imageUrl + " : " + imageType + " qw:" + width + " qh:" + height);

                        imageView.setImageResource(R.drawable.base_image_placeholder_shape);

                        if (!imageUrl.contains(String.valueOf(imageView.getTag(R.id.tag_url)))) {
                            return;
                        }

                        if (imageType != Ok.ImageType.UNKNOWN) {
                            if (!noGif && imageType == Ok.ImageType.GIF) {
                                Glide.with(imageView.getContext())
                                        .load(imageUrl)
                                        .asGif()
                                        .placeholder(R.drawable.base_image_placeholder_shape)
                                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                        .into(new SimpleTarget<GifDrawable>() {
                                            @Override
                                            public void onResourceReady(GifDrawable resource, GlideAnimation<? super GifDrawable> glideAnimation) {
                                                if (resource == null) {
                                                    return;
                                                }
                                                if (!imageUrl.contains(String.valueOf(imageView.getTag(R.id.tag_url)))) {
                                                    imageView.setImageResource(R.drawable.base_image_placeholder_shape);
                                                    return;
                                                }
                                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                                imageView.setImageDrawable(resource);
                                                resource.start();
                                            }
                                        });
                            } else {
                                if (!imageUrl.contains(String.valueOf(imageView.getTag(R.id.tag_url)))) {
                                    imageView.setImageResource(R.drawable.base_image_placeholder_shape);
                                    return;
                                }
                                if (imageView instanceof RImageView) {
                                    ((RImageView) imageView).setShowGifTip(imageType == Ok.ImageType.GIF);
                                }
                                displayJpeg(imageView, imageUrl, width, height, imageSize);
                            }
                        }
                    }

                    @Override
                    public void onLoadStart() {
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setImageResource(R.drawable.base_image_placeholder_shape);
                    }
                });
            }
        }
    }

    public static void displayJpeg(final ImageView imageView, final String url, final int width, final int height, final int imageSize) {
//        if (imageView instanceof RImageView) {
//            ((RImageView) imageView).setShowGifTip(false);
//        }

        if (imageSize == 1) {
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        Glide.with(ValleyApp.getApp())
                .load((width > 0 && height > 0) ? OssHelper.getImageThumb(url, width, height) : url)
                .asBitmap()
                .animate(R.anim.base_alpha_to_1)
                .placeholder(R.drawable.base_image_placeholder_shape)
                .error(R.drawable.base_image_placeholder_shape)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

//        if (imageSize == 1) {
//            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//            Glide.with(ValleyApp.getApp())
//                    .load((width > 0 && height > 0) ? OssHelper.getImageThumb(url, width, height) : url)
//                    .placeholder(R.drawable.zhanweitu_1)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(imageView);
//        } else {
//
//            Glide.with(ValleyApp.getApp())
//                    .load((width > 0 && height > 0) ? OssHelper.getImageThumb(url, width, height) : url)
//                    .asBitmap()
//                    .placeholder(R.drawable.zhanweitu_1)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                            if (imageView == null || resource == null) {
//                                return;
//                            }
//
//                            if (!url.contains(String.valueOf(imageView.getTag(R.id.tag_url)))) {
//                                return;
//                            }
//
//                            int w = resource.getWidth();
//                            int h = resource.getHeight();
//                            L.d("call: onResourceReady([resource, glideAnimation])-> " + url + " w:" +
//                                    w + " H:" + h);
//
//                            int abs = Math.abs(w - h);
//
//                            //自动根据图片的长宽差, 选择缩放类型
//                            if (width <= 0 && height <= 0) {
//                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                            } else if (imageSize <= 1 /*|| abs < Math.min(w / 4, h / 4)*/) {
//                                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//                            } else {
//                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                            }
//
//                            if (imageView instanceof RImageView && (width != -1 && height != -1 /**资讯加载图片不处理渐变动画*/)) {
//                                ((RImageView) imageView).setImageBitmapNoCrop(imageView.getDrawable(), resource);
//                            } else {
//                                imageView.setImageBitmap(resource);
        //                        }
//
//                        @Override
//                        public void onLoadStarted(Drawable placeholder) {
//                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                            imageView.setImageResource(R.drawable.zhanweitu_1);
//                        }
//                    });
//        }
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
                    .centerCrop()
                    .into(imageView);
        } else {
            DrawableRequestBuilder<String> builder = Glide.with(imageView.getContext())
                    .load(OssHelper.getImageThumb(url, width, height))
                    .placeholder(R.drawable.default_vociecover)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            if (blur) {
                builder.bitmapTransform(new GlideBlurTransformation(imageView.getContext()))
                        .into(imageView);
            } else {
                builder.into(imageView);
            }
        }
    }

    /**
     * 更新的动态的阅读数量
     */
    public static void updateDiscussReadCnt(final String item_id) {
        if (TextUtils.isEmpty(item_id)) {
            return;
        }
        if (updateReadCountList.contains(item_id)) {
            return;
        }
        updateReadCountList.add(item_id);
        RRetrofit.create(SocialService.class)
                .updateReadCnt(Param.buildMap("type:discuss", "item_id:" + item_id))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onSucceed(String bean) {
                        super.onSucceed(bean);
                        updateReadCountList.add(item_id);
                    }

                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        if (isError) {
                            updateReadCountList.remove(item_id);
                        }
                    }
                });
    }

    public static abstract class InitLikeViewCallback {

        /**
         * 点赞之后的回调
         *
         * @param isLike 是否属于点赞状态
         */
        public void onLikeCall(boolean isLike) {

        }


        /**
         * 点赞之后的图标
         */
        public int getLikeIcoRes() {
            return R.drawable.dianzan_s;
        }

        /**
         * 未点赞图标
         */
        public int getUnLikeIcoRes() {
            return R.drawable.dianzan_n;
        }
    }


}

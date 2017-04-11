package com.hn.d.valley.main.message;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.angcyo.library.facebook.DraweeViewUtil;
import com.angcyo.library.facebook.RFresco;
import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RBaseAdapter;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.utils.TimeUtil;
import com.angcyo.uiview.utils.media.BitmapDecoder;
import com.angcyo.uiview.utils.media.ImageUtil;
import com.angcyo.uiview.widget.RSoftInputLayout;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.iview.ImagePagerUIView;
import com.hn.d.valley.bean.realm.AmapBean;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.emoji.MoonUtil;
import com.hn.d.valley.helper.TeamNotificationHelper;
import com.hn.d.valley.main.me.UserDetailUIView2;
import com.hn.d.valley.main.message.attachment.CustomAttachment;
import com.hn.d.valley.main.message.attachment.CustomAttachmentType;
import com.hn.d.valley.main.message.attachment.PersonalCard;
import com.hn.d.valley.main.message.attachment.PersonalCardAttachment;
import com.hn.d.valley.main.message.audio.AudioPlayCallback;
import com.hn.d.valley.main.message.audio.MessageAudioControl;
import com.hn.d.valley.main.other.AmapUIView;
import com.hn.d.valley.widget.HnGlideImageView;
import com.lzy.imagepicker.bean.ImageItem;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.attachment.LocationAttachment;
import com.netease.nimlib.sdk.msg.attachment.VideoAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/01 16:23
 * 修改人员：hewking
 * 修改时间：2017/04/01 16:23
 * 修改备注：
 * Version: 1.0.0
 */
public class ChatAdapter extends RBaseAdapter<IMMessage> {

    public RBaseViewHolder mViewHolder;
    UIBaseView mUIBaseView;

    public ChatAdapter(Context context, List<IMMessage> datas,RBaseViewHolder viewHolder, UIBaseView uIBaseView) {
        super(context, datas);
        this.mViewHolder = viewHolder;
        this.mUIBaseView = uIBaseView;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_chat_msg_layout;
    }

    @Override
    public int getItemType(int position) {

        // 拉取
        autoRequestFetchMoreData(position);

        // 上拉加载
        autoRequestLoadMoreData(position);

        return super.getItemType(position);
    }

    @Override
    public void resetData(List<IMMessage> datas) {

        mAllDatas.clear();
        mAllDatas.addAll(datas);
        notifyDataSetChanged();

    }

    private void autoRequestFetchMoreData(int position) {



    }

    private void autoRequestLoadMoreData(int position) {


    }

    public void fetchMoreFailed(){

    }

    public void loadMoreFail() {

    }

    public void fetchMoreComplete(RecyclerView recyclerView ,List<IMMessage> messages) {

        addFrontData(messages);

        fetchMoreComplete(recyclerView,messages.size());

    }

    @Override
    public void onViewDetachedFromWindow(RBaseViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        final View view = holder.v(R.id.message_item_audio_playing_animation);
        selectDrawable(view);
    }

    private void selectDrawable(View view) {
        if (view != null) {
            if (view.getBackground() instanceof AnimationDrawable) {
                AnimationDrawable animation = (AnimationDrawable) view.getBackground();
                animation.stop();
                animation.selectDrawable(2);
            }
        }
    }

    public void addFrontData(List<IMMessage> data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        mAllDatas.addAll(0, data);
        notifyItemRangeInserted(0, data.size()); // add到FetchMoreView之下，保持FetchMoreView在顶部
    }

    public void fetchMoreComplete(final RecyclerView recyclerView, int newDataSize) {

        notifyItemChanged(0);

        // 定位到insert新消息前的top消息位置。必须移动，否则还在顶部，会继续fetch!!!
        if (recyclerView != null) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                //获取第一个可见view的位置
                int firstItemPosition = linearManager.findFirstVisibleItemPosition();
                if (firstItemPosition == 0) {
                    // 最顶部可见的View已经是FetchMoreView了，那么add数据&局部刷新后，要进行定位到上次的最顶部消息。
                    // newDataSize -1  为加载成功向下 偏移量
                    recyclerView.scrollToPosition(newDataSize - 1);
                }
            } else {
                recyclerView.scrollToPosition(newDataSize);
            }
        }
    }

    @Override
    public void onViewAttachedToWindow(RBaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        final View view = holder.v(R.id.message_item_audio_playing_animation);
        final int position = holder.getAdapterPosition();
        final IMMessage imMessage = getAllDatas().get(position);
        if (view != null && imMessage != null) {
            final boolean playing = AudioViewControl.isMessagePlaying(MessageAudioControl.getInstance(mContext), imMessage);
            if (playing && view.getBackground() instanceof AnimationDrawable) {
                AnimationDrawable animation = (AnimationDrawable) view.getBackground();
                animation.start();
            }
        }
    }

    @Override
    protected void onBindView(RBaseViewHolder holder, int position, final IMMessage bean) {
        NimUserInfoCache userInfoCache = NimUserInfoCache.getInstance();

        String avatar = "";
        View itemRootLayout = holder.v(R.id.item_root_layout);
        View contentRootLayout = holder.v(R.id.msg_content_layout);
        if (bean.getDirect() == MsgDirectionEnum.In) {
            //收到的消息
            itemRootLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            contentRootLayout.setBackgroundResource(R.drawable.bubble_box_left_selector);
            if (userInfoCache != null) {
                final NimUserInfo userInfo = userInfoCache.getUserInfo(bean.getFromAccount());
                if (userInfo != null) {
                    avatar = userInfo.getAvatar();
                }
            }
        } else {
            //发出去的消息
            itemRootLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            contentRootLayout.setBackgroundResource(R.drawable.bubble_box_right_selector);
            avatar = UserCache.instance().getAvatar();
        }

        itemRootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((RSoftInputLayout) mViewHolder.v(R.id.chat_root_layout)).requestBackPressed();
                return true;
            }
        });

        //头像
        DraweeViewUtil.setDraweeViewHttp((SimpleDraweeView) holder.v(R.id.msg_ico_view), avatar);

        //消息内容
        updateMsgContent(holder, bean, avatar);

        //时间
        TextView timeTextView = holder.tv(R.id.msg_time_view);
        String timeString = TimeUtil.getTimeShowString(bean.getTime(), false);
        timeTextView.setText(timeString);
        if (position == 0) {
            timeTextView.setVisibility(View.VISIBLE);
        } else {
            final IMMessage preMessage = mAllDatas.get(position - 1);
            timeTextView.setVisibility(needShowTime(preMessage.getTime(), bean.getTime()) ? View.VISIBLE : View.GONE);
        }

        //消息状态
        updateMsgStatus(holder, bean);

        //头像
        holder.v(R.id.msg_ico_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUIBaseView.startIView(new UserDetailUIView2(bean.getFromAccount()));
            }
        });
    }

    private boolean needShowTime(long oldTime, long nowTime) {
        return nowTime - oldTime > 60 * 1000;
    }

    private void updateMsgContent(RBaseViewHolder holder, final IMMessage bean, final String avatar) {
        final View msgContentLayout = holder.v(R.id.msg_content_layout);
        final View msgTextLayout = holder.v(R.id.msg_text_layout);
        final View msgImageLayout = holder.v(R.id.msg_image_layout);
        final View msgLocationLayout = holder.v(R.id.msg_location_layout);
        final View msgAudioLayout = holder.v(R.id.msg_audio_layout);
        final View msgPersonCardLayout = holder.v(R.id.msg_pc_layout);

        msgContentLayout.setVisibility(View.VISIBLE);

        msgImageLayout.setVisibility(View.GONE);
        msgLocationLayout.setVisibility(View.GONE);
        msgTextLayout.setVisibility(View.GONE);
        msgAudioLayout.setVisibility(View.GONE);
        msgPersonCardLayout.setVisibility(View.GONE);

            /*语音消息,未读提醒*/
        holder.v(R.id.message_item_audio_unread_indicator).setVisibility(View.GONE);

        final TextView contentView = holder.tv(R.id.msg_text_view);
        switch (bean.getMsgType()) {
            case audio:
                msgAudioLayout.setVisibility(View.VISIBLE);
                ImageView imageView = holder.v(R.id.message_item_audio_playing_animation);
                final TextView timeView = holder.tv(R.id.message_item_audio_duration);

                if (isReceivedMessage(bean)) {
                    msgAudioLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    imageView.setBackgroundResource(R.drawable.nim_audio_animation_list_left);
                    timeView.setTextColor(Color.BLACK);
                } else {
                    msgAudioLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    imageView.setBackgroundResource(R.drawable.nim_audio_animation_list_right);
                    timeView.setTextColor(Color.WHITE);
                }

                selectDrawable(imageView);

                final AudioViewControl audioViewControl = new AudioViewControl(mContext, holder.itemView, new AudioPlayCallback<IMMessage>() {
                    @Override
                    public List<IMMessage> getAllData() {
                        return ChatAdapter.this.getAllDatas();
                    }

                    @Override
                    public void notifyDataSetChanged() {
                        ChatAdapter.this.notifyDataSetChanged();
                    }
                }, bean);

                msgContentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        audioViewControl.onItemClick();
                    }
                });

                break;
            case avchat://音视频通话
                msgTextLayout.setVisibility(View.VISIBLE);
                contentView.setText("[音视频通话]");
                break;
            case custom://第三方APP自定义消息
                bindCustomMessage(holder, bean,msgContentLayout, msgPersonCardLayout);
                break;
            case file:
                msgTextLayout.setVisibility(View.VISIBLE);
                contentView.setText("[文件消息]");
                break;
            case image:
                bindImageLayout(holder, bean, msgContentLayout, msgImageLayout);

                break;
            case location:
                msgLocationLayout.setVisibility(View.VISIBLE);
                final LocationAttachment attachment = (LocationAttachment) bean.getAttachment();
                holder.tV(R.id.location_address_view).setText(attachment.getAddress());
                new LatLng(attachment.getLatitude(), attachment.getLongitude());
                final AmapBean amapBean = new AmapBean();
                amapBean.latitude = attachment.getLatitude();
                amapBean.longitude = attachment.getLongitude();
                amapBean.address = attachment.getAddress();
                msgContentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mUIBaseView.startIView(new AmapUIView(null, amapBean, avatar, false));
                    }
                });
                break;
            case notification:
                msgTextLayout.setVisibility(View.VISIBLE);

                String text = TeamNotificationHelper.getTeamNotificationText(bean, bean.getSessionId());

                contentView.setText(text);
                break;
            case text:
                msgTextLayout.setVisibility(View.VISIBLE);
                MoonUtil.show(mContext, contentView, bean.getContent());
                break;
            case tip:
                msgTextLayout.setVisibility(View.VISIBLE);
                contentView.setText(bean.getContent());
                break;
            case undef://未知消息类型
                msgTextLayout.setVisibility(View.VISIBLE);
                contentView.setText("[未知消息类型]");
                break;
            case video://视频消息
                msgTextLayout.setVisibility(View.VISIBLE);
                contentView.setText("[视频消息]");
                bindVideoLayout(holder, bean, msgContentLayout, msgImageLayout);

                break;

        }
    }

    private void bindVideoLayout(RBaseViewHolder holder, final IMMessage bean, View msgContentLayout, View msgImageLayout) {
        msgContentLayout.setVisibility(View.GONE);
        msgImageLayout.setVisibility(View.VISIBLE);
        final SimpleDraweeView draweeView = holder.v(R.id.msg_image_view);
        final View clickView = holder.v(R.id.click_view);

        VideoAttachment msgAttachment = (VideoAttachment) bean.getAttachment();
        final String path = msgAttachment.getPath();
        final String thumbPath = msgAttachment.getThumbPath();

        String fileUri = "";
        boolean isFile;
        if (TextUtils.isEmpty(thumbPath) && TextUtils.isEmpty(path)) {
            isFile = false;
            fileUri = msgAttachment.getUrl();
            downloadAttachment(bean);
        } else {
            isFile = true;
            if (!TextUtils.isEmpty(thumbPath)) {
                setImageSize(draweeView, clickView, bean, thumbPath);
                //DraweeViewUtil.setDraweeViewFile(draweeView, thumbPath);
                fileUri = thumbPath;
            } else if (!TextUtils.isEmpty(path)) {
                setImageSize(draweeView, clickView, bean, path);
                //DraweeViewUtil.setDraweeViewFile(draweeView, path);
                fileUri = path;
            } else {
                //DraweeViewUtil.setDraweeViewHttp(draweeView, msgAttachment.getUrl());
            }
        }

        String thumb = msgAttachment.getThumbPathForSave();
        String s = BitmapDecoder.extractThumbnail(msgAttachment.getPath(), thumb) ? thumb : null;

        if (isReceivedMessage(bean)) {
            clickView.setBackgroundResource(R.drawable.bubble_box_left_selector2);
            RFresco.mask(mContext, draweeView, R.drawable.bubble_box_left, thumb, isFile);
        } else {
            clickView.setBackgroundResource(R.drawable.bubble_box_right_selector2);
            RFresco.mask(mContext, draweeView, R.drawable.bubble_box_right_n2, thumb, isFile);
        }


    }

    private void bindImageLayout(RBaseViewHolder holder, final IMMessage bean, View msgContentLayout, View msgImageLayout) {
        msgContentLayout.setVisibility(View.GONE);
        msgImageLayout.setVisibility(View.VISIBLE);
        final SimpleDraweeView draweeView = holder.v(R.id.msg_image_view);
        final View clickView = holder.v(R.id.click_view);

        final FileAttachment msgAttachment = (FileAttachment) bean.getAttachment();
        final String path = msgAttachment.getPath();
        final String thumbPath = msgAttachment.getThumbPath();

        String fileUri = "";
        boolean isFile;
        if (TextUtils.isEmpty(thumbPath) && TextUtils.isEmpty(path)) {
            isFile = false;
            fileUri = msgAttachment.getUrl();
            downloadAttachment(bean);
        } else {
            isFile = true;
            if (!TextUtils.isEmpty(thumbPath)) {
                setImageSize(draweeView, clickView, bean, thumbPath);
                //DraweeViewUtil.setDraweeViewFile(draweeView, thumbPath);
                fileUri = thumbPath;
            } else if (!TextUtils.isEmpty(path)) {
                setImageSize(draweeView, clickView, bean, path);
                //DraweeViewUtil.setDraweeViewFile(draweeView, path);
                fileUri = path;
            } else {
                //DraweeViewUtil.setDraweeViewHttp(draweeView, msgAttachment.getUrl());
            }
        }

        if (isReceivedMessage(bean)) {
            clickView.setBackgroundResource(R.drawable.bubble_box_left_selector2);
            RFresco.mask(mContext, draweeView, R.drawable.bubble_box_left, fileUri, isFile);
        } else {
            clickView.setBackgroundResource(R.drawable.bubble_box_right_selector2);
            RFresco.mask(mContext, draweeView, R.drawable.bubble_box_right_n2, fileUri, isFile);
        }

        msgContentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ChatControl.Images images = getAllImageMessage(bean);
                ImagePagerUIView.start(mUIBaseView.getILayout(), v, images.images, images.positon);
            }
        });

        draweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ChatControl.Images images = getAllImageMessage(bean);
                ImagePagerUIView.start(mUIBaseView.getILayout(), v, images.images, images.positon);
            }
        });

        clickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ChatControl.Images images = getAllImageMessage(bean);
                ImagePagerUIView.start(mUIBaseView.getILayout(), v, images.images, images.positon);
            }
        });
    }

    private void bindCustomMessage(RBaseViewHolder holder, final IMMessage bean, View msgContentLayout, View msgPcLayout) {
        msgContentLayout.setVisibility(View.GONE);
        msgPcLayout.setVisibility(View.VISIBLE);

        HnGlideImageView imageView = holder.v(R.id.iv_item_head);
        TextView tv_pc_name = holder.v(R.id.tv_pc_name);
        TextView tv_pc_desc = holder.v(R.id.tv_pc_desc);

        tv_pc_name.setText("[第三方APP自定义消息]");

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) msgPcLayout.getLayoutParams();
        if (bean.getDirect() == MsgDirectionEnum.In) {
            params.setMarginStart(ScreenUtil.dip2px(10));
        } else {
            params.setMarginEnd(ScreenUtil.dip2px(10));
        }

        CustomAttachment attachment = (CustomAttachment) bean.getAttachment();
        if (attachment == null) {
            return;
        }
        switch (attachment.getType()) {

            case CustomAttachmentType.PersonalCard :
                PersonalCardAttachment pcAttachment = (PersonalCardAttachment) attachment;
                final PersonalCard from = PersonalCardAttachment.from(pcAttachment.toJson(true));
                tv_pc_name.setText(from.getUsername());
                imageView.setImageUrl(from.getAvatar());

                msgPcLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mUIBaseView.startIView(new UserDetailUIView2(from.getUid()));
                    }
                });

                break;
            default:
                break;

        }

    }

    private void updateMsgStatus(RBaseViewHolder viewHolder, final IMMessage bean) {
        final View failView = viewHolder.v(R.id.status_fail_view);
        final View sendingView = viewHolder.v(R.id.status_sending_view);

        failView.setVisibility(View.GONE);
        sendingView.setVisibility(View.GONE);
        MsgStatusEnum status = bean.getStatus();
        switch (status) {
            case draft:
                //草稿
                break;
            case fail:
                //失败
                failView.setVisibility(View.VISIBLE);
                failView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NIMClient.getService(MsgService.class).sendMessage(bean, true);
                        failView.setVisibility(View.GONE);
                        sendingView.setVisibility(View.VISIBLE);
                    }
                });
                break;
            case read:
                //已读
                break;
            case sending:
                //发送中
                sendingView.setVisibility(View.VISIBLE);
                break;
            case success:
                //成功
                break;
            case unread:
                //未读
                break;
        }

//            L.e("消息状态:" + status.getValue());
    }

    /**
     * 下载附件/缩略图
     */
    protected void downloadAttachment(IMMessage message) {
        if (message.getAttachment() != null && message.getAttachment() instanceof FileAttachment)
            NIMClient.getService(MsgService.class).downloadAttachment(message, true);
    }

    // 判断消息方向，是否是接收到的消息
    protected boolean isReceivedMessage(IMMessage message) {
        return message.getDirect() == MsgDirectionEnum.In;
    }

    private ChatControl.Images getAllImageMessage(IMMessage messageAnchor) {
        ChatControl.Images imagesBean = new ChatControl.Images();

        final List<IMMessage> allDatas = getAllDatas();
        ArrayList<ImageItem> images = new ArrayList<>();

        for (int i = 0; i < allDatas.size(); i++) {
            final IMMessage message = allDatas.get(i);
            if (message.getMsgType() == MsgTypeEnum.image) {
                FileAttachment msgAttachment = (FileAttachment) message.getAttachment();
                String path2 = msgAttachment.getPath();
                String thumbPath2 = msgAttachment.getThumbPath();

                if (messageAnchor.isTheSame(message)) {
                    imagesBean.positon = images.size();
                }

                final ImageItem imageItem = new ImageItem();
                imageItem.path = path2;
                imageItem.thumbPath = thumbPath2;
                imageItem.url = msgAttachment.getUrl();
                images.add(imageItem);
            }
        }

        imagesBean.images = images;
        return imagesBean;
    }

    /**
     * 根据图片路径大小, 自动设置View的宽高
     */
    public static void setImageSize(final View view, final IMMessage message, String path) {
        int[] bounds = null;
        if (path != null) {
            bounds = BitmapDecoder.decodeBound(new File(path));
        }
        if (bounds == null) {
            if (message.getMsgType() == MsgTypeEnum.image) {
                ImageAttachment attachment = (ImageAttachment) message.getAttachment();
                bounds = new int[]{attachment.getWidth(), attachment.getHeight()};
            } else if (message.getMsgType() == MsgTypeEnum.video) {
                VideoAttachment attachment = (VideoAttachment) message.getAttachment();
                bounds = new int[]{attachment.getWidth(), attachment.getHeight()};
            }
        }

        if (bounds != null) {
            ImageUtil.ImageSize imageSize = ImageUtil.getThumbnailDisplaySize(bounds[0], bounds[1]);

            ViewGroup.LayoutParams maskParams = view.getLayoutParams();
            maskParams.width = imageSize.width;
            maskParams.height = imageSize.height;
            view.setLayoutParams(maskParams);
        }
    }


    /**
     * 根据图片路径大小, 自动设置View的宽高
     */
    public static void setImageSize(final View view, final View clickView, final IMMessage message, String path) {
        int[] bounds = null;
        if (path != null) {
            bounds = BitmapDecoder.decodeBound(new File(path));
        }
        if (bounds == null) {
            if (message.getMsgType() == MsgTypeEnum.image) {
                ImageAttachment attachment = (ImageAttachment) message.getAttachment();
                bounds = new int[]{attachment.getWidth(), attachment.getHeight()};
            } else if (message.getMsgType() == MsgTypeEnum.video) {
                VideoAttachment attachment = (VideoAttachment) message.getAttachment();
                bounds = new int[]{attachment.getWidth(), attachment.getHeight()};
            }
        }

        if (bounds != null) {
            ImageUtil.ImageSize imageSize = ImageUtil.getThumbnailDisplaySize(bounds[0], bounds[1]);

            ViewGroup.LayoutParams maskParams = view.getLayoutParams();
            maskParams.width = imageSize.width;
            maskParams.height = imageSize.height;
            view.setLayoutParams(maskParams);

            ViewGroup.LayoutParams maskParams2 = clickView.getLayoutParams();
            maskParams2.width = imageSize.width;
            maskParams2.height = imageSize.height;
            clickView.setLayoutParams(maskParams2);
        }
    }


}

package com.hn.d.valley.main.message.chat;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.library.fresco.DraweeViewUtil;
import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.TimeUtil;
import com.angcyo.uiview.widget.RSoftInputLayout;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.cache.TeamDataCache;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.me.SkinManagerUIView;
import com.hn.d.valley.main.message.session.SessionHelper;
import com.hn.d.valley.skin.SkinUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/07 16:25
 * 修改人员：hewking
 * 修改时间：2017/04/07 16:25
 * 修改备注：
 * Version: 1.0.0
 */
public abstract class MsgViewHolderBase<T extends BaseMultiAdapter<V>, V extends RBaseViewHolder> {

    protected TextView msgTimeView;
    protected SimpleDraweeView msgIcoView;
    protected FrameLayout messageItemAudioUnreadIndicator;
    protected ImageView statusFailView;
    protected ProgressBar statusSendingView;
    protected LinearLayout itemRootLayout;
    protected RelativeLayout itemMsgChatRootLayout;
    protected FrameLayout contentContainer;
    protected LinearLayout msgNameLayout;

    protected TextView nameTextView;

    protected RBaseViewHolder mViewHolder;
    protected UIBaseView mUIBaseView;

    protected View.OnLongClickListener longClickListener;

    public RBaseViewHolder getViewHolder() {
        return mViewHolder;
    }

    public void setViewHolder(RBaseViewHolder mViewHolder) {
        this.mViewHolder = mViewHolder;
    }

    public UIBaseView getUIBaseView() {
        return mUIBaseView;
    }

    public void setUIBaseView(UIBaseView mUIBaseView) {
        this.mUIBaseView = mUIBaseView;
    }

    // basic
    protected View view;
    protected Context context;
    protected int position;

    // data
    protected IMMessage message;

    final private T adapter;

    public MsgViewHolderBase(T adapter) {
        this.adapter = adapter;
    }

    public T getAdapter() {
        return adapter;
    }

    protected void convert(V holder, IMMessage data, int position, boolean isScrolling) {
        view = holder.itemView;
        context = holder.getContext();
        message = data;
        this.position = position;

        inflate();
        refresh(holder);
    }

    private void inflate() {

        msgTimeView = findViewById(R.id.msg_time_view);
        msgIcoView = findViewById(R.id.msg_ico_view);
        nameTextView = findViewById(R.id.msg_nickname);
        messageItemAudioUnreadIndicator = findViewById(R.id.message_item_audio_unread_indicator);
        statusFailView = findViewById(R.id.status_fail_view);
        statusSendingView = findViewById(R.id.status_sending_view);
        itemRootLayout = findViewById(R.id.item_root_layout);
        itemMsgChatRootLayout = findViewById(R.id.item_msg_chat_root_layout);
        contentContainer = findViewById(R.id.msg_content_layout);
        msgNameLayout = findViewById(R.id.msg_name_layout);

        if (isMiddleItem()) {
            itemRootLayout.removeAllViews();
            itemRootLayout.setGravity(Gravity.CENTER);
            if (itemRootLayout.getChildCount() == 0) {
                if (getContentResId() == 0) {
                    return;
                }
                View.inflate(view.getContext(), getContentResId(), itemRootLayout);
            }
            inflateContentView();
            return;
        }

        // 这里只要inflate出来后加入一次即可
        if (contentContainer.getChildCount() == 0) {
            if (getContentResId() == 0) {
                return;
            }
            View.inflate(view.getContext(), getContentResId(), contentContainer);
        }

        inflateContentView();

    }

    private void refresh(V holder) {

        //时间
        String timeString = TimeUtil.getTimeShowString(message.getTime(), false);
        msgTimeView.setText(timeString);
        if (position == 0) {
            msgTimeView.setVisibility(View.VISIBLE);
        } else {
            final IMMessage preMessage = getMsgAdapter().getAllDatas().get(position - 1);
            msgTimeView.setVisibility(isMiddleItem() || needShowTime(preMessage.getTime(), message.getTime()) ? View.VISIBLE : View.GONE);
        }

        if (isMiddleItem()) {
            bindContentView();
            return;
        }

        setLongClickListener();

        if (!isShowBubble() && !isMiddleItem()) {
            return;
        }

        NimUserInfoCache userInfoCache = NimUserInfoCache.getInstance();
        String avatar = "";

        if (message.getSessionType() == SessionTypeEnum.Team && isReceivedMessage() && !isMiddleItem()) {
            nameTextView.setVisibility(View.VISIBLE);
            nameTextView.setText(TeamDataCache.getInstance().getTeamMemberDisplayName(message.getSessionId(), message
                    .getFromAccount()));
        } else {
            nameTextView.setVisibility(View.GONE);
        }

        if (isReceivedMessage()) {
            //收到的消息
            itemRootLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

            contentContainer.setBackgroundResource(R.drawable.bubble_box_left_selector);

            if (userInfoCache != null) {
                NimUserInfo userInfo = userInfoCache.getUserInfo(message.getFromAccount());
                if (userInfo != null) {
                    avatar = userInfo.getAvatar();
                }
            }
        } else {
            //发出去的消息
            itemRootLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            switch (SkinUtils.getSkin()) {
                case SkinManagerUIView.SKIN_BLACK:
                    contentContainer.setBackgroundResource(R.drawable.bubble_box_right_black_selector);
                    break;
                case SkinManagerUIView.SKIN_GREEN:
                    contentContainer.setBackgroundResource(R.drawable.bubble_box_right_green_selector);
                    break;
                case SkinManagerUIView.SKIN_BLUE:
                    contentContainer.setBackgroundResource(R.drawable.bubble_box_right_blue_selector);
                    break;
            }
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
        DraweeViewUtil.setDraweeViewHttp((SimpleDraweeView) msgIcoView, avatar);

        //消息状态
        updateMsgStatus(holder, message);

        //头像
        msgIcoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionHelper.getSessionListener().onAvatarClicked(mUIBaseView, message);
            }
        });

        msgIcoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (getMsgAdapter().getHeadAitListener() != null) {
                    getMsgAdapter().getHeadAitListener().onGroupHeadAit(message);
                }
//                SessionHelper.getSessionListener().onAvatarLongClicked(mUIBaseView, message);
                return true;
            }
        });
        bindContentView();
    }

    protected void setLongClickListener() {
        contentContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!onItemLongClick()) {
                    if (getMsgAdapter().getEventListener() != null) {
                        getMsgAdapter().getEventListener().onViewHolderLongClick(contentContainer, view, message);
                        return true;
                    }
                }
                return false;
            }
        });

    }

    protected boolean onItemLongClick() {
        return false;
    }

//    private void setReadReceipt() {
//        if (!TextUtils.isEmpty(getMsgAdapter().getUuid()) && message.getUuid().equals(getMsgAdapter().getUuid())) {
//            readReceiptTextView.setVisibility(View.VISIBLE);
//        } else {
//            readReceiptTextView.setVisibility(View.GONE);
//        }
//    }

    // 设置FrameLayout子控件的gravity参数
    protected final void setGravity(View view, int gravity) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.gravity = gravity;
    }

    // 判断消息方向，是否是接收到的消息
    protected boolean isReceivedMessage() {
        return message.getDirect() == MsgDirectionEnum.In;
    }

    // 返回该消息是不是居中显示
    protected boolean isMiddleItem() {
        return false;
    }

    // 是否显示头像，默认为显示
    protected boolean isShowHeadImage() {
        return true;
    }

    // 是否显示气泡背景，默认为显示
    protected boolean isShowBubble() {
        return true;
    }

    protected final ChatAdapter2 getMsgAdapter() {
        return (ChatAdapter2) adapter;
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

    protected boolean needShowTime(long oldTime, long nowTime) {
        return nowTime - oldTime > 60 * 1000;
    }

    /**
     * 下载附件/缩略图
     */
    protected void downloadAttachment(IMMessage message) {
        downloadAttachment(message, true);
    }

    protected void downloadAttachment(IMMessage message, boolean thumb) {
        if (message.getAttachment() != null && message.getAttachment() instanceof FileAttachment) {
            NIMClient.getService(MsgService.class).downloadAttachment(message, thumb);
        }
    }

    // 判断消息方向，是否是接收到的消息
    protected boolean isReceivedMessage(IMMessage message) {
        return message.getDirect() == MsgDirectionEnum.In;
    }


    // 根据layout id查找对应的控件
    protected <T extends View> T findViewById(int id) {
        return (T) view.findViewById(id);
    }

    /// -- 以下接口可由子类覆盖或实现
    // 返回具体消息类型内容展示区域的layout res id
    abstract protected int getContentResId();

    // 在该接口中根据layout对各控件成员变量赋值
    abstract protected void inflateContentView();

    // 将消息数据项与内容的view进行绑定
    abstract protected void bindContentView();


}

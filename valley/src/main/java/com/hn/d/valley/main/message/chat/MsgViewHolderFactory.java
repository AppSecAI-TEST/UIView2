package com.hn.d.valley.main.message.chat;

import com.hn.d.valley.main.message.attachment.CustomExpressionAttachment;
import com.hn.d.valley.main.message.attachment.GrabedMsgAttachment;
import com.hn.d.valley.main.message.attachment.PersonalCardAttachment;
import com.hn.d.valley.main.message.attachment.ReceiptsNoticeAttachment;
import com.hn.d.valley.main.message.attachment.RedPacketAttachment;
import com.hn.d.valley.main.message.attachment.RefundMsgAttachment;
import com.hn.d.valley.main.message.attachment.SystemPushAttachment;
import com.hn.d.valley.main.message.attachment.WithDrawalAttachment;
import com.hn.d.valley.main.message.chat.viewholder.MsgVHExpression;
import com.hn.d.valley.main.message.chat.viewholder.MsgVHReceiptsNoticeMsg;
import com.hn.d.valley.main.message.chat.viewholder.MsgVHRefundMsg;
import com.hn.d.valley.main.message.chat.viewholder.MsgVHWithDrawalMsg;
import com.hn.d.valley.main.message.chat.viewholder.MsgViewHolderAudio;
import com.hn.d.valley.main.message.chat.viewholder.MsgVHGIF;
import com.hn.d.valley.main.message.chat.viewholder.MsgViewHolderGrabedMsg;
import com.hn.d.valley.main.message.chat.viewholder.MsgViewHolderLocation;
import com.hn.d.valley.main.message.chat.viewholder.MsgViewHolderNotification;
import com.hn.d.valley.main.message.chat.viewholder.MsgViewHolderPersonCard;
import com.hn.d.valley.main.message.chat.viewholder.MsgViewHolderPicture;
import com.hn.d.valley.main.message.chat.viewholder.MsgViewHolderPushMultiPicture;
import com.hn.d.valley.main.message.chat.viewholder.MsgViewHolderPushPictureText;
import com.hn.d.valley.main.message.chat.viewholder.MsgViewHolderPushText;
import com.hn.d.valley.main.message.chat.viewholder.MsgViewHolderRedPacket;
import com.hn.d.valley.main.message.chat.viewholder.MsgViewHolderText;
import com.hn.d.valley.main.message.chat.viewholder.MsgViewHolderTip;
import com.hn.d.valley.main.message.chat.viewholder.MsgViewHolderVideo;
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.attachment.LocationAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.NotificationAttachment;
import com.netease.nimlib.sdk.msg.attachment.VideoAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hewking on 2017/4/8.
 */

public class MsgViewHolderFactory {

    private static HashMap<Class<? extends MsgAttachment>, Class<? extends MsgViewHolderBase>> viewHolders = new HashMap<>();

    private static Class<? extends MsgViewHolderBase> tipMsgViewHolder;

    static {
        register(ImageAttachment.class, MsgViewHolderPicture.class);
        register(AudioAttachment.class, MsgViewHolderAudio.class);
        register(VideoAttachment.class, MsgViewHolderVideo.class);
        register(FileAttachment.class,MsgVHGIF.class);
        register(CustomExpressionAttachment.class, MsgVHExpression.class);
        register(LocationAttachment.class, MsgViewHolderLocation.class);
        register(NotificationAttachment.class, MsgViewHolderNotification.class);
        register(PersonalCardAttachment.class, MsgViewHolderPersonCard.class);
        register(RedPacketAttachment.class, MsgViewHolderRedPacket.class);
        register(GrabedMsgAttachment.class, MsgViewHolderGrabedMsg.class);
        register(ReceiptsNoticeAttachment.class,MsgVHReceiptsNoticeMsg.class);
        register(RefundMsgAttachment.class, MsgVHRefundMsg.class);
        register(WithDrawalAttachment.class, MsgVHWithDrawalMsg.class);
        register(SystemPushAttachment.SingleTextSPAttachment.class, MsgViewHolderPushText.class);
        register(SystemPushAttachment.TextAndPictureSPAttachment.class, MsgViewHolderPushPictureText.class);
        register(SystemPushAttachment.MultiPictureSPAttachment.class, MsgViewHolderPushMultiPicture.class);
    }

    public static void register(Class<? extends MsgAttachment> attach, Class<? extends MsgViewHolderBase> viewHolder) {
        viewHolders.put(attach, viewHolder);
    }

    public static void registerTipMsgViewHolder(Class<? extends MsgViewHolderBase> viewHolder) {
        tipMsgViewHolder = viewHolder;
    }

    public static List<Class<? extends MsgViewHolderBase>> getAllViewHolder() {
        List<Class<? extends MsgViewHolderBase>> list = new ArrayList<>();
        list.addAll(viewHolders.values());
//        if (tipMsgViewHolder != null) {
//            list.add(tipMsgViewHolder);
//        }
//        list.add(MsgViewHolderUnknown.class);
        list.add(MsgViewHolderText.class);
        list.add(MsgViewHolderTip.class);
        return list;
    }

    public static Class<? extends MsgViewHolderBase> getViewHolderByType(IMMessage message) {
        if (message.getMsgType() == MsgTypeEnum.text) {
            return MsgViewHolderText.class;
        } else if (message.getMsgType() == MsgTypeEnum.tip) {
//            return tipMsgViewHolder == null ? MsgViewHolderUnknown.class : tipMsgViewHolder;
            return MsgViewHolderTip.class;
        } else {
            Class<? extends MsgViewHolderBase> viewHolder = null;
            if (message.getAttachment() != null) {
                Class<? extends MsgAttachment> clazz = message.getAttachment().getClass();
                while (viewHolder == null && clazz != null) {
                    viewHolder = viewHolders.get(clazz);
                    if (viewHolder == null) {
                        clazz = getSuperClass(clazz);
                    }
                }
            }
//            return viewHolder == null ? MsgViewHolderUnknown.class : viewHolder;
            return viewHolder == null ? MsgViewHolderText.class : viewHolder;
        }
    }

    private static Class<? extends MsgAttachment> getSuperClass(Class<? extends MsgAttachment> derived) {
        Class sup = derived.getSuperclass();
        if (sup != null && MsgAttachment.class.isAssignableFrom(sup)) {
            return sup;
        } else {
            for (Class itf : derived.getInterfaces()) {
                if (MsgAttachment.class.isAssignableFrom(itf)) {
                    return itf;
                }
            }
        }
        return null;
    }

}

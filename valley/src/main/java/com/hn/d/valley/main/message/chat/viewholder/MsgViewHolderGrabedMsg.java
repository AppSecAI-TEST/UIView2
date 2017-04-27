package com.hn.d.valley.main.message.chat.viewholder;

import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.attachment.CustomAttachment;
import com.hn.d.valley.main.message.attachment.GrabedMsgAttachment;
import com.hn.d.valley.main.message.attachment.GrabredPacket;
import com.hn.d.valley.main.message.attachment.RedPacketGrabedMsg;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgViewHolderGrabedMsg extends MsgViewHolderBase {

    public MsgViewHolderGrabedMsg(BaseMultiAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return 0;
    }

    @Override
    protected void inflateContentView() {

    }

    @Override
    protected void bindContentView() {
        CustomAttachment attachment = (CustomAttachment) message.getAttachment();
        if (attachment == null) {
            return;
        }

        GrabedMsgAttachment pcAttachment = (GrabedMsgAttachment) attachment;
        RedPacketGrabedMsg redPacket = pcAttachment.getGrabedMsg();

        NimUserInfoCache userInfoCache = NimUserInfoCache.getInstance();
        if (UserCache.getUserAccount().equals(redPacket.getOwner() + "")) {
            msgTimeView.setText(String.format("%s 领取了你的红包",userInfoCache.getUserDisplayName(redPacket.getGraber() + "")));
            return;
        }
        msgTimeView.setText(String.format("你已经领取了 %s 的红包",userInfoCache.getUserDisplayName(redPacket.getOwner() + "")));

    }

    @Override
    protected boolean isMiddleItem() {
        return true;
    }

    @Override
    protected boolean isShowBubble() {
        return false;
    }
}

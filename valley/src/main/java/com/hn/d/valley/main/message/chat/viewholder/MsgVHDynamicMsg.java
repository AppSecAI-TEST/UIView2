package com.hn.d.valley.main.message.chat.viewholder;

import android.widget.TextView;

import com.hn.d.valley.R;
import com.hn.d.valley.main.message.attachment.CustomAttachment;
import com.hn.d.valley.main.message.attachment.LikeMsg;
import com.hn.d.valley.main.message.attachment.LikeMsgAttachment;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgVHDynamicMsg extends MsgViewHolderBase {

    public MsgVHDynamicMsg(BaseMultiAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.item_redpacket_grabedmsg;
    }

    @Override
    protected void inflateContentView() {

    }

    @Override
    protected void bindContentView() {

        TextView tv_hongbao_notice = (TextView) findViewById(R.id.tv_hongbao_notice);

        CustomAttachment attachment = (CustomAttachment) message.getAttachment();
        if (attachment == null) {
            return;
        }

        LikeMsgAttachment likeMsgAttachment = (LikeMsgAttachment) attachment;
        LikeMsg likeMsg = likeMsgAttachment.getLikeMsg();

        tv_hongbao_notice.setText(likeMsg.getMsg());

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

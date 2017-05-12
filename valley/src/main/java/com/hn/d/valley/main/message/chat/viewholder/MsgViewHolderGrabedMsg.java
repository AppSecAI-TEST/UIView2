package com.hn.d.valley.main.message.chat.viewholder;

import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.angcyo.uiview.github.utilcode.utils.SpannableStringUtils;
import com.hn.d.valley.R;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.attachment.CustomAttachment;
import com.hn.d.valley.main.message.attachment.GrabedMsgAttachment;
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

        GrabedMsgAttachment pcAttachment = (GrabedMsgAttachment) attachment;
        RedPacketGrabedMsg redPacket = pcAttachment.getGrabedMsg();

        NimUserInfoCache userInfoCache = NimUserInfoCache.getInstance();
        if (redPacket.getOwner() == (redPacket.getGraber())) {
            //自己抢到了红包
            tv_hongbao_notice.setText(SpannableStringUtils.getBuilder(String.format(context.getString(R.string.text_get_your),userInfoCache.getUserDisplayName(redPacket.getGraber() + "")))
                    .append(context.getString(R.string.text_redpacket))
                    .setForegroundColor(ContextCompat.getColor(context,R.color.base_red))
                    .create());
            return;
        } else if (UserCache.getUserAccount().equals(redPacket.getOwner() + "")) {
            tv_hongbao_notice.setText(SpannableStringUtils.getBuilder(String.format(context.getString(R.string.text_get_your),userInfoCache.getUserDisplayName(redPacket.getGraber() + "")))
                    .append(context.getString(R.string.text_redpacket))
                    .setForegroundColor(ContextCompat.getColor(context,R.color.base_red))
                    .create());
            return;
        }
        tv_hongbao_notice.setText(SpannableStringUtils.getBuilder(String.format(context.getString(R.string.text_you_already_get),userInfoCache.getUserDisplayName(redPacket.getOwner() + "")))
                .append(context.getString(R.string.text_redpacket)).setForegroundColor(ContextCompat.getColor(context,R.color.base_red))
                .create());

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

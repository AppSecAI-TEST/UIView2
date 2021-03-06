package com.hn.d.valley.main.message.chat.viewholder;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.angcyo.github.utilcode.utils.SpannableStringUtils;
import com.hn.d.valley.R;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.attachment.CustomAttachment;
import com.hn.d.valley.main.message.attachment.GrabedMsgAttachment;
import com.hn.d.valley.main.message.attachment.RedPacketGrabedMsg;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.main.message.redpacket.GrabedRDResultUIView;
import com.netease.nimlib.sdk.msg.model.IMMessage;

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

        // 与前一条消息一分钟内 不显示 时间
        IMMessage preMessage = getMsgAdapter().getAllDatas().get(position - 1);
        if (!needShowTime(preMessage.getTime(), message.getTime())) {
            msgTimeView.setVisibility(View.GONE);
        }

        GrabedMsgAttachment pcAttachment = (GrabedMsgAttachment) attachment;
        final RedPacketGrabedMsg redPacket = pcAttachment.getGrabedMsg();

        tv_hongbao_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUIBaseView.startIView(new GrabedRDResultUIView(redPacket.getRid()));
            }
        });

        NimUserInfoCache userInfoCache = NimUserInfoCache.getInstance();
        if (redPacket.getOwner() == (redPacket.getGraber()) && UserCache.getUserAccount().equals(redPacket.getOwner() + "")) {
            //自己抢到了自己红包
            tv_hongbao_notice.setText(SpannableStringUtils.getBuilder(context.getString(R.string.text_get_redpacket_myself))
                    .append(context.getString(R.string.text_redpacket))
                    .setForegroundColor(ContextCompat.getColor(context, R.color.base_red_d85940))
                    .create());
            return;
        } else if (UserCache.getUserAccount().equals(redPacket.getOwner() + "")) {
            tv_hongbao_notice.setText(SpannableStringUtils.getBuilder(String.format(context.getString(R.string.text_get_your), userInfoCache.getUserDisplayName(redPacket.getGraber() + "")))
                    .append(context.getString(R.string.text_redpacket))
                    .setForegroundColor(ContextCompat.getColor(context, R.color.base_red_d85940))
                    .create());
            return;
        } else if (UserCache.getUserAccount().equals(redPacket.getGraber() + "")) {
            tv_hongbao_notice.setText(SpannableStringUtils.getBuilder(String.format(context.getString(R.string.text_you_already_get), userInfoCache.getUserDisplayName(redPacket.getOwner() + "")))
                    .append(context.getString(R.string.text_redpacket)).setForegroundColor(ContextCompat.getColor(context, R.color.base_red_d85940))
                    .create());
        } else {
            tv_hongbao_notice.setText(SpannableStringUtils.getBuilder(String.format(context.getString(R.string.text_two_user_already_get),userInfoCache.getUserDisplayName(redPacket.getGraber() + ""),
                    userInfoCache.getUserDisplayName(redPacket.getOwner() + "")))
                    .append(context.getString(R.string.text_redpacket)).setForegroundColor(ContextCompat.getColor(context, R.color.base_red_d85940))
                    .create());
        }

    }

    @Override
    protected boolean needShowTime(long oldTime, long nowTime) {

        return super.needShowTime(oldTime, nowTime);
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

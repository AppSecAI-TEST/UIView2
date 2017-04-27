package com.hn.d.valley.main.message.chat.viewholder;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.utils.media.ImageUtil;
import com.hn.d.valley.R;
import com.hn.d.valley.base.iview.ImagePagerUIView;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.me.UserDetailUIView2;
import com.hn.d.valley.main.message.attachment.CustomAttachment;
import com.hn.d.valley.main.message.attachment.CustomAttachmentType;
import com.hn.d.valley.main.message.attachment.PersonalCard;
import com.hn.d.valley.main.message.attachment.PersonalCardAttachment;
import com.hn.d.valley.main.message.attachment.RedPacket;
import com.hn.d.valley.main.message.attachment.RedPacketAttachment;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.ChatControl2;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.main.message.redpacket.OpenRedPacketUIDialog;
import com.hn.d.valley.widget.MsgThumbImageView;
import com.lzy.imagepicker.bean.ImageItem;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;
import java.util.List;

import static com.hn.d.valley.main.message.ChatAdapter.setImageSize;

/**
 * Created by hewking on 2017/4/25.
 */

public class MsgViewHolderRedPacket extends MsgViewHolderBase {

    public MsgViewHolderRedPacket(BaseMultiAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.msg_redpacket_layout;
    }

    @Override
    protected void inflateContentView() {

    }

    @Override
    protected void bindContentView() {

        TextView tv_content = (TextView) findViewById(R.id.tv_red_content);
        RelativeLayout rl_container = (RelativeLayout) findViewById(R.id.rl_red_packet);

        CustomAttachment attachment = (CustomAttachment) message.getAttachment();
        if (attachment == null) {
            return;
        }
        switch (attachment.getType()) {

            case CustomAttachmentType.REDPACKET:
                RedPacketAttachment pcAttachment = (RedPacketAttachment) attachment;
                final RedPacket redPacket = pcAttachment.getRedPacket();

                tv_content.setText(redPacket.getMsg());

                if (message.getFromAccount().equals(UserCache.getUserAccount())) {
                    return;
                }

                rl_container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mUIBaseView.startIView(new OpenRedPacketUIDialog(redPacket.getRid()));
                    }
                });

                break;
            default:
                break;

        }

    }

}

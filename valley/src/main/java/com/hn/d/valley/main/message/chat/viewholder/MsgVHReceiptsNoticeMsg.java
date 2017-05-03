package com.hn.d.valley.main.message.chat.viewholder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hn.d.valley.R;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.attachment.CustomAttachment;
import com.hn.d.valley.main.message.attachment.GrabedMsgAttachment;
import com.hn.d.valley.main.message.attachment.ReceiptsNotice;
import com.hn.d.valley.main.message.attachment.ReceiptsNoticeAttachment;
import com.hn.d.valley.main.message.attachment.RedPacketGrabedMsg;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.main.wallet.BillUIView;
import com.hn.d.valley.widget.HnGlideImageView;

import java.util.Locale;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgVHReceiptsNoticeMsg extends MsgViewHolderBase {

    public MsgVHReceiptsNoticeMsg(BaseMultiAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.msg_personalcard_layout;
    }

    @Override
    protected void inflateContentView() {

    }

    @Override
    protected void bindContentView() {

        HnGlideImageView imageView = (HnGlideImageView) findViewById(R.id.iv_item_head);
        TextView tv_pc_name = (TextView) findViewById(R.id.tv_pc_name);
        TextView msgPcLayout = (TextView) findViewById(R.id.tv_pc_desc);
        LinearLayout pc_layout = (LinearLayout) findViewById(R.id.msg_card_layout);

        ReceiptsNoticeAttachment attachment = (ReceiptsNoticeAttachment) message.getAttachment();
        if (attachment == null) {
            return;
        }

        ReceiptsNoticeAttachment pcAttachment = attachment;
        ReceiptsNotice redPacket = pcAttachment.getReceiptsNotice();

        imageView.setImageResource(R.drawable.hongbao_xiao_konglongjun);
        NimUserInfoCache userInfoCache = NimUserInfoCache.getInstance();

        tv_pc_name.setText(String.format(Locale.CHINA,"发送红包 $ %f 元给  %s ",redPacket.getMoney() / 100f
                ,userInfoCache.getUserDisplayNameEx(redPacket.getTo_uid() + "")));

        contentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUIBaseView.startIView(new BillUIView());
            }
        });

    }

}

package com.hn.d.valley.main.message.chat.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hn.d.valley.R;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.main.me.SkinManagerUIView;
import com.hn.d.valley.main.message.attachment.CustomAttachment;
import com.hn.d.valley.main.message.attachment.GiftReceiveAttachment;
import com.hn.d.valley.main.message.attachment.GiftReceiveMsg;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.main.message.gift.SendGiftUIDialog;
import com.hn.d.valley.skin.SkinUtils;
import com.hn.d.valley.widget.HnGlideImageView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.util.Map;

import rx.functions.Action0;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgVHGiftReceive extends MsgViewHolderBase {

    private TextView tv_text;
    private HnGlideImageView iv_thumb;

    public MsgVHGiftReceive(BaseMultiAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.item_msg_gift_layout;
    }

    @Override
    protected void inflateContentView() {
        tv_text = (TextView) findViewById(R.id.iv_icon);
        iv_thumb = (HnGlideImageView) findViewById(R.id.iv_gift_thumb);
    }

    @Override
    protected void bindContentView() {
        GiftReceiveAttachment attachment = (GiftReceiveAttachment) message.getAttachment();
        if (attachment == null) {
            return;
        }
        final GiftReceiveMsg msg = attachment.getGiftReceiveMsg();
        message.getFromAccount();
        if (message.getSessionType() == SessionTypeEnum.P2P) {
            tv_text.setText(String.format("送你一个 %s", msg.getGift_info().getName()));
        } else if (message.getSessionType() == SessionTypeEnum.Team) {
            tv_text.setText(String.format("%s 送 %s 一个 %s", message.getFromAccount(),msg.getTo_uid(),msg.getGift_info().getName()));
        }
        boolean read = message.isRemoteRead();
        if (!read) {
//            getUIBaseView().startIView(new SendGiftUIDialog(msg.getGift_info(), new Action0() {
//                @Override
//                public void call() {
//
//                }
//            }));
        }

        contentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        iv_thumb.setImageUrl(msg.getGift_info().getThumb());
    }
}

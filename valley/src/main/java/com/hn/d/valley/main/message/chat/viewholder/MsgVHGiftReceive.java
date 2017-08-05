package com.hn.d.valley.main.message.chat.viewholder;

import android.text.TextUtils;
import android.widget.TextView;

import com.hn.d.valley.R;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.attachment.GiftReceiveAttachment;
import com.hn.d.valley.main.message.attachment.GiftReceiveMsg;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.main.message.gift.ReceiveGiftUIDialog;
import com.hn.d.valley.widget.HnGlideImageView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.util.HashMap;
import java.util.Map;

import static com.hn.d.valley.main.message.chat.ChatUIView2.msgService;

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
            tv_text.setText(String.format("送你 %s", msg.getGift_info().getName()));
        } else if (message.getSessionType() == SessionTypeEnum.Team) {
            NimUserInfoCache userInfoCache = NimUserInfoCache.getInstance();
            tv_text.setText(String.format("%s 送 %s  %s", userInfoCache.getUserDisplayName(message.getFromAccount())
                    , userInfoCache.getUserDisplayName(msg.getTo_uid()), msg.getGift_info().getName()));
        }

        Map<String, Object> localExtension = message.getLocalExtension();
        if (localExtension == null) {
            localExtension = new HashMap<>();
            localExtension.put("read", true);
            message.setLocalExtension(localExtension);
            msgService().updateIMMessage(message);
            if (getMsgAdapter().hasOnShow() && isReceivedMessage() && msg.getTo_uid().equals(UserCache.getUserAccount())) {
                if (TextUtils.isEmpty(msg.getGift_info().getCharm()) || msg.getGift_info().getCharm().equals("0")) {
                    return;
                }
                getUIBaseView().startIView(new ReceiveGiftUIDialog(msg.getGift_info(),message.getSessionType()));
            }
        }

        iv_thumb.setImageUrl(msg.getGift_info().getThumb());
    }
}

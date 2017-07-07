package com.hn.d.valley.main.message.chat.viewholder;

import android.widget.ImageView;
import android.widget.TextView;

import com.hn.d.valley.R;
import com.hn.d.valley.main.me.SkinManagerUIView;
import com.hn.d.valley.main.message.attachment.CustomAttachment;
import com.hn.d.valley.main.message.attachment.GiftReceiveAttachment;
import com.hn.d.valley.main.message.attachment.GiftReceiveMsg;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.skin.SkinUtils;
import com.hn.d.valley.widget.HnGlideImageView;

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
        contentContainer.setBackground(null);
        switch (SkinUtils.getSkin()) {
            case SkinManagerUIView.SKIN_BLACK:
                tv_text.setBackgroundResource(R.drawable.bubble_box_right_black_selector);
                break;
            case SkinManagerUIView.SKIN_GREEN:
                tv_text.setBackgroundResource(R.drawable.bubble_box_right_green_selector);
                break;
            case SkinManagerUIView.SKIN_BLUE:
                tv_text.setBackgroundResource(R.drawable.bubble_box_right_blue_selector);
                break;
        }
        GiftReceiveMsg msg = attachment.getGiftReceiveMsg();
        tv_text.setText(String.format("送你一个 %s",msg.getGift_info().getName()));
        iv_thumb.setImageUrl(msg.getGift_info().getThumb());
    }
}

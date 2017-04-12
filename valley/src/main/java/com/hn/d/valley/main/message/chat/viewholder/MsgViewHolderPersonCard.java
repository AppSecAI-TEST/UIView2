package com.hn.d.valley.main.message.chat.viewholder;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.ScreenUtil;
import com.hn.d.valley.R;
import com.hn.d.valley.main.me.UserDetailUIView2;
import com.hn.d.valley.main.message.attachment.CustomAttachment;
import com.hn.d.valley.main.message.attachment.CustomAttachmentType;
import com.hn.d.valley.main.message.attachment.PersonalCard;
import com.hn.d.valley.main.message.attachment.PersonalCardAttachment;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.widget.HnGlideImageView;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgViewHolderPersonCard extends MsgViewHolderBase {

    public MsgViewHolderPersonCard(BaseMultiAdapter adapter) {
        super(adapter);
    }

    @Override
    public void convert(RBaseViewHolder holder, IMMessage data, int position, boolean isScrolling) {
        super.convert(holder, data, position, isScrolling);
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

        tv_pc_name.setText("[第三方APP自定义消息]");

        contentContainer.setBackgroundResource(0);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) pc_layout.getLayoutParams();
        if (isReceivedMessage()) {
            params.setMarginStart(ScreenUtil.dip2px(10));
        } else {
            params.setMarginEnd(ScreenUtil.dip2px(10));
        }

        CustomAttachment attachment = (CustomAttachment) message.getAttachment();
        if (attachment == null) {
            return;
        }
        switch (attachment.getType()) {

            case CustomAttachmentType.PersonalCard:
                PersonalCardAttachment pcAttachment = (PersonalCardAttachment) attachment;
                final PersonalCard from = PersonalCardAttachment.from(pcAttachment.toJson(true));
                tv_pc_name.setText(from.getUsername());
                imageView.setImageUrl(from.getAvatar());

                pc_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mUIBaseView.startIView(new UserDetailUIView2(from.getUid()));
                    }
                });

                break;
            default:
                break;

        }

    }

}

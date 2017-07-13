package com.hn.d.valley.main.message.chat.viewholder;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.hn.d.valley.R;
import com.hn.d.valley.emoji.MoonUtil;
import com.hn.d.valley.main.message.attachment.RechargeMsgAttachment;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.main.message.groupchat.RequestCallback;
import com.hn.d.valley.utils.HtmlFrom;
import com.hn.d.valley.widget.HnGlideImageView;
import com.hn.d.valley.x5.X5WebUIView;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.regex.Pattern;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgVHRecharge extends MsgViewHolderBase {

    TextView contentView;


    public MsgVHRecharge(BaseMultiAdapter adapter) {
        super(adapter);
    }

    @Override
    public void convert(RBaseViewHolder holder, IMMessage data, int position, boolean isScrolling) {
        super.convert(holder,data,position,isScrolling);
    }

    @Override
    protected int getContentResId() {
        return R.layout.msg_text_layout;
    }

    @Override
    protected void inflateContentView() {
        contentView = (TextView) findViewById(R.id.msg_text_view);
    }

    @Override
    protected void bindContentView() {
        RechargeMsgAttachment attachment = (RechargeMsgAttachment) message.getAttachment();
        if (attachment != null) {
            contentView.setText(attachment.getRechargeMsg().getMsg());
        }
    }

}

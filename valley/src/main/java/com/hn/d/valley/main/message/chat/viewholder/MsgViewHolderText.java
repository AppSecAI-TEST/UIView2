package com.hn.d.valley.main.message.chat.viewholder;

import android.widget.TextView;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.hn.d.valley.R;
import com.hn.d.valley.emoji.MoonUtil;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgViewHolderText extends MsgViewHolderBase {

    public MsgViewHolderText(BaseMultiAdapter adapter) {
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

    }

    @Override
    protected void bindContentView() {

        TextView contentView = (TextView) findViewById(R.id.msg_text_view);
        MoonUtil.show(context, contentView, message.getContent());

    }
}

package com.hn.d.valley.main.message.chat.viewholder;

import android.widget.TextView;

import com.hn.d.valley.R;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgViewHolderUnknown extends MsgViewHolderBase {

    public MsgViewHolderUnknown(BaseMultiAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.item_single_main_text_view;
    }

    @Override
    protected void inflateContentView() {

    }

    @Override
    protected void bindContentView() {
        TextView tv_text = (TextView) findViewById(R.id.text_view);
        tv_text.setText(mUIBaseView.getString(R.string.text_unkown_msg) + ": " + message.getContent());

    }
}

package com.hn.d.valley.main.message.chat.viewholder;

import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.hn.d.valley.R;
import com.hn.d.valley.emoji.MoonUtil;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.x5.X5WebUIView;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.Map;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgViewHolderPushText extends MsgViewHolderBase {

    public MsgViewHolderPushText(BaseMultiAdapter adapter) {
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
        Map<String, Object> extension = message.getRemoteExtension();

        MoonUtil.show(context, contentView, (String) extension.get("ext"));
        String title = (String) extension.get("title");
        final String link = (String) extension.get("link");
        String thumb = (String) extension.get("thumb");

        contentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUIBaseView.startIView(new X5WebUIView(link));
            }
        });

    }
}

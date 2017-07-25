package com.hn.d.valley.main.message.chat.viewholder;

import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.hn.d.valley.R;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.widget.HnGlideImageView;
import com.hn.d.valley.x5.X5WebUIView;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.Map;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgViewHolderPushPictureText extends MsgViewHolderBase {

    public MsgViewHolderPushPictureText(BaseMultiAdapter adapter) {
        super(adapter);
    }

    @Override
    public void convert(RBaseViewHolder holder, IMMessage data, int position, boolean isScrolling) {
        super.convert(holder,data,position,isScrolling);
    }

    @Override
    protected int getContentResId() {
        return R.layout.msg_push_image_layout;
    }

    @Override
    protected void inflateContentView() {

    }

    @Override
    protected void bindContentView() {

        TextView contentView = (TextView) findViewById(R.id.msg_text_view);
        HnGlideImageView draweeView = (HnGlideImageView) findViewById(R.id.msg_image_view);

        Map<String, Object> extension = message.getRemoteExtension();

        String title = (String) extension.get("title");
        final String link = (String) extension.get("link");
        String thumb = (String) extension.get("thumb");

        draweeView.setImageUrl(thumb);
        contentView.setText(title);

        contentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUIBaseView.startIView(new X5WebUIView(link));
            }
        });

    }

}

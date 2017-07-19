package com.hn.d.valley.main.message.chat.viewholder;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.angcyo.github.utilcode.utils.SpannableStringUtils;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.hn.d.valley.R;
import com.hn.d.valley.main.message.attachment.DiscussRecommAttachment;
import com.hn.d.valley.main.message.attachment.DiscussRecommendMsg;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.sub.user.DynamicDetailUIView2;
import com.hn.d.valley.x5.X5WebUIView;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.Map;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgVHDiscussRecommend extends MsgViewHolderBase {

    public MsgVHDiscussRecommend(BaseMultiAdapter adapter) {
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
        DiscussRecommAttachment attachment  = (DiscussRecommAttachment) message.getAttachment();
        final DiscussRecommendMsg msg = attachment.getRecommendMsg();
        contentView.setText(SpannableStringUtils.getBuilder(msg.getMsg())
                .append(context.getString(R.string.text_click_look_detail))
                .setForegroundColor(ContextCompat.getColor(context,R.color.blue_4777af))
                .create());

        contentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUIBaseView.startIView(new DynamicDetailUIView2(msg.getDiscuss_id()));
            }
        });

    }
}

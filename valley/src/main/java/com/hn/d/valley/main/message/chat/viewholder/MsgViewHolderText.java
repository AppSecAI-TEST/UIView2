package com.hn.d.valley.main.message.chat.viewholder;

import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.github.utilcode.utils.SpannableStringUtils;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.T_;
import com.bumptech.glide.Glide;
import com.hn.d.valley.R;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.emoji.MoonUtil;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.main.message.groupchat.RequestCallback;
import com.hn.d.valley.main.wallet.MyWalletUIView;
import com.hn.d.valley.utils.HtmlFrom;
import com.hn.d.valley.utils.Regex;
import com.hn.d.valley.widget.HnGlideImageView;
import com.hn.d.valley.x5.X5WebUIView;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgViewHolderText extends MsgViewHolderBase {

    TextView contentView;

    public MsgViewHolderText(BaseMultiAdapter adapter) {
        super(adapter);
    }

    @Override
    public void convert(RBaseViewHolder holder, IMMessage data, int position, boolean isScrolling) {
        super.convert(holder, data, position, isScrolling);
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
        final String msg = message.getContent();
        if (TextUtils.isEmpty(msg)) {
            return;
        }

        if (MsgVHLink.isLinkMsg(message)) {
            contentView.setTextColor(ContextCompat.getColor(context,R.color.blue_4777af));
            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUIBaseView.startIView(new X5WebUIView(msg));

                }
            });
            contentView.setText(msg);
//            SpannableStringUtils.getBuilder(msg).append(msg).setClickSpan(new ClickableSpan() {
//                @Override
//                public void onClick(View widget) {
//                    mUIBaseView.startIView(new X5WebUIView(msg));
//
//                }
//
//                @Override
//                public void updateDrawState(TextPaint ds) {
//                    super.updateDrawState(ds);
//                    ds.setColor(ContextCompat.getColor(context,R.color.blue_4777af));
//                    ds.clearShadowLayer();
//                }
//            }).create();
            return;
        }

        MoonUtil.show(context, contentView, message.getContent());
    }

}

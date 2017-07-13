package com.hn.d.valley.main.message.chat.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hn.d.valley.R;
import com.hn.d.valley.main.message.attachment.ConsumeMsg;
import com.hn.d.valley.main.message.attachment.KLGCoinConsumeAttachment;
import com.hn.d.valley.main.message.attachment.WithDrawalFailAttachment;
import com.hn.d.valley.main.message.attachment.WithDrawalFailMsg;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.main.wallet.BillUIView;
import com.hn.d.valley.sub.other.KLGCoinUIVIew;

import java.util.Locale;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgVHKLGCoinConsume extends MsgViewHolderBase {

    public MsgVHKLGCoinConsume(BaseMultiAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.msg_wallet_layout;
    }

    @Override
    protected void inflateContentView() {

    }

    @Override
    protected void bindContentView() {

        ImageView imageView = (ImageView) findViewById(R.id.iv_item_head);
        TextView tv_pc_name = (TextView) findViewById(R.id.tv_pc_name);

        KLGCoinConsumeAttachment attachment = (KLGCoinConsumeAttachment) message.getAttachment();
        if (attachment == null) {
            return;
        }

        ConsumeMsg drawalMsg = attachment.getConsumeMsg();
        imageView.setImageResource(R.drawable.longbi_80);

        tv_pc_name.setText(String.format(Locale.CHINA,context.getString(R.string.text_klgcoin_consume_msg),drawalMsg.getExtend().getCoins()));

        contentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUIBaseView.startIView(new KLGCoinUIVIew());
            }
        });

    }

}

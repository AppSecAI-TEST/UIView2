package com.hn.d.valley.main.message.chat.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hn.d.valley.R;
import com.hn.d.valley.main.message.attachment.WithDrawalAttachment;
import com.hn.d.valley.main.message.attachment.WithDrawalFailAttachment;
import com.hn.d.valley.main.message.attachment.WithDrawalFailMsg;
import com.hn.d.valley.main.message.attachment.WithDrawalMsg;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.main.wallet.BillUIView;

import java.util.Locale;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgVHWithDrawalFail extends MsgViewHolderBase {

    public MsgVHWithDrawalFail(BaseMultiAdapter adapter) {
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

        WithDrawalFailAttachment attachment = (WithDrawalFailAttachment) message.getAttachment();
        if (attachment == null) {
            return;
        }

        WithDrawalFailMsg drawalMsg = attachment.getWithDrawalMsg();
        imageView.setImageResource(R.drawable.tixian_konglongjun);

        tv_pc_name.setText(String.format(Locale.CHINA,context.getString(R.string.text_withdrawal_fail_msg),drawalMsg.getMoney() / 100f));

        contentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUIBaseView.startIView(new BillUIView());
            }
        });

    }

}

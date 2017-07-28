package com.hn.d.valley.main.message.chat.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hn.d.valley.R;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.cache.TeamDataCache;
import com.hn.d.valley.main.message.attachment.ReceiptsNotice;
import com.hn.d.valley.main.message.attachment.ReceiptsNoticeAttachment;
import com.hn.d.valley.main.message.attachment.RefundMsg;
import com.hn.d.valley.main.message.attachment.RefundMsgAttachment;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.main.wallet.BillUIView;
import com.hn.d.valley.widget.HnGlideImageView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.util.Locale;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgVHRefundMsg extends MsgViewHolderBase {

    public MsgVHRefundMsg(BaseMultiAdapter adapter) {
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

        RefundMsgAttachment attachment = (RefundMsgAttachment) message.getAttachment();
        if (attachment == null) {
            return;
        }

        RefundMsg refundMsg = attachment.getRefundMsg();

        imageView.setImageResource(R.drawable.hongbao_xiao_konglongjun);
        NimUserInfoCache userInfoCache = NimUserInfoCache.getInstance();

        contentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUIBaseView.startIView(new BillUIView());
            }
        });

        if (refundMsg.getExtend() == null ) {
            tv_pc_name.setText(refundMsg.getMsg());
            return;
        }
        if (refundMsg.getExtend().getTo_uid() == 0) {
            tv_pc_name.setText(String.format(Locale.CHINA,context.getString(R.string.text_msg_notice_refund),refundMsg.getReason()
                    , TeamDataCache.getInstance().getTeamName(refundMsg.getExtend().getTo_gid() + ""),refundMsg.getMoney() / 100f));
        }else {
            tv_pc_name.setText(String.format(Locale.CHINA,context.getString(R.string.text_msg_notice_refund),refundMsg.getReason()
                    ,userInfoCache.getUserDisplayNameEx(refundMsg.getExtend().getTo_uid() + ""),refundMsg.getMoney() / 100f));
        }

    }

}

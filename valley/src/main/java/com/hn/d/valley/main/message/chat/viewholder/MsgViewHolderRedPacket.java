package com.hn.d.valley.main.message.chat.viewholder;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.attachment.CustomAttachment;
import com.hn.d.valley.main.message.attachment.CustomAttachmentType;
import com.hn.d.valley.main.message.attachment.RedPacket;
import com.hn.d.valley.main.message.attachment.RedPacketAttachment;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.main.message.redpacket.Constants;
import com.hn.d.valley.main.message.redpacket.GrabedRDResultUIView;
import com.hn.d.valley.main.message.redpacket.OpenRedPacketUIDialog;
import com.hn.d.valley.main.message.service.RedPacketService;

import okhttp3.ResponseBody;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.hn.d.valley.main.message.redpacket.GrabPacketHelper.parseResult;

/**
 * Created by hewking on 2017/4/25.
 */

public class MsgViewHolderRedPacket extends MsgViewHolderBase {

    public MsgViewHolderRedPacket(BaseMultiAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.msg_redpacket_layout;
    }

    @Override
    protected void inflateContentView() {

    }

    @Override
    protected void bindContentView() {
        contentContainer.setBackground(null);
        TextView tv_content = (TextView) findViewById(R.id.tv_red_content);
        RelativeLayout rl_container = (RelativeLayout) findViewById(R.id.rl_red_packet);

        CustomAttachment attachment = (CustomAttachment) message.getAttachment();
        if (attachment == null) {
            return;
        }
        switch (attachment.getType()) {

            case CustomAttachmentType.REDPACKET:
                RedPacketAttachment pcAttachment = (RedPacketAttachment) attachment;
                final RedPacket redPacket = pcAttachment.getRedPacket();

                tv_content.setText(redPacket.getMsg());

                if (message.getFromAccount().equals(UserCache.getUserAccount())) {
//                    rl_container.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            mUIBaseView.startIView(new GrabedRDResultUIView(redPacket.getRid()));
//                        }
//                    });
//                    return;
                }

                rl_container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        RRetrofit.create(RedPacketService.class)
                                .status(Param.buildInfoMap("uid:" + UserCache.getUserAccount(), "redid:" + redPacket.getRid()))
                                .subscribeOn(Schedulers.io())
                                .map(new Func1<ResponseBody, Integer>() {
                                    @Override
                                    public Integer call(ResponseBody responseBody) {
                                        return parseResult(responseBody);
                                    }
                                }).subscribe(new BaseSingleSubscriber<Integer>() {

                            @Override
                            public void onSucceed(Integer code) {
                                if (Constants.ALREADY_GRAB == code) {
                                    mUIBaseView.startIView(new GrabedRDResultUIView(redPacket.getRid()));
                                } else if (Constants.CAN_BE_GRAB == code) {
                                    mUIBaseView.startIView(new OpenRedPacketUIDialog(redPacket.getRid()));
                                } else {
                                    T_.show("很抱歉，不能抢了");
                                }
                            }

                            @Override
                            public void onError(int code, String msg) {
                                super.onError(code, msg);
                            }
                        });

                    }
                });

                break;
            default:
                break;

        }

    }

}

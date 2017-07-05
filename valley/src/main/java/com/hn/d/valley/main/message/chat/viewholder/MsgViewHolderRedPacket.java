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
import com.hn.d.valley.main.message.attachment.RedPacket;
import com.hn.d.valley.main.message.attachment.RedPacketAttachment;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.main.message.redpacket.Constants;
import com.hn.d.valley.main.message.redpacket.GrabedRDResultUIView;
import com.hn.d.valley.main.message.redpacket.P2PStatusRPUIView;
import com.hn.d.valley.main.message.redpacket.OpenRedPacketUIDialog;
import com.hn.d.valley.main.message.service.RedPacketService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import okhttp3.ResponseBody;
import rx.android.schedulers.AndroidSchedulers;
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
        RedPacketAttachment pcAttachment = (RedPacketAttachment) attachment;
        final RedPacket redPacket = pcAttachment.getRedPacket();

        tv_content.setText(redPacket.getMsg());

        if (message.getFromAccount().equals(UserCache.getUserAccount())) {
            rl_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isChatGroup()) {
                        mUIBaseView.startIView(new P2PStatusRPUIView(message.getSessionId(),redPacket.getRid(),false));
                    } else {
                        checkRedPacketStatus(redPacket);
                    }
                }
            });
            return;
        }

        rl_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isChatGroup()) {
//                    mUIBaseView.startIView(new P2PStatusRPUIView(UserCache.getUserAccount(),redPacket.getRid(),true));
                    checkRedPacketStatus(redPacket);
                } else {
                    checkRedPacketStatus(redPacket);
                }
            }
        });

    }

    private void checkRedPacketStatus(final RedPacket redPacket) {
        RRetrofit.create(RedPacketService.class)
                .status(Param.buildInfoMap("uid:" + UserCache.getUserAccount(), "redid:" + redPacket.getRid()))
                .subscribeOn(Schedulers.io())
                .map(new Func1<ResponseBody, Integer>() {
                    @Override
                    public Integer call(ResponseBody responseBody) {
                        return parseResult(responseBody);
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSingleSubscriber<Integer>() {
                    @Override
                    public void onSucceed(Integer code) {
                        if (Constants.ALREADY_GRAB == code) {
                            if (!isChatGroup()) {
                                if (message.getFromAccount().equals(UserCache.getUserAccount())) {
                                    mUIBaseView.startIView(new P2PStatusRPUIView(message.getSessionId(),redPacket.getRid(),true));
                                    return;
                                }
                                mUIBaseView.startIView(new P2PStatusRPUIView(UserCache.getUserAccount(),redPacket.getRid(),true));
                                return;
                            }
                            mUIBaseView.startIView(new GrabedRDResultUIView(redPacket.getRid()));
                        } else if (Constants.CAN_BE_GRAB == code) {
                            mUIBaseView.startIView(new OpenRedPacketUIDialog(Constants.CAN_BE_GRAB,message.getSessionId(),redPacket.getRid()));
                        } else if (Constants.CAN_NOTE_GRAB == code) {
//                            mUIBaseView.startIView(new OpenRedPacketUIDialog(Constants.CAN_NOTE_GRAB,redPacket.getRid()));
                            mUIBaseView.startIView(new GrabedRDResultUIView(redPacket.getRid()));
                        } else if (Constants.EXPORE == code){
                            mUIBaseView.startIView(new OpenRedPacketUIDialog(Constants.EXPORE,redPacket.getRid()));
                        } else if (Constants.LOOT_OUT == code){
                            mUIBaseView.startIView(new OpenRedPacketUIDialog(Constants.LOOT_OUT,redPacket.getRid()));
                        } else {
                            T_.show("很抱歉，不能抢了");
                        }
                    }

                });
    }

    private boolean isChatGroup() {
        return message.getSessionType() == SessionTypeEnum.Team;
    }

}

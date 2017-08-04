package com.hn.d.valley.main.message.chat.viewholder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.view.UIIViewImpl;
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
import com.hn.d.valley.main.message.redpacket.GrabPacketHelper;
import com.hn.d.valley.main.message.redpacket.GrabedRDResultUIView;
import com.hn.d.valley.main.message.redpacket.OpenRedPacketUIDialog;
import com.hn.d.valley.main.message.service.RedPacketService;
import com.jakewharton.rxbinding.view.RxView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by hewking on 2017/4/25.
 */

public class MsgViewHolderRedPacket extends MsgViewHolderBase {

    public MsgViewHolderRedPacket(BaseMultiAdapter adapter) {
        super(adapter);
    }

    public static void checkRedPacketStatus(final UIIViewImpl layout, final long redId) {
        checkRedPacketStatus(layout, redId, "");
    }

    public static void checkRedPacketStatus(final UIIViewImpl layout, final long redId, final String discuss_id) {
        RRetrofit.create(RedPacketService.class)
                .status(Param.buildInfoMap("uid:" + UserCache.getUserAccount(), "redid:" + redId))
                .subscribeOn(Schedulers.io())
                .map(new Func1<ResponseBody, Integer>() {
                    @Override
                    public Integer call(ResponseBody responseBody) {
                        return GrabPacketHelper.parseResult(responseBody);
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSingleSubscriber<Integer>() {
                    @Override
                    public void onSucceed(Integer code) {
                        if (Constants.ALREADY_GRAB == code) {
//                            if (!isChatGroup()) {
//                                if (message.getFromAccount().equals(UserCache.getUserAccount())) {
//                                    mUIBaseView.startIView(new P2PStatusRPUIView(message.getSessionId(),redPacket.getRid(),true));
//                                    return;
//                                }
//                                mUIBaseView.startIView(new P2PStatusRPUIView(UserCache.getUserAccount(),redPacket.getRid(),true));
//                                return;
//                            }
                            layout.startIView(new GrabedRDResultUIView(redId).setSqureRedbag(true));
                        } else if (Constants.CAN_BE_GRAB == code) {
//                            layout.startIView(new OpenRedPacketUIDialog(Constants.CAN_BE_GRAB,message.getSessionId(),redId));
                            layout.startIView(new OpenRedPacketUIDialog(Constants.CAN_BE_GRAB, redId).setDiscuss_id(discuss_id));
                        } else if (Constants.CAN_NOTE_GRAB == code) {
//                            mUIBaseView.startIView(new OpenRedPacketUIDialog(Constants.CAN_NOTE_GRAB,redPacket.getRid()));
                            layout.startIView(new GrabedRDResultUIView(redId).setSqureRedbag(true));
                        } else if (Constants.EXPORE == code) {
                            layout.startIView(new OpenRedPacketUIDialog(Constants.EXPORE, redId).setDiscuss_id(discuss_id));
                        } else if (Constants.LOOT_OUT == code) {
                            layout.startIView(new OpenRedPacketUIDialog(Constants.LOOT_OUT, redId).setDiscuss_id(discuss_id));
                        } else {
                            T_.show("很抱歉，不能抢了");
                        }
                    }
                });
    }

    @Override
    protected int getContentResId() {
        return R.layout.msg_redpacket_layout_two;
    }

    @Override
    protected void inflateContentView() {

    }

    @Override
    protected void bindContentView() {
        TextView tv_content = (TextView) findViewById(R.id.tv_red_content);
        LinearLayout rl_container = (LinearLayout) findViewById(R.id.msg_card_layout);

        CustomAttachment attachment = (CustomAttachment) message.getAttachment();
        if (attachment == null) {
            return;
        }
        RedPacketAttachment pcAttachment = (RedPacketAttachment) attachment;
        final RedPacket redPacket = pcAttachment.getRedPacket();

        if (isReceivedMessage()) {
            contentContainer.setBackgroundResource(R.drawable.hongbao_bg_left);
        } else {
            contentContainer.setBackgroundResource(R.drawable.hongbao_bg_right);
        }


        tv_content.setText(redPacket.getMsg());

        if (message.getFromAccount().equals(UserCache.getUserAccount())) {
            rl_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isChatGroup()) {
//                        mUIBaseView.startIView(new P2PStatusRPUIView(message.getSessionId(),redPacket.getRid(),false));
                        mUIBaseView.startIView(new GrabedRDResultUIView(redPacket.getRid()));
                    } else {
                        checkRedPacketStatus(getUIBaseView(), redPacket.getRid());
                    }
                }
            });
            return;
        }

        // 防止多次重复点击
        RxView.clicks(rl_container).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (!isChatGroup()) {
                            checkRedPacketStatus(getUIBaseView(), redPacket.getRid());
                        } else {
                            checkRedPacketStatus(getUIBaseView(), redPacket.getRid());
                        }
                    }
                });

//        rl_container.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isChatGroup()) {
////                    mUIBaseView.startIView(new P2PStatusRPUIView(UserCache.getUserAccount(),redPacket.getRid(),true));
//                    checkRedPacketStatus(redPacket);
//                } else {
//                    checkRedPacketStatus(redPacket);
//                }
//            }
//        });

    }

    private boolean isChatGroup() {
        return message.getSessionType() == SessionTypeEnum.Team;
    }

}

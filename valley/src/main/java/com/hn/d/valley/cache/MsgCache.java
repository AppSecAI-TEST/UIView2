package com.hn.d.valley.cache;

import android.text.TextUtils;

import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.event.UpdateDataEvent;
import com.hn.d.valley.nim.RNim;
import com.hn.d.valley.utils.RBus;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/26 13:59
 * 修改人员：Robi
 * 修改时间：2016/12/26 13:59
 * 修改备注：
 * Version: 1.0.0
 */
public class MsgCache implements ICache {

    private MsgCache() {
    }

    public static MsgCache instance() {
        return Holder.instance;
    }

    public static void notifyNoreadNum() {
        notifyNoreadNum(RecentContactsCache.instance().getTotalUnreadCount());
    }

    public static void notifyNoreadNum(int num) {
        RBus.post(Constant.TAG_NO_READ_NUM, new UpdateDataEvent(num, Constant.POS_MESSAGE));
    }

    public static MsgService msgService() {
        return NIMClient.getService(MsgService.class);
    }

    public void setMsgUnread(final String messageUuid, String sessionId, SessionTypeEnum sessionType) {
        msgService().queryMessageListEx(
                MessageBuilder.createEmptyMessage(sessionId, sessionType, System.currentTimeMillis()),
                QueryDirectionEnum.QUERY_OLD, 10, true)
                .setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
                    @Override
                    public void onResult(int code, List<IMMessage> result, Throwable exception) {
                        if (code == ResponseCode.RES_SUCCESS) {
                            for (IMMessage message : result) {
                                if (TextUtils.equals(messageUuid, message.getUuid())) {
                                    message.setConfig(new CustomMessageConfig());
                                    notifyNoreadNum();
                                    break;
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void registerObservers(boolean register) {
        RNim.service(MsgServiceObserve.class).observeReceiveMessage(new Observer<List<IMMessage>>() {
            @Override
            public void onEvent(List<IMMessage> imMessages) {
                //收到消息之后, 通知未读消息提醒
                notifyNoreadNum();
            }
        }, register);
    }

    @Override
    public void buildCache() {
        notifyNoreadNum();
    }

    @Override
    public void clear() {

    }

    private static class Holder {
        static MsgCache instance = new MsgCache();
    }
}

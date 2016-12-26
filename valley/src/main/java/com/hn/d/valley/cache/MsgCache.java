package com.hn.d.valley.cache;

import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.event.UpdateDataEvent;
import com.hn.d.valley.nim.RNim;
import com.hn.d.valley.utils.RBus;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.IMMessage;

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

package com.hn.d.valley.main.me.setting;

import android.text.TextUtils;

import com.angcyo.uiview.github.utilcode.utils.SPUtils;
import com.hn.d.valley.nim.RNim;
import com.netease.nimlib.sdk.NIMClient;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/15 17:54
 * 修改人员：hewking
 * 修改时间：2017/05/15 17:54
 * 修改备注：
 * Version: 1.0.0
 */
public class MsgNotifySetting {

    private static final String TAG = MsgNotifySetting.class.getSimpleName();

    private static final String TOGGLENOTIFICATION = "toggleNotification";
    private static final String SHOWMSGNOTIDETAIL = "showMsgNotiDetail";
    private static final String DOWNTIME = "downTime";
    private static final String DOWNTIMETOGGLE = "downTimeToggle";
    private static final String RING = "ring";
    private static final String VIBRATE = "vibrate";
    private static final String CIRCLE_NOTIFY = "circle_notify";
    private static final String GROUPMSG_NOTIFY = "groupmsg_notify";
    private static final String BINDCONTACTS = "bind_contacts";

    private SPUtils mSPUtils;

    private MsgNotifySetting() {
        if (mSPUtils == null) {
            mSPUtils = new SPUtils(TAG);
        }
    }

    private static class Holder {
        private static MsgNotifySetting instance = new MsgNotifySetting();
    }

    public static MsgNotifySetting instance() {
        return Holder.instance;
    }

    // 开启/关闭通知栏消息提醒
    public MsgNotifySetting toggleNotification(boolean enable) {
        NIMClient.toggleNotification(enable);
//        NIMClient.updateStatusBarNotificationConfig(RNim.genNotiConfig());
        mSPUtils.putBoolean(TOGGLENOTIFICATION,enable);
        return this;
    }

    public boolean isEnableNoti() {
        return mSPUtils.getBoolean(TOGGLENOTIFICATION,true);
    }

    /**
     * 通知显示消息详情
     * @param enable
     * @return
     */
    public MsgNotifySetting showMsgNotiDetail(boolean enable) {
        mSPUtils.putBoolean(SHOWMSGNOTIDETAIL,enable);
        return this;
    }

    public boolean isShowMsgNotiDetail() {
        return mSPUtils.getBoolean(SHOWMSGNOTIDETAIL,false);
    }

    public MsgNotifySetting setAvoidMsgNotify(boolean enable)  {
        mSPUtils.putBoolean(DOWNTIMETOGGLE,enable);
        return this;
    }

    /**
     * 是否设置消息免打扰
     * @return
     */
    public boolean isMsgNotify() {
        return mSPUtils.getBoolean(DOWNTIMETOGGLE,false);
    }

    /**
     * 设置消息免打扰时间
     * @param downTime 为"" 设置为 false
     * @return
     */
    public MsgNotifySetting setDownTime(String downTime) {
        if (TextUtils.isEmpty(downTime)) {
            setAvoidMsgNotify(false);
        } else {
            setAvoidMsgNotify(true);
        }
        mSPUtils.putString(DOWNTIME,downTime);
        return this;
    }

    public MsgNotifySetting setRing(boolean enable) {
        mSPUtils.putBoolean(RING,enable);
        return this;
    }

    public boolean isRing() {
        return mSPUtils.getBoolean(RING,true);
    }

    public MsgNotifySetting setVibrate(boolean enable) {
        mSPUtils.putBoolean(VIBRATE,enable);
        return this;
    }

    public boolean isVirbrate() {
        return mSPUtils.getBoolean(VIBRATE,true);
    }

    public void enableCircleNotify(boolean enable) {
        mSPUtils.putBoolean(CIRCLE_NOTIFY,enable);
    }

    public boolean isCircleCNotify() {
        return mSPUtils.getBoolean(CIRCLE_NOTIFY);
    }

    public void enableGroupMsgNotify(boolean enable) {
        mSPUtils.putBoolean(GROUPMSG_NOTIFY,enable);
    }

    public boolean isGroupMsgNotify() {
        return mSPUtils.getBoolean(GROUPMSG_NOTIFY,true);
    }

    public void enableBindContacts(boolean enable) {
        mSPUtils.putBoolean(BINDCONTACTS,enable);
    }

    public boolean isBindContacts() {
        return mSPUtils.getBoolean(BINDCONTACTS);
    }

}

package com.hn.d.valley.base.constant;

import com.angcyo.umeng.UM;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：友盟事件统计
 * 创建人员：Robi
 * 创建时间：2017/05/19 10:37
 * 修改人员：Robi
 * 修改时间：2017/05/19 10:37
 * 修改备注：
 * Version: 1.0.0
 */
public class Action {
    static String app_launch = "app_launch";
    static String authAction = "authAction";
    static String authAction_commit = "authAction_commit";
    static String phone_register = "phone_register";
    static String publishAction = "publishAction";
    static String publishAction_Audio = "publishAction_Audio";
    static String publishAction_Picture = "publishAction_Picture";
    static String publishAction_Success = "publishAction_Success";
    static String publishAction_Video = "publishAction_Video";
    static String qq_register = "qq_register";
    static String tap_klg = "tap_klg";
    static String wechat_register = "wechat_register";

    /**
     * APP启动
     */
    public static void app_launch() {
        UM.onEvent(app_launch);
    }

    /**
     * 点击认证事件
     */
    public static void authAction() {
        UM.onEvent(authAction);
    }

    /**
     * 点击认证并且提交
     */
    public static void authAction_commit() {
        UM.onEvent(authAction_commit);
    }

    /**
     * 手机号注册
     */
    public static void phone_register() {
        UM.onEvent(phone_register);
    }

    /**
     * 点击发布
     */
    public static void publishAction() {
        UM.onEvent(publishAction);
    }

    /**
     * 点击发布语音
     */
    public static void publishAction_Audio() {
        UM.onEvent(publishAction_Audio);
    }

    /**
     * 发布图文
     */
    public static void publishAction_Picture() {
        UM.onEvent(publishAction_Picture);
    }

    /**
     * 帖子发布成功
     */
    public static void publishAction_Success() {
        UM.onEvent(publishAction_Success);
    }

    /**
     * 发布视频
     */
    public static void publishAction_Video() {
        UM.onEvent(publishAction_Video);
    }

    /**
     * QQ注册
     */
    public static void qq_register() {
        UM.onEvent(qq_register);
    }

    /**
     * 点击恐龙谷界面
     */
    public static void tap_klg() {
        UM.onEvent(tap_klg);
    }

    /**
     * 微信注册
     */
    public static void wechat_register() {
        UM.onEvent(wechat_register);
    }
}

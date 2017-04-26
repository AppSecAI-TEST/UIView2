package com.hn.d.valley.main.message.notify;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/25 11:00
 * 修改人员：hewking
 * 修改时间：2017/04/25 11:00
 * 修改备注：
 * Version: 1.0.0
 */
public class GroupDissolve extends BaseNotification{

    /**
     * msg : 该群因涉嫌相关违规条例，已被禁用，不能使用，系统将会自动解散该群
     */

    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

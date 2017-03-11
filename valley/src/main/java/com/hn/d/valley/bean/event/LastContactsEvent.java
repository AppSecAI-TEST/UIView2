package com.hn.d.valley.bean.event;

import com.netease.nimlib.sdk.msg.model.RecentContact;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/26 11:04
 * 修改人员：Robi
 * 修改时间：2016/12/26 11:04
 * 修改备注：
 * Version: 1.0.0
 */
public class LastContactsEvent {

    /**
     * 最后一个联系人发来的消息
     */

    public RecentContact mRecentContact;

    public LastContactsEvent(RecentContact contact) {
        mRecentContact = contact;
    }
}
package com.hn.d.valley.main.message.session;

import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/18 14:04
 * 修改人员：hewking
 * 修改时间：2017/05/18 14:04
 * 修改备注：
 * Version: 1.0.0
 */
public interface SessionProxy {
    // 发送消息
    boolean sendMessage(IMMessage msg);



}

package com.hn.d.valley.main.message.attachment;

import com.hn.d.valley.nim.CustomAttachParser;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/05 17:02
 * 修改人员：hewking
 * 修改时间：2017/04/05 17:02
 * 修改备注：
 * Version: 1.0.0
 */
public abstract class CustomAttachment implements MsgAttachment {

    protected int type;

    public CustomAttachment(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

}

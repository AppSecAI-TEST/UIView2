package com.hn.d.valley.nim;

import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;

public class CustomAttachParser implements MsgAttachmentParser {

    // 根据解析到的消息类型，确定附件对象类型
    @Override
    public MsgAttachment parse(String json) {
        CustomAttachment attachment = new CustomAttachment(json);
        return attachment;
    }
}
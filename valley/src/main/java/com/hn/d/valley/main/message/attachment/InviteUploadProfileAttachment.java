package com.hn.d.valley.main.message.attachment;

import com.angcyo.uiview.utils.Json;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/25 16:58
 * 修改人员：hewking
 * 修改时间：2017/04/25 16:58
 * 修改备注：
 * Version: 1.0.0
 */
public class InviteUploadProfileAttachment extends CustomAttachment {

    private InviteUploadMsg inviteUploadMsg;

    public InviteUploadProfileAttachment(String content) {
        super(CustomAttachmentType.SHARE_DYNAMIC);
        inviteUploadMsg = Json.from(content,InviteUploadMsg.class);
    }

    public InviteUploadProfileAttachment(InviteUploadMsg detailMsg) {
        super(CustomAttachmentType.SHARE_DYNAMIC);
        inviteUploadMsg = detailMsg;
    }

    public InviteUploadMsg getInviteUploadMsg() {
        return inviteUploadMsg;
    }

    public void setDynamicMsg(InviteUploadMsg receiptsNotice) {
        this.inviteUploadMsg = receiptsNotice;
    }

    @Override
    public String toJson(boolean send) {
        return Json.to(inviteUploadMsg);
    }



}

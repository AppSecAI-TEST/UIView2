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
public class LikeMsgAttachment extends CustomAttachment {

    private LikeMsg likeMsg;

    public LikeMsgAttachment(String content) {
        super(CustomAttachmentType.LIKEMSG);
        likeMsg = Json.from(content,LikeMsg.class);
    }

    public LikeMsg getLikeMsg() {
        return likeMsg;
    }

    public void setLikeMsg(LikeMsg receiptsNotice) {
        this.likeMsg = receiptsNotice;
    }

    @Override
    public String toJson(boolean send) {
        return Json.to(likeMsg);
    }



}

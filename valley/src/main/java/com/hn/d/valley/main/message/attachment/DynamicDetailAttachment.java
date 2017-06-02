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
public class DynamicDetailAttachment extends CustomAttachment {

    private DynamicDetailMsg dynamicMsg;

    public DynamicDetailAttachment(String content) {
        super(CustomAttachmentType.SHARE_DYNAMIC);
        dynamicMsg = Json.from(content,DynamicDetailMsg.class);
    }

    public DynamicDetailAttachment(DynamicDetailMsg detailMsg) {
        super(CustomAttachmentType.SHARE_DYNAMIC);
        dynamicMsg = detailMsg;
    }

    public DynamicDetailMsg getDynamicMsg() {
        return dynamicMsg;
    }

    public void setDynamicMsg(DynamicDetailMsg receiptsNotice) {
        this.dynamicMsg = receiptsNotice;
    }

    @Override
    public String toJson(boolean send) {
        return Json.to(dynamicMsg);
    }



}

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
public class CustomExpressionAttachment extends CustomAttachment {

    private CustomExpressionMsg expressionMsg;

    public CustomExpressionAttachment(String content) {
        super(CustomAttachmentType.EXPRESSIONMSG);
        expressionMsg = Json.from(content,CustomExpressionMsg.class);
    }

    public CustomExpressionAttachment(CustomExpressionMsg expressionMsg) {
        super(CustomAttachmentType.EXPRESSIONMSG);
        this.expressionMsg = expressionMsg;
    }

    public CustomExpressionMsg getExpressionMsg() {
        return expressionMsg;
    }

    public void setExpressionMsg(CustomExpressionMsg expressionMsg) {
        this.expressionMsg = expressionMsg;
    }

    @Override
    public String toJson(boolean send) {
        return Json.to(expressionMsg);
    }



}

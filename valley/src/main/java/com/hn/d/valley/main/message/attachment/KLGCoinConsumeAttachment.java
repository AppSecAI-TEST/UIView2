package com.hn.d.valley.main.message.attachment;

import com.angcyo.uiview.utils.Json;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/09 15:36
 * 修改人员：hewking
 * 修改时间：2017/05/09 15:36
 * 修改备注：
 * Version: 1.0.0
 */
public class KLGCoinConsumeAttachment extends CustomAttachment {

    private ConsumeMsg withDrawalMsg;

    public ConsumeMsg getConsumeMsg() {
        return withDrawalMsg;
    }


    public KLGCoinConsumeAttachment(String content) {
        super(CustomAttachmentType.KLG_CONSUME);
        withDrawalMsg = Json.from(content,ConsumeMsg.class);

    }

    @Override
    public String toJson(boolean send) {
        return Json.to(withDrawalMsg);
    }
}

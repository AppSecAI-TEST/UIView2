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
public class ShareNewsAttachment extends CustomAttachment {

    private ShareNewsMsg newsMsg;

    public ShareNewsAttachment(String content) {
        super(CustomAttachmentType.SHARE_NEWS_CODE);
        newsMsg = Json.from(content,ShareNewsMsg.class);
    }

    public ShareNewsAttachment(ShareNewsMsg detailMsg) {
        super(CustomAttachmentType.SHARE_DYNAMIC);
        newsMsg = detailMsg;
    }

    public ShareNewsMsg getNewsMsg() {
        return newsMsg;
    }

    public void setDynamicMsg(ShareNewsMsg receiptsNotice) {
        this.newsMsg = receiptsNotice;
    }

    @Override
    public String toJson(boolean send) {
        return Json.to(newsMsg);
    }



}

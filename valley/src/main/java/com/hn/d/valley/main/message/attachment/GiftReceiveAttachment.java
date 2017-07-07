package com.hn.d.valley.main.message.attachment;

import com.angcyo.uiview.utils.Json;
import com.hn.d.valley.bean.GiftBean;

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
public class GiftReceiveAttachment extends CustomAttachment {

    private GiftReceiveMsg giftReceiveMsg;

    public GiftReceiveAttachment(String content) {
        super(CustomAttachmentType.GIFT_RECEIVE);
        giftReceiveMsg = Json.from(content,GiftReceiveMsg.class);
    }

    public GiftReceiveAttachment(GiftReceiveMsg detailMsg) {
        super(CustomAttachmentType.SHARE_DYNAMIC);
        giftReceiveMsg = detailMsg;
    }

    public GiftReceiveAttachment(GiftBean gift,String to_uid) {
        super(CustomAttachmentType.SHARE_DYNAMIC);
        giftReceiveMsg = new GiftReceiveMsg();
        giftReceiveMsg.setGift_info(gift);
        giftReceiveMsg.setExtend_type(CustomAttachmentType.GIFT);
        giftReceiveMsg.setCreated((int) System.currentTimeMillis());
        giftReceiveMsg.setMsg(String.format("送了一个 %s",gift.getName()));
        giftReceiveMsg.setTo_uid(to_uid);
    }

    public GiftReceiveMsg getGiftReceiveMsg() {
        return giftReceiveMsg;
    }

    public void setDynamicMsg(GiftReceiveMsg receiptsNotice) {
        this.giftReceiveMsg = receiptsNotice;
    }

    @Override
    public String toJson(boolean send) {
        return Json.to(giftReceiveMsg);
    }



}

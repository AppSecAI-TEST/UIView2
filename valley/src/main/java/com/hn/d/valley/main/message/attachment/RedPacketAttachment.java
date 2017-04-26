package com.hn.d.valley.main.message.attachment;

import com.angcyo.uiview.utils.Json;
import com.hn.d.valley.bean.FriendBean;

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
public class RedPacketAttachment extends CustomAttachment {

    private RedPacket redPacket;

    public RedPacketAttachment(String content) {
        super(CustomAttachmentType.REDPACKET);
        redPacket = Json.from(content,RedPacket.class);
    }

    public RedPacket getRedPacket() {
        return redPacket;
    }

    public void setRedPacket(RedPacket redPacket) {
        this.redPacket = redPacket;
    }


    @Override
    public String toJson(boolean send) {
        return Json.to(redPacket);
    }



}

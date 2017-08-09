package com.hn.d.valley.main.message.attachment;

import com.angcyo.uiview.utils.Json;
import com.hn.d.valley.main.message.chat.viewholder.MsgVHLink;
import com.hn.d.valley.utils.HtmlFrom;

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
public class LinkMsgAttachment extends CustomAttachment {

    private HtmlFrom.LinkBean linkBean;

    public LinkMsgAttachment(HtmlFrom.LinkBean content) {
        super(-1);
        this.linkBean = content;
    }

    public HtmlFrom.LinkBean getLinkBean() {
        return linkBean;
    }

    public void setLinkBean(HtmlFrom.LinkBean linkBean) {
        this.linkBean = linkBean;
    }

    @Override
    public String toJson(boolean send) {
        return Json.to(linkBean);
    }



}

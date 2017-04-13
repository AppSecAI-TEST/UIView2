package com.hn.d.valley.main.message.attachment;

import com.angcyo.uiview.utils.Json;
import com.hn.d.valley.bean.FriendBean;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/05 16:58
 * 修改人员：hewking
 * 修改时间：2017/04/05 16:58
 * 修改备注：
 * Version: 1.0.0
 */
public class SystemPushAttachment extends CustomAttachment {

    private SystemPush systemPush;

    public SystemPushAttachment(String content) {
        super(CustomAttachmentType.SYSTEMP_PUSH);
        this.systemPush = Json.from(content,SystemPush.class);

    }

    public SystemPushAttachment(SystemPush systemPush) {
        super(CustomAttachmentType.SYSTEMP_PUSH);
        this.systemPush = systemPush;

    }

    @Override
    public String toJson(boolean send) {
        return Json.to(systemPush);
    }

    public String getSubType() {
        return systemPush.getTpl_type();
    }

    public SystemPushAttachment newInstance(String subType) {
        SystemPushAttachment attachment = null;
        switch (subType) {
            case "1":
                attachment = new SingleTextSPAttachment(systemPush);
                break;
            case "2" :
                attachment = new TextAndPictureSPAttachment(systemPush);
                break;
            case "3":
                attachment = new MultiPictureSPAttachment(systemPush);
                break;
        }
        return attachment;
    }

    public static class SingleTextSPAttachment extends SystemPushAttachment {

        public SingleTextSPAttachment(SystemPush systemPush) {
            super(systemPush);
        }
    }

    public static class TextAndPictureSPAttachment extends SystemPushAttachment {
        public TextAndPictureSPAttachment(SystemPush systemPush) {
            super(systemPush);
        }
    }

    public static class MultiPictureSPAttachment extends SystemPushAttachment {
        public MultiPictureSPAttachment(SystemPush systemPush) {
            super(systemPush);
        }
    }

}

package com.hn.d.valley.main.message.attachment;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/05 17:40
 * 修改人员：hewking
 * 修改时间：2017/04/05 17:40
 * 修改备注：
 * Version: 1.0.0
 */
public interface CustomAttachmentType {
    // 多端统一
    int PersonalCard = 1;

    int Notice = 2;

    int SYSTEMP_PUSH = 3;

    int SP_SINGLE_TEXT = 31;

    int SP_TEXT_PICTURE = 32;

    int SP_MULTI_PICTURE = 33;

    String PersonalCard_ = "user";

    String SystemPush = "system_push";

}

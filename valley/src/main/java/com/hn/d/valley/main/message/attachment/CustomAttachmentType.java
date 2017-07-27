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

    int REDPACKET = 4;

    int GRABEDMSG = 5;


    int RECEIPTSNOTICE = 6;

    int REFOUNDMSG = 7;

    int WITHDRAWAL = 8;

    int EXPRESSIONMSG = 9;

    int LIKEMSG = 10;

    int DYNAMICMSG = 11;

    int HOTSPOTINFO = 12;

    int SHARE_DYNAMIC = 13;

    int INVITE_UPLOAD = 14;

    int GIFT_RECEIVE = 15;

    int WITHDRAWAL_FAIL = 16;

    int KLG_CONSUME = 17;

    int RECHARGE_CODE = 18;

    int SHARE_NEWS_CODE = 19;

    int DISCUSS_RECOMM_TYPE = 20;

    int ONLINE_FORWARD_CODE = 21;


    int SP_SINGLE_TEXT = 31;

    int SP_TEXT_PICTURE = 32;

    int SP_MULTI_PICTURE = 33;



    String PersonalCard_ = "user";

    String ManagerPush = "system_push";

    String NEWBAG = "newredbag";

    String GRABREDBAG = "grabredbag";

    String COLLECTION_CONFIRM = "collection_confirm";

    String RECEIPTES = "collection";

    String REFUND = "refund";

    String RECHARGE = "recharge";

    String WITHDRAWAL_FAIL_MSG = "cashout_fail";

    String WITHDRAWAL_MSG = "cashout";

    String KLG_COIN_CONSUME = "consume";

    String KLGGIF_MSG = "system_klgGif";

    String LIKE_MSG = "like";

    String FORWARD_MSG = "forward";

    String HOTSPOTIFNO_MSG = "hot_news";

    String SHARE_DYNAMIC_MSG  = "dynamic";

    String SHARE_NEWS = "ShareHotNews";

    String INVITE_UPLOAD_PHOTOS  = "invite_upload_photos";

    String GIFT = "gift";

    String ONLINE_FORWARD_VIDEO = "video";

    String DISCUSS_RECOMM = "discuss_recommend";

}

package com.hn.d.valley.main.message.attachment;

import com.angcyo.uiview.utils.Json;
import com.google.gson.reflect.TypeToken;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.bean.realm.UserInfoBean;

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
public class PersonalCardAttachment extends CustomAttachment {

//    private BaseCustomBean friendBean;

    private PersonalCard personalCard;


    public PersonalCardAttachment(FriendBean friendBean) {
        super(CustomAttachmentType.PersonalCard);
//        this.friendBean = new BaseCustomBean<>(friendBean,CustomAttachmentType.PersonalCard);
        this.personalCard = PersonalCard.friend2PerCard(friendBean);
    }

    public PersonalCardAttachment(UserInfoBean bean) {
        super(CustomAttachmentType.PersonalCard);
        this.personalCard = PersonalCard.userInfo2PerCard(bean);
    }

    public PersonalCardAttachment(PersonalCard card) {
        super(CustomAttachmentType.PersonalCard);
        this.personalCard = card;
    }

    public PersonalCard getPersonalCard() {
        return personalCard;
    }

    @Override
    public String toJson(boolean send) {
//        return Json.to(friendBean);
        return Json.to(personalCard);
    }

//    public static BaseCustomBean<FriendBean> from(String json) {
//        return Json.from(json,new TypeToken<BaseCustomBean<FriendBean>>(){}.getType());
//    }

    public static PersonalCard from(String json) {
        return Json.from(json,PersonalCard.class);
    }




}

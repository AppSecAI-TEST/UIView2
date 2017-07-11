package com.hn.d.valley.main.message.attachment;

import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.bean.realm.UserInfoBean;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/06 9:48
 * 修改人员：hewking
 * 修改时间：2017/04/06 9:48
 * 修改备注：
 * Version: 1.0.0
 */
public class PersonalCard extends BaseCustomMsg{


    /**
     * msg : 用户名片
     * extend_type : user
     * uid : 50002
     * created : 1481768331
     * username : 和尚挑水
     * avatar : http://static.bzsns.cn/pic/M00/00/B1/CixiMlfX0JiAJF9kAAAhgFkBirQ91.JPEG
     */

    public static PersonalCard friend2PerCard(FriendBean bean) {
        PersonalCard personalCard = new PersonalCard();
        personalCard.setMsg("用户名片");
        personalCard.setExtend_type(CustomAttachmentType.PersonalCard_);
        personalCard.setUid(bean.getUid());
        personalCard.setCreated((int) System.currentTimeMillis());
        personalCard.setUsername(bean.getTrueName());
        personalCard.setAvatar(bean.getAvatar());
        return personalCard;
    }

    public static PersonalCard userInfo2PerCard(UserInfoBean bean) {
        PersonalCard card = new PersonalCard();
        card.setMsg("用户名片");
        card.setExtend_type(CustomAttachmentType.PersonalCard_);
        card.setUid(bean.getUid());
        card.setCreated((int)System.currentTimeMillis());
        card.setUsername(bean.getTrue_name());
        card.setAvatar(bean.getAvatar());
        return card;
    }

    private String msg;
    private String uid;
    private int created;
    private String username;
    private String avatar;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}

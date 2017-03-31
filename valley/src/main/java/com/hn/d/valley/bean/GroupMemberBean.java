package com.hn.d.valley.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hewking on 2017/3/13.
 */
public class GroupMemberBean {

    /**
     * user_id : 50003
     * member_type : 1
     * default_nick : 憨豆先生
     * nick :
     * created : 1480932612
     * user_avatar : http://static.bzsns.cn/pic/M00/00/ED/CixiMlgf2JGANVS_AAAYeWy7a6g56.JPEG?w=200&h=200&s=1
     * contact_mark :
     */

    @SerializedName("user_id")
    private String userId;
    @SerializedName("member_type")
    private String memberType;
    @SerializedName("default_nick")
    private String defaultNick;
    private String nick;
    private String created;
    @SerializedName("user_avatar")
    private String userAvatar;
    @SerializedName("contact_mark")
    private String contactMark;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public String getDefaultNick() {
        return defaultNick;
    }

    public void setDefaultNick(String defaultNick) {
        this.defaultNick = defaultNick;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getContactMark() {
        return contactMark;
    }

    public void setContactMark(String contactMark) {
        this.contactMark = contactMark;
    }

    @Override
    public String toString() {
        return userId;
    }
}

package com.hn.d.valley.bean;

import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hewking on 2017/3/14.
 */
public class GroupDescBean {

    /**
     * gid : 24
     * yx_gid : 14671794
     * default_name : 幽灵,和尚挑水,憨豆先生,胡浩三雄
     * name :
     * introduce :
     * default_avatar : http://static.bzsns.cn/pic/M00/01/04/CixiMlhGF3qAfuEwAAFvigHWDEo913.jpg?w=330&h=330&s=1
     * avatar :
     * admin : 50001
     * created : 1481610753
     * member_limit : 10
     * member_count : 5
     *         "top_member_limit":"50",

     * nick :
     * default_nick : signature
     * push : 1
     * user_avatar : http://static.bzsns.cn/pic/M00/00/2B/CixiMlbVVumAIJEGAAAetFQEzXc84.JPEG
     * announcement : 1
     */

    private String gid;
    @SerializedName("yx_gid")
    private String yxGid;
    @SerializedName("default_name")
    private String defaultName;
    private String name;
    private String introduce;
    @SerializedName("default_avatar")
    private String defaultAvatar;
    private String avatar;
    private String admin;
    private String created;
    @SerializedName("member_limit")
    private String memberLimit;
    @SerializedName("member_count")
    private int memberCount;
    @SerializedName("top_member_limit")
    private String topMemberLimit;
    private String nick;
    @SerializedName("default_nick")
    private String defaultNick;
    private String push;
    @SerializedName("user_avatar")
    private String userAatar;
    private int announcement;


    public boolean canUpgrade() {
        return Integer.valueOf(memberLimit) < Integer.valueOf(topMemberLimit);
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getYxGid() {
        return yxGid;
    }

    public void setYxGid(String yxGid) {
        this.yxGid = yxGid;
    }

    public String getTopMemberLimit() {
        return topMemberLimit;
    }

    public void setTopMemberLimit(String topMemberLimit) {
        this.topMemberLimit = topMemberLimit;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getDefaultAvatar() {
        return defaultAvatar;
    }

    public void setDefaultAvatar(String defaultAvatar) {
        this.defaultAvatar = defaultAvatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getMemberLimit() {
        return memberLimit;
    }

    public void setMemberLimit(String memberLimit) {
        this.memberLimit = memberLimit;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getDefaultNick() {
        return defaultNick;
    }

    public void setDefaultNick(String defaultNick) {
        this.defaultNick = defaultNick;
    }

    public String getPush() {
        return push;
    }

    public void setPush(String push) {
        this.push = push;
    }

    public String getUserAatar() {
        return userAatar;
    }

    public void setUserAatar(String userAatar) {
        this.userAatar = userAatar;
    }

    public int getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(int announcement) {
        this.announcement = announcement;
    }
}

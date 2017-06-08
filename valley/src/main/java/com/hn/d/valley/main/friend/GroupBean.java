package com.hn.d.valley.main.friend;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by hewking on 2017/3/13.
 */
public class GroupBean extends RealmObject{


    /**
     * gid : 10395
     * yx_gid : 23850410
     * name :
     * default_name : 无敌,军训,安卓(ღ˘⌣˘ღ)?????
     * introduce :
     * avatar :
     * default_avatar : http:\/\/circleimg.klgwl.com\/77500371484917281.776834
     * admin : 50031
     * created : 1489125764
     * member_count : 3
     */

    private String gid;
    @SerializedName("yx_gid")
    private String yxGid;
    private String name;
    @SerializedName("default_name")
    private String defaultName;
    private String introduce;
    private String avatar;
    @SerializedName("default_avatar")
    private String defaultAvatar;
    private String admin;
    private String created;
    @SerializedName("member_count")
    private String memberCount;

    public String getTrueName() {
        if (!TextUtils.isEmpty(name)){
            return name;
        } else {
            return defaultName;
        }
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDefaultAvatar() {
        return defaultAvatar;
    }

    public void setDefaultAvatar(String defaultAvatar) {
        this.defaultAvatar = defaultAvatar;
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

    public String getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(String memberCount) {
        this.memberCount = memberCount;
    }
}

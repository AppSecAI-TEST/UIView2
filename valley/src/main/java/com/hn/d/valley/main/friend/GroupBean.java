package com.hn.d.valley.main.friend;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hewking on 2017/3/13.
 */
public class GroupBean {

    /**
     * gid : 21
     * yx_gid : 33333
     * name :
     * introduce :
     * avatar : http://static.bzsns.cn/pic/M00/00/38/CixiMlbj0DOATFzlAABGkQXpArU717.png
     * admin : 50001
     * created : 1480932785
     * member_count : 4
     */

    private String gid;
    @SerializedName("yx_gid")
    private String yxGid;
    private String name;
    private String introduce;
    private String avatar;
    private String admin;
    private String created;
    @SerializedName("member_count")
    private String memberCcount;

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

    public String getMemberCcount() {
        return memberCcount;
    }

    public void setMemberCcount(String memberCcount) {
        this.memberCcount = memberCcount;
    }
}

package com.hn.d.valley.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hewking on 2017/3/9.
 */
public class GroupInfoBean {

    /**
     * gid : 22
     * group_name : 幽灵,和尚挑水,憨豆先生
     * group_avatar : http://static.bzsns.cn/pic/M00/00/38/CixiMlbj0DOATFzlAABGkQXpArU717.png
     * yx_gid : 20019
     */

    private String gid;
    @SerializedName("group_name")
    private String groupnName;
    @SerializedName("group_avatar")
    private String groupAvatar;
    private String yx_gid;

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getGroupnName() {
        return groupnName;
    }

    public void setGroupnName(String groupnName) {
        this.groupnName = groupnName;
    }

    public String getGroupAvatar() {
        return groupAvatar;
    }

    public void setGroupAvatar(String groupAvatar) {
        this.groupAvatar = groupAvatar;
    }

    public String getYx_gid() {
        return yx_gid;
    }

    public void setYx_gid(String yx_gid) {
        this.yx_gid = yx_gid;
    }
}

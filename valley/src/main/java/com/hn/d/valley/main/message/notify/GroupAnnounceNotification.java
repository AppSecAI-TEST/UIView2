package com.hn.d.valley.main.message.notify;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/25 10:54
 * 修改人员：hewking
 * 修改时间：2017/04/25 10:54
 * 修改备注：
 * Version: 1.0.0
 */
public class GroupAnnounceNotification extends BaseNotification{


    /**
     * extend_type : group_announcement
     * gid : 10505
     * an_id : 52
     * group_name : gggg
     * modify_time : 1493088871
     * avatar : http://avatorimg.klgwl.com/7dbc966b-0b51-46b7-b149-b77d489fb77d
     * username : ttt
     * content : 据统计
     * created : 1493088871
     */

    private String gid;
    private String an_id;
    private String group_name;
    private int modify_time;
    private String avatar;
    private String username;
    private String content;



    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getAn_id() {
        return an_id;
    }

    public void setAn_id(String an_id) {
        this.an_id = an_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public int getModify_time() {
        return modify_time;
    }

    public void setModify_time(int modify_time) {
        this.modify_time = modify_time;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}

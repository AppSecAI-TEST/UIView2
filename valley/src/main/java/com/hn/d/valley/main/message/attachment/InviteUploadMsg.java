package com.hn.d.valley.main.message.attachment;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/06/09 15:18
 * 修改人员：hewking
 * 修改时间：2017/06/09 15:18
 * 修改备注：
 * Version: 1.0.0
 */
public class InviteUploadMsg extends BaseCustomMsg {

    /**
     * msg : 邀请你完善照片墙
     * username : 哦哦
     * uid : 62176
     * created : 1496992060
     */

    private String msg;
    private String username;
    private String uid;
    private int created;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
}

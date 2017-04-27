package com.hn.d.valley.main.message.attachment;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/26 14:57
 * 修改人员：hewking
 * 修改时间：2017/04/26 14:57
 * 修改备注：
 * Version: 1.0.0
 */
public class RedPacketGrabedMsg extends BaseCustomMsg {


    /**
     * msg : 红包被抢
     * rid : 4343241312312324
     * created : 1435324645
     * owner : 60006
     * graber : 60012
     */

    private String msg;
    private long rid;
    private int created;
    private int owner;
    private int graber;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getRid() {
        return rid;
    }

    public void setRid(long rid) {
        this.rid = rid;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public int getGraber() {
        return graber;
    }

    public void setGraber(int graber) {
        this.graber = graber;
    }
}

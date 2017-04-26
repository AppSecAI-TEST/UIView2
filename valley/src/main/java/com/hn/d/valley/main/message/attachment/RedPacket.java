package com.hn.d.valley.main.message.attachment;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/25 18:59
 * 修改人员：hewking
 * 修改时间：2017/04/25 18:59
 * 修改备注：
 * Version: 1.0.0
 */
public class RedPacket extends BaseCustomMsg {


    /**
     * msg : 红包祝福语
     * rid : 4343241312312324
     * created : 1435324645
     */

    private String msg;
    private long rid;
    private int created;

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
}

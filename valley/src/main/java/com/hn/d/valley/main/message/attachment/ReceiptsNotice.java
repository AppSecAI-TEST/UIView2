package com.hn.d.valley.main.message.attachment;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/02 14:53
 * 修改人员：hewking
 * 修改时间：2017/05/02 14:53
 * 修改备注：
 * Version: 1.0.0
 */
public class ReceiptsNotice  extends BaseCustomMsg{

    /**
     * msg : 收款通知
     * money : 100
     * created : 1492069482
     * id : 7710083190358017
     * uid : 60006
     * to_uid : 60012
     */

    private String msg;
    private int money;
    private int created;
    private String id;
    private int uid;
    private int to_uid;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getTo_uid() {
        return to_uid;
    }

    public void setTo_uid(int to_uid) {
        this.to_uid = to_uid;
    }
}

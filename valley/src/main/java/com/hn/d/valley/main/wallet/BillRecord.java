package com.hn.d.valley.main.wallet;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/28 10:56
 * 修改人员：hewking
 * 修改时间：2017/04/28 10:56
 * 修改备注：
 * Version: 1.0.0
 */
public class BillRecord {


    /**
     * id : 2
     * uid : 60006
     * type : 0
     * money : 48
     * description : 红包
     * created : 1490604689
     * payid :
     * out_payid :
     */

    private int id;
    private int uid;
    private int type;
    private int money;
    private String description;
    private int created;
    private String payid;
    private String out_payid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public String getPayid() {
        return payid;
    }

    public void setPayid(String payid) {
        this.payid = payid;
    }

    public String getOut_payid() {
        return out_payid;
    }

    public void setOut_payid(String out_payid) {
        this.out_payid = out_payid;
    }
}

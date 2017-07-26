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
     * id : 834
     * uid : 60006
     * type : 1
     * sub_type : 2
     * money : 10
     * extend : {"into_account":100,"poundage":1}
     * transferway : 支付宝(135***593)
     * description : 提现
     * created : 1496654803
     * payid : 26942316174376960
     * out_payid :
     * remain : 499997833
     * cashout_time : 1496654963
     */

    private int id;
    private int uid;
    private int type;
    private int sub_type;
    private int money;
    private String extend;
    private String transferway;
    private String description;
    private int created;
    private String payid;
    private String out_payid;
    private int remain;
    private int cashout_time;

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

    public int getSub_type() {
        return sub_type;
    }

    public void setSub_type(int sub_type) {
        this.sub_type = sub_type;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public String getTransferway() {
        return transferway;
    }

    public void setTransferway(String transferway) {
        this.transferway = transferway;
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

    public int getRemain() {
        return remain;
    }

    public void setRemain(int remain) {
        this.remain = remain;
    }

    public int getCashout_time() {
        return cashout_time;
    }

    public void setCashout_time(int cashout_time) {
        this.cashout_time = cashout_time;
    }
}

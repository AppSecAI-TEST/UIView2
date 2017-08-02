package com.hn.d.valley.bean;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：cjh
 * 创建时间：2017/7/28
 * 修改人员：cjh
 * 修改时间：2017/7/28
 * 修改备注：
 * Version: 1.0.0
 */
public class BillRecordDetailBean {

    /**
     * id : 45393273585401856
     * uid : 62300
     * type : 1
     * sub_type : 4
     * extend : dragon_coin
     * money : 600
     * transferway :
     * description : 购买龙币
     * created : 1501053854
     * payid : 45393273585401856
     * out_payid :
     * remain : 1347
     * cashout_time : 0
     */

    private long id;
    private int uid;
    private int type;
    private int sub_type;
    private String extend;
    private int money;
    private String transferway;
    private String description;


    private int created;
    private String payid;
    private String out_payid;
    private int remain;
    private int cashout_time;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
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

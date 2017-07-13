package com.hn.d.valley.main.message.attachment;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：cjh
 * 创建时间：2017/7/12
 * 修改人员：cjh
 * 修改时间：2017/7/12
 * 修改备注：
 * Version: 1.0.0
 */
public class WithDrawalFailMsg extends BaseCustomMsg {


    /**
     * msg : 提现失败
     * reason : 账号不存在
     * money : 200
     * account_type : 0
     * account : 13888888888
     * cashoutid : 13
     */

    private String msg;
    private String reason;
    private int money;
    private int account_type;
    private String account;
    private String cashoutid;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getAccount_type() {
        return account_type;
    }

    public void setAccount_type(int account_type) {
        this.account_type = account_type;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCashoutid() {
        return cashoutid;
    }

    public void setCashoutid(String cashoutid) {
        this.cashoutid = cashoutid;
    }
}

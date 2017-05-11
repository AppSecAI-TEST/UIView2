package com.hn.d.valley.main.message.attachment;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/09 15:34
 * 修改人员：hewking
 * 修改时间：2017/05/09 15:34
 * 修改备注：
 * Version: 1.0.0
 */
public class WithDrawalMsg extends BaseCustomMsg {


    /**
     * msg : 提现失败
     * caccounting_date : 142983729
     * money : 200
     * account_type : 0
     * account : 13888888888
     * cashoutid : 13
     */

    private String msg;
    private int caccounting_date;
    private int money;
    private int account_type;
    private String account;
    private int cashoutid;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCaccounting_date() {
        return caccounting_date;
    }

    public void setCaccounting_date(int caccounting_date) {
        this.caccounting_date = caccounting_date;
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

    public int getCashoutid() {
        return cashoutid;
    }

    public void setCashoutid(int cashoutid) {
        this.cashoutid = cashoutid;
    }
}

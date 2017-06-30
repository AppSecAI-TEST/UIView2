package com.hn.d.valley.main.message.attachment;

/**
 * Created by cjh on 2017/6/30.
 */

public class RechargeMsg extends BaseCustomMsg{

    /**
     * msg : 充值成功
     * money : 1
     * created : 1498786782
     * account : 604***@qq.com
     * rechargeid : 35884451858743296
     */

    private String msg;
    private int money;
    private int created;
    private String account;
    private String rechargeid;

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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getRechargeid() {
        return rechargeid;
    }

    public void setRechargeid(String rechargeid) {
        this.rechargeid = rechargeid;
    }
}

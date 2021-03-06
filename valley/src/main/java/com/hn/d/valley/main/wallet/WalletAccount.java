package com.hn.d.valley.main.wallet;

import android.text.TextUtils;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/28 13:53
 * 修改人员：hewking
 * 修改时间：2017/04/28 13:53
 * 修改备注：
 * Version: 1.0.0
 */
public class WalletAccount {


    /**
     * uid : 60006
     * money : 8858
     * freeze : 530
     * alipay : 13560487593;;;宋亮
     * wechatpay :
     * has_pin : 1
     * fingerprint : 0
     * device : 1ASD23123SFESF2343XCVDGDFGFG3434
     * "cashout_rate":0.0055
     "cashout_doorsill":1000
     alipay_userid
     */

    private int uid;

    public String getAlipay_userid() {
        return alipay_userid;
    }

    public void setAlipay_userid(String alipay_userid) {
        this.alipay_userid = alipay_userid;
    }

    private int money;
    private int freeze;
    private String alipay;
    private String wechatpay;
    private int has_pin;
    private String alipay_userid;

    public float getCashout_rate() {
        return cashout_rate;
    }

    public void setCashout_rate(float cashout_rate) {
        this.cashout_rate = cashout_rate;
    }

    private int fingerprint;
    private String device;

    public int getCashout_doorsill() {
        return cashout_doorsill;
    }

    public void setCashout_doorsill(int cashout_doorsill) {
        this.cashout_doorsill = cashout_doorsill;
    }

    private float cashout_rate;
    private int cashout_doorsill;

    public boolean hasPin() {
        // 1 已设置 0 未设置
        return has_pin == 1;
    }

    public int bindType() {
        if (!TextUtils.isEmpty(alipay_userid)) {
            return 1;
        } else {
            return 0;
        }
    }

    public boolean hasAlipay() {
        return !TextUtils.isEmpty(alipay) || !TextUtils.isEmpty(alipay_userid);
    }

    public boolean hasMoney() {
        return !(money == 0);
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getFreeze() {
        return freeze;
    }

    public void setFreeze(int freeze) {
        this.freeze = freeze;
    }

    public String getAlipay() {
        return alipay;
    }

    public void setAlipay(String alipay) {
        this.alipay = alipay;
    }

    public String getWechatpay() {
        return wechatpay;
    }

    public void setWechatpay(String wechatpay) {
        this.wechatpay = wechatpay;
    }

    public int getHas_pin() {
        return has_pin;
    }

    public void setHas_pin(int has_pin) {
        this.has_pin = has_pin;
    }

    public int getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(int fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}

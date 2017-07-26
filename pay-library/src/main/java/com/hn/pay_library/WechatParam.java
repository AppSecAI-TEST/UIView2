package com.hn.pay_library;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：cjh
 * 创建时间：2017/7/25
 * 修改人员：cjh
 * 修改时间：2017/7/25
 * 修改备注：
 * Version: 1.0.0
 */
public class WechatParam {

    /**
     * partnerid : 1431966102
     * prepayid : wx201707251647333d7950956c0079739755
     * noncestr : 2c6a75f67459efa1942446210d5fd090
     * timestamp : 1500972453
     * sign : 78D47403F860F5E4671FDCC427AB01E0
     */

    private String partnerid;
    private String prepayid;
    private String noncestr;
    private int timestamp;
    private String sign;

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}

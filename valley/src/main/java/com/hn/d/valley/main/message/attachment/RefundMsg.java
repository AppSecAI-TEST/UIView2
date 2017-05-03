package com.hn.d.valley.main.message.attachment;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/02 15:27
 * 修改人员：hewking
 * 修改时间：2017/05/02 15:27
 * 修改备注：
 * Version: 1.0.0
 */
public class RefundMsg extends BaseCustomMsg {


    /**
     * msg : 红包退还通知
     * accounting_data : 1491969252
     * money : 200
     * refund_way : 0
     * reason : 红包超过24小时未被领取
     * transferid : 7289689132236803
     * remark : 了解详细信息，可查看钱包明细
     * extend : {"to_gid":0,"to_uid":50015}
     */

    private String msg;
    private int accounting_data;
    private int money;
    private int refund_way;
    private String reason;
    private String transferid;
    private String remark;
    private ExtendBean extend;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getAccounting_data() {
        return accounting_data;
    }

    public void setAccounting_data(int accounting_data) {
        this.accounting_data = accounting_data;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getRefund_way() {
        return refund_way;
    }

    public void setRefund_way(int refund_way) {
        this.refund_way = refund_way;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTransferid() {
        return transferid;
    }

    public void setTransferid(String transferid) {
        this.transferid = transferid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public ExtendBean getExtend() {
        return extend;
    }

    public void setExtend(ExtendBean extend) {
        this.extend = extend;
    }

    public static class ExtendBean {
        /**
         * to_gid : 0
         * to_uid : 50015
         */

        private int to_gid;
        private int to_uid;

        public int getTo_gid() {
            return to_gid;
        }

        public void setTo_gid(int to_gid) {
            this.to_gid = to_gid;
        }

        public int getTo_uid() {
            return to_uid;
        }

        public void setTo_uid(int to_uid) {
            this.to_uid = to_uid;
        }
    }
}

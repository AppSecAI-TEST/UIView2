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
public class ConsumeMsg extends BaseCustomMsg {

    /**
     * msg : 支付成功
     * money : 200
     * goods : dragon_coin
     * created : 1498099181
     * transferid : 24143123123123
     * extend : {"coins":10}
     */

    private String msg;
    private int money;
    private String goods;
    private int created;
    private String transferid;
    private ExtendBean extend;

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

    public String getGoods() {
        return goods;
    }

    public void setGoods(String goods) {
        this.goods = goods;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public String getTransferid() {
        return transferid;
    }

    public void setTransferid(String transferid) {
        this.transferid = transferid;
    }

    public ExtendBean getExtend() {
        return extend;
    }

    public void setExtend(ExtendBean extend) {
        this.extend = extend;
    }

    public static class ExtendBean {
        /**
         * coins : 10
         */

        private int coins;

        public int getCoins() {
            return coins;
        }

        public void setCoins(int coins) {
            this.coins = coins;
        }
    }
}

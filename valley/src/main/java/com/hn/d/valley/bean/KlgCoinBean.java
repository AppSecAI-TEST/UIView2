package com.hn.d.valley.bean;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：cjh
 * 创建时间：2017/7/4
 * 修改人员：cjh
 * 修改时间：2017/7/4
 * 修改备注：
 * Version: 1.0.0
 */
public class KlgCoinBean {


    /**
     * action : 1005
     * action_desc : 充值
     * created : 1498030176
     * value : 60
     * in_out : 1
     * detail : {"money":"600","platform":"1","pay_id":"1","pay_time":"123332234","donate":"1","coin":"60"}
     */

    private String action;
    private String action_desc;
    private String created;
    private String value;
    private String in_out;
    private DetailBean detail;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction_desc() {
        return action_desc;
    }

    public void setAction_desc(String action_desc) {
        this.action_desc = action_desc;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getIn_out() {
        return in_out;
    }

    public void setIn_out(String in_out) {
        this.in_out = in_out;
    }

    public DetailBean getDetail() {
        return detail;
    }

    public void setDetail(DetailBean detail) {
        this.detail = detail;
    }

    public static class DetailBean {
        /**
         * money : 600
         * platform : 1
         * pay_id : 1
         * pay_time : 123332234
         * donate : 1
         * coin : 60
         */

        private String money;
        private String platform;
        private String pay_id;
        private String pay_time;
        private String donate;
        private String coin;

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getPay_id() {
            return pay_id;
        }

        public void setPay_id(String pay_id) {
            this.pay_id = pay_id;
        }

        public String getPay_time() {
            return pay_time;
        }

        public void setPay_time(String pay_time) {
            this.pay_time = pay_time;
        }

        public String getDonate() {
            return donate;
        }

        public void setDonate(String donate) {
            this.donate = donate;
        }

        public String getCoin() {
            return coin;
        }

        public void setCoin(String coin) {
            this.coin = coin;
        }
    }
}

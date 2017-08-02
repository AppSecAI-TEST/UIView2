package com.hn.d.valley.bean;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/08/02 17:39
 * 修改人员：Robi
 * 修改时间：2017/08/02 17:39
 * 修改备注：
 * Version: 1.0.0
 */
public class RewardModel {

    private List<DataListBean> data_list;

    public List<DataListBean> getData_list() {
        return data_list;
    }

    public void setData_list(List<DataListBean> data_list) {
        this.data_list = data_list;
    }

    public static class DataListBean {
        /**
         * type : 2
         * created : 1501663670
         * user_info : {"uid":"60003","sex":"2","grade":"1","username":"哈哈","avatar":"http://avatorimg.klgwl.com/60003/1501242681593_s_414.00x414.00.png"}
         * package_info : {"money":"1"}
         * gift_info : {"id":"25","name":"皇冠","thumb":"http://avatorimg.klgwl.com/13/1313.png"}
         */

        private String type;
        private String created;
        private UserInfoBean user_info;
        private PackageInfoBean package_info;
        private GiftBean gift_info;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }

        public UserInfoBean getUser_info() {
            return user_info;
        }

        public void setUser_info(UserInfoBean user_info) {
            this.user_info = user_info;
        }

        public PackageInfoBean getPackage_info() {
            return package_info;
        }

        public void setPackage_info(PackageInfoBean package_info) {
            this.package_info = package_info;
        }

        public GiftBean getGift_info() {
            return gift_info;
        }

        public void setGift_info(GiftBean gift_info) {
            this.gift_info = gift_info;
        }

        public static class UserInfoBean {
            /**
             * uid : 60003
             * sex : 2
             * grade : 1
             * username : 哈哈
             * avatar : http://avatorimg.klgwl.com/60003/1501242681593_s_414.00x414.00.png
             */

            private String uid;
            private String sex;
            private String grade;
            private String username;
            private String avatar;

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getSex() {
                return sex;
            }

            public void setSex(String sex) {
                this.sex = sex;
            }

            public String getGrade() {
                return grade;
            }

            public void setGrade(String grade) {
                this.grade = grade;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }
        }

        public static class PackageInfoBean {
            /**
             * money : 1
             */

            private String money;

            public String getMoney() {
                return money;
            }

            public void setMoney(String money) {
                this.money = money;
            }
        }
    }
}

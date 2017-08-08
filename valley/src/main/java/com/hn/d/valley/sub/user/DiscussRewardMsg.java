package com.hn.d.valley.sub.user;

import com.hn.d.valley.main.message.attachment.BaseCustomMsg;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：cjh
 * 创建时间：2017/8/7
 * 修改人员：cjh
 * 修改时间：2017/8/7
 * 修改备注：
 * Version: 1.0.0
 */
public class DiscussRewardMsg extends BaseCustomMsg {

    /**
     * msg : 自由如风打赏了0.03元红包
     * created : 1502076117
     * uid : 60058
     * avatar : http://klg-useravator.oss-cn-shenzhen.aliyuncs.com/abcdefg/c53d2e62d255a65ee5918d8fe4ad4034.jpg
     * username : 自由如风
     * discuss_id : 6543
     * type : 2
     * package_info : {"money":"3"}
     */

    private String msg;
    private int created;
    private String uid;
    private String avatar;
    private String username;
    private String discuss_id;
    private int type;
    private PackageInfoBean package_info;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDiscuss_id() {
        return discuss_id;
    }

    public void setDiscuss_id(String discuss_id) {
        this.discuss_id = discuss_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public PackageInfoBean getPackage_info() {
        return package_info;
    }

    public void setPackage_info(PackageInfoBean package_info) {
        this.package_info = package_info;
    }

    public static class PackageInfoBean {
        /**
         * money : 3
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

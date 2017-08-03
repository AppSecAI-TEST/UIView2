package com.hn.d.valley.bean;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：cjh
 * 创建时间：2017/8/2
 * 修改人员：cjh
 * 修改时间：2017/8/2
 * 修改备注：
 * Version: 1.0.0
 */
public class GiftRecordBean {

    /**
     * gift_id : 37
     * uid : 60001
     * created : 1501315429
     * charm : 0
     * thumb : http://avatorimg.klgwl.com/13/13665.png
     * name : 泰迪熊
     * user_info : {"avatar":"http://avatorimg.klgwl.com/60001/150105659775_s_375.00x375.00.png","username":"汤命圳"}
     */

    private String gift_id;
    private String uid;
    private String created;
    private String charm;
    private String thumb;
    private String name;
    private UserInfoBean user_info;

    public String getGift_id() {
        return gift_id;
    }

    public void setGift_id(String gift_id) {
        this.gift_id = gift_id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCharm() {
        return charm;
    }

    public void setCharm(String charm) {
        this.charm = charm;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserInfoBean getUser_info() {
        return user_info;
    }

    public void setUser_info(UserInfoBean user_info) {
        this.user_info = user_info;
    }

    public static class UserInfoBean {
        /**
         * avatar : http://avatorimg.klgwl.com/60001/150105659775_s_375.00x375.00.png
         * username : 汤命圳
         */

        private String avatar;
        private String username;

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
    }
}
